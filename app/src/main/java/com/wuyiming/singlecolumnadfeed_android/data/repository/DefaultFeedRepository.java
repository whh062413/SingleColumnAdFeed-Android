package com.wuyiming.singlecolumnadfeed_android.data.repository;

import android.content.Context;

import com.wuyiming.singlecolumnadfeed_android.data.local.InteractionStore;
import com.wuyiming.singlecolumnadfeed_android.data.mock.MockDataSource;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultFeedRepository implements FeedRepository {
    private List<FeedItem> allItems;
    private final InteractionStore interactionStore;

    private static volatile DefaultFeedRepository instance;

    public static DefaultFeedRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (DefaultFeedRepository.class) {
                if (instance == null) {
                    instance = new DefaultFeedRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private DefaultFeedRepository(Context context) {
        interactionStore = InteractionStore.getInstance(context);
        allItems = MockDataSource.generateFeedItems(100);
        syncLikedState();
    }

    @Override
    public void reloadData() {
        allItems = MockDataSource.generateFeedItems(100);
        syncLikedState();
    }

    private void syncLikedState() {
        for (FeedItem item : allItems) {
            String id = item.getId();
            if (interactionStore.hasInteraction(id)) {
                item.setLiked(interactionStore.isLiked(id));
                item.setCollected(interactionStore.isCollected(id));
            }
        }
    }

    @Override
    public List<FeedItem> getAllItems() {
        return new ArrayList<>(allItems);
    }

    @Override
    public List<FeedItem> getItemsByCategory(FeedCategory category) {
        return allItems.stream()
                .filter(item -> item.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedItem> loadMore(int page, int pageSize, FeedCategory category) {
        List<FeedItem> source = category == null ? allItems : getItemsByCategory(category);
        int start = (page - 1) * pageSize;
        if (start >= source.size()) return new ArrayList<>();
        int end = Math.min(start + pageSize, source.size());
        return new ArrayList<>(source.subList(start, end));
    }

    @Override
    public void toggleLike(String feedId) {
        boolean newLiked = !interactionStore.isLiked(feedId);
        interactionStore.setLiked(feedId, newLiked);

        for (FeedItem item : allItems) {
            if (item.getId().equals(feedId)) {
                item.setLiked(newLiked);
                break;
            }
        }
    }

    @Override
    public void toggleCollect(String feedId) {
        boolean newCollected = !interactionStore.isCollected(feedId);
        interactionStore.setCollected(feedId, newCollected);

        for (FeedItem item : allItems) {
            if (item.getId().equals(feedId)) {
                item.setCollected(newCollected);
                break;
            }
        }
    }

    @Override
    public FeedItem getItemById(String feedId) {
        return allItems.stream()
                .filter(item -> item.getId().equals(feedId))
                .findFirst()
                .orElse(null);
    }
}