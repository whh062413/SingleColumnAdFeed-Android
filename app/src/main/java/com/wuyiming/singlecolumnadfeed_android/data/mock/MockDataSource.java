package com.wuyiming.singlecolumnadfeed_android.data.mock;

import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;

import java.util.List;

/**
 * Facade over MockFeedDataSource for cleaner access.
 */
public class MockDataSource {

    public static List<FeedItem> generateFeedItems(int count) {
        return MockFeedDataSource.generateFeedItems(count);
    }
}
