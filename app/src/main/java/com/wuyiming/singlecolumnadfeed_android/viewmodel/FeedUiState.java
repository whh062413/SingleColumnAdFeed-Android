package com.wuyiming.singlecolumnadfeed_android.viewmodel;

import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;

import java.util.List;

public class FeedUiState {
    private final boolean loading;
    private final boolean refreshing;
    private final boolean loadingMore;
    private final List<FeedItem> items;
    private final FeedCategory currentCategory;
    private final String error;
    private final boolean hasMore;

    private FeedUiState(Builder builder) {
        this.loading = builder.loading;
        this.refreshing = builder.refreshing;
        this.loadingMore = builder.loadingMore;
        this.items = builder.items;
        this.currentCategory = builder.currentCategory;
        this.error = builder.error;
        this.hasMore = builder.hasMore;
    }

    public boolean isLoading() { return loading; }
    public boolean isRefreshing() { return refreshing; }
    public boolean isLoadingMore() { return loadingMore; }
    public List<FeedItem> getItems() { return items; }
    public FeedCategory getCurrentCategory() { return currentCategory; }
    public String getError() { return error; }
    public boolean hasMore() { return hasMore; }

    public static class Builder {
        private boolean loading;
        private boolean refreshing;
        private boolean loadingMore;
        private List<FeedItem> items;
        private FeedCategory currentCategory = FeedCategory.RECOMMEND;
        private String error;
        private boolean hasMore = true;

        public Builder loading(boolean loading) { this.loading = loading; return this; }
        public Builder refreshing(boolean refreshing) { this.refreshing = refreshing; return this; }
        public Builder loadingMore(boolean loadingMore) { this.loadingMore = loadingMore; return this; }
        public Builder items(List<FeedItem> items) { this.items = items; return this; }
        public Builder currentCategory(FeedCategory category) { this.currentCategory = category; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder hasMore(boolean hasMore) { this.hasMore = hasMore; return this; }

        public FeedUiState build() { return new FeedUiState(this); }
    }
}
