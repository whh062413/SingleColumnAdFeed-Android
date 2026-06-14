package com.wuyiming.singlecolumnadfeed_android.data.repository;

import android.content.Context;
import com.wuyiming.singlecolumnadfeed_android.ai.AiInsightGenerator;
import com.wuyiming.singlecolumnadfeed_android.data.local.FeedInteractionStore;
import com.wuyiming.singlecolumnadfeed_android.data.mock.MockFeedDataSource;
import com.wuyiming.singlecolumnadfeed_android.data.model.AdInsight;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.coroutines.Continuation;

public class DefaultFeedRepository implements FeedRepository {

    private final FeedInteractionStore interactionStore;
    private List<FeedItem> allItems = Collections.emptyList();
    public volatile AiInsightGenerator aiInsightGenerator = null;

    public DefaultFeedRepository(Context context) {
        this(context, new FeedInteractionStore(context));
    }

    public DefaultFeedRepository(Context context, FeedInteractionStore interactionStore) {
        this.interactionStore = interactionStore;
        this.allItems = restorePersistedInteractions(MockFeedDataSource.generateFeedItems(100));
    }

    @Override
    public Object loadFeedItems(int page, int pageSize, int refreshSeed, Continuation<? super List<FeedItem>> completion) {
        if (page == 1) {
            allItems = restorePersistedInteractions(MockFeedDataSource.generateFeedItems(100));
        }
        int start = (page - 1) * pageSize;
        if (start >= allItems.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + pageSize, allItems.size());
        return allItems.subList(start, end);
    }

    @Override
    public Object generateAiInsight(FeedItem item, Continuation<? super AdInsight> completion) {
        AiInsightGenerator gen = aiInsightGenerator;
        if (gen == null) return null;
        if (item.getInsight() != null && item.getInsight().getSummary() != null
                && !item.getInsight().getSummary().isEmpty()) {
            return null;
        }
        try {
            return gen.generate(item.getTitle(), item.getDescription());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public FeedItem toggleLike(FeedItem item) {
        boolean nextLiked = !item.isLiked();
        interactionStore.setLiked(item.getId(), nextLiked);
        allItems = mapItems(allItems, it -> {
            if (it.getId().equals(item.getId())) {
                return it.toBuilder()
                        .isLiked(nextLiked)
                        .likesCount(nextLiked ? it.getLikesCount() + 1 : Math.max(0, it.getLikesCount() - 1))
                        .build();
            }
            return it;
        });
        return item.toBuilder()
                .isLiked(nextLiked)
                .likesCount(nextLiked ? item.getLikesCount() + 1 : Math.max(0, item.getLikesCount() - 1))
                .build();
    }

    @Override
    public FeedItem toggleCollect(FeedItem item) {
        boolean nextCollected = !item.isCollected();
        interactionStore.setCollected(item.getId(), nextCollected);
        allItems = mapItems(allItems, it -> {
            if (it.getId().equals(item.getId())) {
                return it.toBuilder().isCollected(nextCollected).build();
            }
            return it;
        });
        return item.toBuilder().isCollected(nextCollected).build();
    }

    @Override
    public FeedItem getItemById(String feedId) {
        for (FeedItem item : allItems) {
            if (item.getId().equals(feedId)) {
                return item;
            }
        }
        return null;
    }

    private List<FeedItem> restorePersistedInteractions(List<FeedItem> items) {
        return mapItems(items, item -> {
            boolean restoredLiked = interactionStore.hasLikeOverride(item.getId())
                    ? interactionStore.isLiked(item.getId())
                    : item.isLiked();
            boolean restoredCollected = interactionStore.hasCollectOverride(item.getId())
                    ? interactionStore.isCollected(item.getId())
                    : item.isCollected();
            int likesCount = item.getLikesCount();
            if (restoredLiked && !item.isLiked()) {
                likesCount = item.getLikesCount() + 1;
            } else if (!restoredLiked && item.isLiked()) {
                likesCount = Math.max(0, item.getLikesCount() - 1);
            }
            return item.toBuilder()
                    .isLiked(restoredLiked)
                    .isCollected(restoredCollected)
                    .likesCount(likesCount)
                    .build();
        });
    }

    private static List<FeedItem> mapItems(List<FeedItem> source, ItemMapper mapper) {
        List<FeedItem> result = new ArrayList<>(source.size());
        for (FeedItem item : source) {
            result.add(mapper.map(item));
        }
        return result;
    }

    private interface ItemMapper {
        FeedItem map(FeedItem item);
    }
}
