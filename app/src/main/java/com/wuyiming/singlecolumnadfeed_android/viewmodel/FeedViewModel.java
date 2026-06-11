package com.wuyiming.singlecolumnadfeed_android.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wuyiming.singlecolumnadfeed_android.ai.AiInsightGenerator;
import com.wuyiming.singlecolumnadfeed_android.ai.AiSearchService;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;
import com.wuyiming.singlecolumnadfeed_android.data.repository.DefaultFeedRepository;
import com.wuyiming.singlecolumnadfeed_android.data.repository.FeedRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FeedViewModel extends AndroidViewModel {
    private static final int PAGE_SIZE = 20;
    private static final long MIN_REFRESH_ANIMATION_MS = 600L;

    private final FeedRepository repository;
    // Three dedicated executors: refresh has its own, never blocked by AI
    private final ExecutorService dataExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService searchExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService refreshExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<FeedUiState> uiState = new MutableLiveData<>();

    private int currentPage = 1;
    private FeedCategory currentCategory = FeedCategory.RECOMMEND;
    private List<FeedItem> allLoadedItems = new ArrayList<>();

    private volatile AiInsightGenerator aiGenerator;
    private volatile AiSearchService aiSearchService;
    private final AtomicInteger searchGeneration = new AtomicInteger(0);
    private final MutableLiveData<String> likeSyncEvent = new MutableLiveData<>();

    private static volatile FeedViewModel sharedInstance;

    public static FeedViewModel getSharedInstance(Application app) {
        if (sharedInstance == null) {
            synchronized (FeedViewModel.class) {
                if (sharedInstance == null) {
                    sharedInstance = new FeedViewModel(app);
                    sharedInstance.init();
                }
            }
        }
        return sharedInstance;
    }

    public static FeedViewModel getInstance(Application app) {
        return getSharedInstance(app);
    }

    public FeedViewModel(@NonNull Application application) {
        super(application);
        repository = DefaultFeedRepository.getInstance(application);
    }

    public void configureAi(String apiKey, String apiUrl, String model) {
        this.aiGenerator = new AiInsightGenerator(apiKey, apiUrl, model);
        this.aiSearchService = new AiSearchService(apiKey, apiUrl, model);
        if (!allLoadedItems.isEmpty()) {
            generateInsightsFor(allLoadedItems);
        }
    }

    private void init() {
        uiState.setValue(new FeedUiState.Builder()
                .loading(true)
                .items(new ArrayList<>())
                .currentCategory(currentCategory)
                .build());
        loadFirstPage();
    }

    private void loadFirstPage() {
        dataExecutor.execute(() -> {
            repository.reloadData();
            allLoadedItems.clear();
            currentPage = 1;
            List<FeedItem> items = repository.loadMore(currentPage, PAGE_SIZE, currentCategory);
            allLoadedItems.addAll(items);
            postFeedState(items.size() == PAGE_SIZE);
            generateInsightsFor(items);
        });
    }

    /**
     * Pull-to-refresh — runs on its own executor so it is never blocked
     * by ongoing AI generation in dataExecutor.
     */
    public void refresh() {
        final long startTime = System.currentTimeMillis();

        uiState.setValue(new FeedUiState.Builder()
                .refreshing(true)
                .items(new ArrayList<>(allLoadedItems))
                .currentCategory(currentCategory)
                .isSearchMode(false)
                .build());

        refreshExecutor.execute(() -> {
            repository.reloadData();
            allLoadedItems.clear();
            currentPage = 1;
            List<FeedItem> items = repository.loadMore(currentPage, PAGE_SIZE, currentCategory);
            allLoadedItems.addAll(items);

            long elapsed = System.currentTimeMillis() - startTime;
            long delay = Math.max(0, MIN_REFRESH_ANIMATION_MS - elapsed);

            mainHandler.postDelayed(() -> {
                uiState.setValue(new FeedUiState.Builder()
                        .loading(false)
                        .refreshing(false)
                        .items(new ArrayList<>(allLoadedItems))
                        .currentCategory(currentCategory)
                        .hasMore(items.size() == PAGE_SIZE)
                        .isSearchMode(false)
                        .build());
            }, delay);

            // Start AI generation on the data executor (may queue behind existing work)
            generateInsightsFor(items);
        });
    }

    public void loadMore() {
        dataExecutor.execute(() -> {
            uiState.postValue(new FeedUiState.Builder()
                    .loadingMore(true)
                    .items(new ArrayList<>(allLoadedItems))
                    .currentCategory(currentCategory)
                    .isSearchMode(false)
                    .build());

            currentPage++;
            List<FeedItem> items = repository.loadMore(currentPage, PAGE_SIZE, currentCategory);
            allLoadedItems.addAll(items);
            postFeedState(items.size() == PAGE_SIZE);
            generateInsightsFor(items);
        });
    }

    public void switchCategory(FeedCategory category) {
        if (currentCategory == category) return;
        currentCategory = category;
        searchGeneration.incrementAndGet();
        loadFirstPage();
    }

    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            clearSearch();
            return;
        }

        final String trimmed = query.trim();
        final int gen = searchGeneration.incrementAndGet();

        uiState.setValue(new FeedUiState.Builder()
                .loading(false)
                .items(new ArrayList<>(allLoadedItems))
                .currentCategory(currentCategory)
                .isSearching(true)
                .build());

        searchExecutor.execute(() -> {
            if (searchGeneration.get() != gen) return;

            List<FeedItem> snapshot = new ArrayList<>(allLoadedItems);

            List<String> keywords;
            AiSearchService searchService = aiSearchService;
            if (searchService != null) {
                keywords = searchService.extractKeywords(trimmed);
            } else {
                keywords = splitLocal(trimmed);
            }

            if (searchGeneration.get() != gen) return;

            List<FeedItem> results = new ArrayList<>();
            for (FeedItem item : snapshot) {
                if (matchesKeywords(item, keywords)) {
                    results.add(item);
                }
            }

            if (searchGeneration.get() != gen) return;

            uiState.postValue(new FeedUiState.Builder()
                    .loading(false)
                    .isSearching(false)
                    .items(results)
                    .currentCategory(currentCategory)
                    .hasMore(false)
                    .isSearchMode(true)
                    .build());
        });
    }

    public void clearSearch() {
        searchGeneration.incrementAndGet();
        uiState.setValue(new FeedUiState.Builder()
                .loading(false)
                .isSearching(false)
                .items(new ArrayList<>(allLoadedItems))
                .currentCategory(currentCategory)
                .hasMore(true)
                .isSearchMode(false)
                .build());
    }

    private List<String> splitLocal(String query) {
        List<String> keywords = new ArrayList<>();
        for (String part : query.split("[\\s,.!?;:，。！？；：]+")) {
            if (part.length() >= 1) keywords.add(part);
        }
        if (keywords.isEmpty()) keywords.add(query);
        return keywords;
    }

    private boolean matchesKeywords(FeedItem item, List<String> keywords) {
        String title = item.getTitle() != null ? item.getTitle().toLowerCase() : "";
        String desc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
        StringBuilder tagStr = new StringBuilder();
        if (item.getInsight() != null && item.getInsight().getTags() != null) {
            for (String tag : item.getInsight().getTags()) {
                tagStr.append(tag.toLowerCase());
            }
        }
        String combined = title + " " + desc + " " + tagStr;

        for (String kw : keywords) {
            if (combined.contains(kw.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void postFeedState(boolean hasMore) {
        uiState.postValue(new FeedUiState.Builder()
                .loading(false)
                .loadingMore(false)
                .items(new ArrayList<>(allLoadedItems))
                .currentCategory(currentCategory)
                .hasMore(hasMore)
                .isSearchMode(false)
                .build());
    }

    public void toggleLike(String feedId) {
        dataExecutor.execute(() -> {
            repository.toggleLike(feedId);
            uiState.postValue(new FeedUiState.Builder()
                    .loading(false)
                    .items(new ArrayList<>(allLoadedItems))
                    .currentCategory(currentCategory)
                    .hasMore(true)
                    .build());
            likeSyncEvent.postValue(feedId);
        });
    }

    public void toggleCollect(String feedId) {
        dataExecutor.execute(() -> {
            repository.toggleCollect(feedId);
            uiState.postValue(new FeedUiState.Builder()
                    .loading(false)
                    .items(new ArrayList<>(allLoadedItems))
                    .currentCategory(currentCategory)
                    .hasMore(true)
                    .build());
        });
    }

    public FeedItem getItemById(String feedId) {
        for (FeedItem item : allLoadedItems) {
            if (item.getId().equals(feedId)) return item;
        }
        return repository.getItemById(feedId);
    }

    private void generateInsightsFor(List<FeedItem> items) {
        AiInsightGenerator gen = aiGenerator;
        if (gen == null || items == null || items.isEmpty()) return;

        dataExecutor.execute(() -> {
            for (FeedItem item : items) {
                if (item.getInsight() != null && item.getInsight().getSummary() != null) continue;
                try {
                    com.wuyiming.singlecolumnadfeed_android.data.model.AdInsight insight =
                            gen.generate(item.getTitle(), item.getDescription());
                    item.setInsight(insight);
                } catch (Exception e) {
                    // local fallback handled inside generator
                }
                postFeedState(true);
            }
        });
    }

    public LiveData<FeedUiState> getUiState() { return uiState; }
    public LiveData<String> getLikeSyncEvent() { return likeSyncEvent; }

    @Override
    protected void onCleared() {
        super.onCleared();
        dataExecutor.shutdown();
        searchExecutor.shutdown();
        refreshExecutor.shutdown();
    }
}