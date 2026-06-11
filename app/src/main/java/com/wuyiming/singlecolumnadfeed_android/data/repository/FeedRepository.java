package com.wuyiming.singlecolumnadfeed_android.data.repository;

import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;

import java.util.List;

public interface FeedRepository {
    List<FeedItem> getAllItems();
    List<FeedItem> getItemsByCategory(FeedCategory category);
    List<FeedItem> loadMore(int page, int pageSize, FeedCategory category);
    void reloadData();
    void toggleLike(String feedId);
    void toggleCollect(String feedId);
    FeedItem getItemById(String feedId);
}