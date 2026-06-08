package com.wuyiming.singlecolumnadfeed_android.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;
import com.wuyiming.singlecolumnadfeed_android.data.repository.DefaultFeedRepository;
import com.wuyiming.singlecolumnadfeed_android.data.repository.FeedRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedViewModel extends AndroidViewModel {
    private static final int PAGE_SIZE = 20;

    private final FeedRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<FeedUiState> uiState = new MutableLiveData<>();

    private int currentPage = 1;
    private FeedCategory currentCategory = FeedCategory.RECOMMEND;
    private List<FeedItem> allLoadedItems = new ArrayList<>();

    // Shared ViewModel pattern: flag to trigger UI refresh on like sync
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
        // For Compose, use factory; but for simplicity, reuse shared
        return getSharedInstance(app);
    }

    public FeedViewModel(@NonNull Application application) {
        super(application);
        repository = DefaultFeedRepository.getInstance(application);
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
        executor.execute(() -> {
            allLoadedItems.clear();
            currentPage = 1;
            List<FeedItem> items = repository.loadMore(currentPage, PAGE_SIZE, currentCategory);
            allLoadedItems.addAll(items);

            uiState.postValue(new FeedUiState.Builder()
                    .loading(false)
                    .items(new ArrayList<>(allLoadedItems))
                    .currentCategory(currentCategory)
                    .hasMore(items.size() == PAGE_SIZE)
                    .build());
        });
    }

    public void refresh() {
        executor.execute(() -> {
            uiState.postValue(new FeedUiState.Builder()
                    .refreshing(true)
                    .items(new ArrayList<>(allLoadedItems))
                    .currentCategory(currentCategory)
                    .build());

            allLoadedItems.clear();
            currentPage = 1;
            List<FeedItem> items = repository.loadMore(currentPage, PAGE_SIZE, currentCategory);
            allLoadedItems.addAll(items);

            uiState.postValue(new FeedUiState.Builder()
                    .loading(false)
                    .refreshing(false)
                    .items(new ArrayList<>(allLoadedItems))
                    .currentCategory(currentCategory)
                    .hasMore(items.size() == PAGE_SIZE)
                    .build());
        });
    }

    public void loadMore() {
        executor.execute(() -> {
            uiState.postValue(new FeedUiState.Builder()
                    .loadingMore(true)
                    .items(new ArrayList<>(allLoadedItems))
                    .currentCategory(currentCategory)
                    .build());

            currentPage++;
            List<FeedItem> items = repository.loadMore(currentPage, PAGE_SIZE, currentCategory);
            allLoadedItems.addAll(items);

            uiState.postValue(new FeedUiState.Builder()
                    .loading(false)
                    .loadingMore(false)
                    .items(new ArrayList<>(allLoadedItems))
                    .currentCategory(currentCategory)
                    .hasMore(items.size() == PAGE_SIZE)
                    .build());
        });
    }

    public void switchCategory(FeedCategory category) {
        if (currentCategory == category) return;
        currentCategory = category;
        loadFirstPage();
    }

    public void toggleLike(String feedId) {
        executor.execute(() -> {
            repository.toggleLike(feedId);
            // Update local list
            for (FeedItem item : allLoadedItems) {
                if (item.getId().equals(feedId)) {
                    item.setLiked(!item.isLiked());
                    break;
                }
            }
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
        executor.execute(() -> {
            repository.toggleCollect(feedId);
            for (FeedItem item : allLoadedItems) {
                if (item.getId().equals(feedId)) {
                    item.setCollected(!item.isCollected());
                    break;
                }
            }
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

    public LiveData<FeedUiState> getUiState() { return uiState; }
    public LiveData<String> getLikeSyncEvent() { return likeSyncEvent; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
