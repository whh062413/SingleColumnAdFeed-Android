package com.wuyiming.singlecolumnadfeed_android.data.model;

import java.util.Objects;

public final class FeedItem {
    private final String id;
    private final String title;
    private final String description;
    private final FeedItemType type;
    private final FeedCategory category;
    private final String coverUrl;
    private final String videoUrl;
    private final int likesCount;
    private final int commentsCount;
    private final boolean isLiked;
    private final boolean isCollected;
    private final AdInsight insight;
    private final long timestamp;

    public FeedItem(String id, String title, String description, FeedItemType type, FeedCategory category,
                    String coverUrl, String videoUrl, int likesCount, int commentsCount,
                    boolean isLiked, boolean isCollected, AdInsight insight, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.coverUrl = coverUrl;
        this.videoUrl = videoUrl;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.isLiked = isLiked;
        this.isCollected = isCollected;
        this.insight = insight;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public FeedItemType getType() { return type; }
    public FeedCategory getCategory() { return category; }
    public String getCoverUrl() { return coverUrl; }
    public String getVideoUrl() { return videoUrl; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public boolean isLiked() { return isLiked; }
    public boolean isCollected() { return isCollected; }
    public AdInsight getInsight() { return insight; }
    public long getTimestamp() { return timestamp; }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedItem)) return false;
        FeedItem that = (FeedItem) o;
        return likesCount == that.likesCount &&
               commentsCount == that.commentsCount &&
               isLiked == that.isLiked &&
               isCollected == that.isCollected &&
               timestamp == that.timestamp &&
               Objects.equals(id, that.id) &&
               Objects.equals(title, that.title) &&
               Objects.equals(description, that.description) &&
               type == that.type &&
               category == that.category &&
               Objects.equals(coverUrl, that.coverUrl) &&
               Objects.equals(videoUrl, that.videoUrl) &&
               Objects.equals(insight, that.insight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, type, category, coverUrl, videoUrl,
                           likesCount, commentsCount, isLiked, isCollected, insight, timestamp);
    }

    @Override
    public String toString() {
        return "FeedItem{id='" + id + "', title='" + title + "'}";
    }

    public static final class Builder {
        private String id;
        private String title;
        private String description;
        private FeedItemType type;
        private FeedCategory category;
        private String coverUrl;
        private String videoUrl;
        private int likesCount;
        private int commentsCount;
        private boolean isLiked;
        private boolean isCollected;
        private AdInsight insight;
        private long timestamp = System.currentTimeMillis();

        public Builder() {}

        public Builder(FeedItem source) {
            this.id = source.id;
            this.title = source.title;
            this.description = source.description;
            this.type = source.type;
            this.category = source.category;
            this.coverUrl = source.coverUrl;
            this.videoUrl = source.videoUrl;
            this.likesCount = source.likesCount;
            this.commentsCount = source.commentsCount;
            this.isLiked = source.isLiked;
            this.isCollected = source.isCollected;
            this.insight = source.insight;
            this.timestamp = source.timestamp;
        }

        public Builder id(String v) { this.id = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder type(FeedItemType v) { this.type = v; return this; }
        public Builder category(FeedCategory v) { this.category = v; return this; }
        public Builder coverUrl(String v) { this.coverUrl = v; return this; }
        public Builder videoUrl(String v) { this.videoUrl = v; return this; }
        public Builder likesCount(int v) { this.likesCount = v; return this; }
        public Builder commentsCount(int v) { this.commentsCount = v; return this; }
        public Builder isLiked(boolean v) { this.isLiked = v; return this; }
        public Builder isCollected(boolean v) { this.isCollected = v; return this; }
        public Builder insight(AdInsight v) { this.insight = v; return this; }
        public Builder timestamp(long v) { this.timestamp = v; return this; }

        public FeedItem build() {
            return new FeedItem(id, title, description, type, category, coverUrl, videoUrl,
                               likesCount, commentsCount, isLiked, isCollected, insight, timestamp);
        }
    }
}
