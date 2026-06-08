package com.wuyiming.singlecolumnadfeed_android.data.model;

import java.io.Serializable;

public class FeedItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String title;
    private final String description;
    private final String coverUrl;
    private final String videoUrl;
    private final FeedItemType feedItemType;
    private final FeedCategory category;
    private final int likeCount;
    private final int commentCount;
    private boolean isLiked;
    private boolean isCollected;
    private AdInsight insight;
    private final long timestamp;

    private FeedItem(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.coverUrl = builder.coverUrl;
        this.videoUrl = builder.videoUrl;
        this.feedItemType = builder.feedItemType;
        this.category = builder.category;
        this.likeCount = builder.likeCount;
        this.commentCount = builder.commentCount;
        this.isLiked = builder.isLiked;
        this.isCollected = builder.isCollected;
        this.insight = builder.insight;
        this.timestamp = builder.timestamp;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCoverUrl() { return coverUrl; }
    public String getVideoUrl() { return videoUrl; }
    public FeedItemType getFeedItemType() { return feedItemType; }
    public FeedCategory getCategory() { return category; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
    public boolean isLiked() { return isLiked; }
    public boolean isCollected() { return isCollected; }
    public AdInsight getInsight() { return insight; }
    public long getTimestamp() { return timestamp; }

    public void setLiked(boolean liked) { this.isLiked = liked; }
    public void setCollected(boolean collected) { this.isCollected = collected; }
    public void setInsight(AdInsight insight) { this.insight = insight; }

    public static class Builder {
        private String id;
        private String title;
        private String description;
        private String coverUrl;
        private String videoUrl;
        private FeedItemType feedItemType = FeedItemType.BIG_IMAGE;
        private FeedCategory category = FeedCategory.RECOMMEND;
        private int likeCount;
        private int commentCount;
        private boolean isLiked;
        private boolean isCollected;
        private AdInsight insight;
        private long timestamp = System.currentTimeMillis();

        public Builder id(String id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder coverUrl(String coverUrl) { this.coverUrl = coverUrl; return this; }
        public Builder videoUrl(String videoUrl) { this.videoUrl = videoUrl; return this; }
        public Builder feedItemType(FeedItemType type) { this.feedItemType = type; return this; }
        public Builder category(FeedCategory category) { this.category = category; return this; }
        public Builder likeCount(int count) { this.likeCount = count; return this; }
        public Builder commentCount(int count) { this.commentCount = count; return this; }
        public Builder isLiked(boolean liked) { this.isLiked = liked; return this; }
        public Builder isCollected(boolean collected) { this.isCollected = collected; return this; }
        public Builder insight(AdInsight insight) { this.insight = insight; return this; }
        public Builder timestamp(long ts) { this.timestamp = ts; return this; }

        public FeedItem build() { return new FeedItem(this); }
    }
}
