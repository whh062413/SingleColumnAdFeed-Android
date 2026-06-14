package com.wuyiming.singlecolumnadfeed_android.data.model;

public enum FeedCategory {
    RECOMMEND("推荐"),
    ECOMMERCE("电商"),
    LOCAL("本地");

    private final String title;

    FeedCategory(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
