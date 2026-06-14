package com.wuyiming.singlecolumnadfeed_android.data.mock;

import com.wuyiming.singlecolumnadfeed_android.data.model.AdInsight;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem;
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItemType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MockFeedDataSource {

    private static final String PACKAGE_NAME = "com.wuyiming.singlecolumnadfeed_android";

    private static final List<String> COVER_IMAGES = Arrays.asList(
            "ad_cover_1", "ad_cover_2", "ad_cover_3", "ad_cover_4", "ad_cover_5",
            "ad_cover_6", "ad_cover_7", "ad_cover_8", "ad_cover_9", "ad_cover_10"
    );

    private static final List<String> VIDEO_URLS = Arrays.asList(
            "https://videos.pexels.com/video-files/855564/855564-hd_1920_1080_24fps.mp4",
            "https://videos.pexels.com/video-files/2795171/2795171-hd_1920_1080_25fps.mp4",
            "https://videos.pexels.com/video-files/3129957/3129957-hd_1920_1080_25fps.mp4",
            "https://videos.pexels.com/video-files/3571264/3571264-hd_1920_1080_30fps.mp4",
            "https://videos.pexels.com/video-files/3209828/3209828-hd_1920_1080_25fps.mp4",
            "https://videos.pexels.com/video-files/3195394/3195394-hd_1920_1080_25fps.mp4",
            "https://videos.pexels.com/video-files/854671/854671-hd_1920_1080_25fps.mp4",
            "https://videos.pexels.com/video-files/853919/853919-hd_1920_1080_25fps.mp4",
            "https://videos.pexels.com/video-files/856787/856787-hd_1920_1080_30fps.mp4",
            "https://videos.pexels.com/video-files/856973/856973-hd_1920_1080_25fps.mp4"
    );

    private static final List<List<String>> TAG_POOL = Arrays.asList(
            Arrays.asList("\u7f8e\u5986", "\u5316\u5986\u76d2", "\u9ad8\u989c\u503c", "\u7206\u6b3e", "\u5b66\u751f\u515a"),
            Arrays.asList("\u6559\u80b2", "\u6559\u6750", "\u7545\u9500\u4e66", "\u5b66\u4e60\u5fc5\u5907", "\u9ad8\u5206"),
            Arrays.asList("\u65e5\u7528\u54c1", "\u6e05\u6d01", "\u6740\u83cc", "\u5bb6\u5ead\u88c5", "\u5b9e\u60e0"),
            Arrays.asList("\u5bb6\u5c45", "\u53a8\u623f\u597d\u7269", "\u9ad8\u989c\u503c", "\u5b9e\u7528", "\u7206\u6b3e"),
            Arrays.asList("\u996e\u6599", "\u590f\u65e5\u5fc5\u5907", "\u6e05\u723d", "\u805a\u4f1a", "\u7279\u4ef7"),
            Arrays.asList("\u9970\u54c1", "\u624b\u4e32", "\u6587\u73a9", "\u9001\u793c", "\u7cbe\u81f4"),
            Arrays.asList("\u624b\u8868", "\u65f6\u5c1a\u914d\u9970", "\u54c1\u8d28", "\u5546\u52a1", "\u793c\u7269"),
            Arrays.asList("\u6c34\u679c", "\u6a31\u6843", "\u65b0\u9c9c", "\u4ea7\u5730\u76f4\u53d1", "\u5f53\u5b63"),
            Arrays.asList("\u6c34\u679c", "\u6a59\u5b50", "\u9c9c\u751c", "\u591a\u6c41", "\u5305\u90ae"),
            Arrays.asList("\u65e5\u7528\u54c1", "\u6c34\u676f", "\u4fbf\u643a", "\u9ad8\u989c\u503c", "\u5b66\u751f\u515a")
    );

    private static final List<String> TITLE_PREFIXES = Arrays.asList(
            "\u5927\u724c\u5e73\u66ff\u5316\u5986\u76d2",
            "2025\u65b0\u7248\u6559\u6750\u5168\u89e3",
            "\u6291\u83cc\u6d17\u624b\u6db2\u5bb6\u5ead\u88c5",
            "\u9ad8\u989c\u503c\u6052\u6e29\u70e7\u6c34\u58f6",
            "\u96ea\u78a7\u51b0\u723d\u6574\u7bb1\u8d2d",
            "\u5929\u7136\u7389\u77f3\u624b\u4e32",
            "\u8f7b\u5962\u77f3\u82f1\u624b\u8868",
            "\u73b0\u6458\u5927\u6a31\u6843\u987a\u4e30\u5305\u90ae",
            "\u8d63\u5357\u810a\u6a59\u5f53\u5b63\u9c9c\u6458",
            "\u7f51\u7ea2\u9ad8\u989c\u503c\u968f\u624b\u676f"
    );

    private static final List<String> TITLE_SUFFIXES = Arrays.asList(
            " | \u7cbe\u81f4\u5973\u5b69\u5fc5\u5907\u6536\u7eb3\u795e\u5668",
            " | \u5b66\u9738\u90fd\u5728\u7528\u7684\u63d0\u5206\u5b9d\u5178",
            " | \u6e29\u548c\u4e0d\u4f24\u624b \u9650\u65f6\u4e70\u4e8c\u9001\u4e00",
            " | 3\u5206\u949f\u6cb8\u817e \u989c\u503c\u4e0e\u5b9e\u529b\u5e76\u5b58",
            " | \u590f\u65e5\u7eed\u547d\u6c34 \u805a\u4f1a\u5fc5\u5907",
            " | \u62db\u8d22\u8f6c\u8fd0 \u81ea\u6234\u9001\u4eba\u90fd\u4f53\u9762",
            " | \u7b80\u7ea6\u767e\u642d \u901a\u52e4\u7ea6\u4f1a\u4e00\u652f\u641e\u5b9a",
            " | \u9897\u9897\u7206\u6c41 \u679c\u56ed\u76f4\u53d1\u65b0\u9c9c\u5230\u5bb6",
            " | \u751c\u5230\u5fc3\u574e 10\u65a4\u5bb6\u5ead\u88c5",
            " | \u8010\u6454\u9632\u6f0f \u4e0a\u8bfe\u901a\u52e4\u5fc5\u5907"
    );

    private static final List<String> DESCRIPTIONS = Arrays.asList(
            "\u591a\u5c42\u5206\u533a\u5927\u5bb9\u91cf\uff0c\u5316\u5986\u6536\u7eb3\u4e00\u6b65\u5230\u4f4d\uff0c\u5c0f\u7ea2\u4e66\u4e07\u4eba\u79cd\u8349\u540c\u6b3e",
            "\u540d\u5e08\u7f16\u5199\u8003\u70b9\u5168\u8986\u76d6\uff0c\u540c\u6b65\u8bfe\u5802\u8fdb\u5ea6\uff0c\u968f\u4e66\u8d60\u771f\u9898\u5377",
            "99.9%\u6709\u6548\u6291\u83cc\uff0cpH\u4e2d\u6027\u4e0d\u4f24\u624b\uff0c500ml\u5927\u5bb9\u91cf\u5bb6\u5ead\u88c5",
            "304\u4e0d\u9508\u94a2\u5185\u80c6\uff0c\u667a\u80fd\u6052\u6e29\u4fdd\u6e29\uff0c\u989c\u503c\u5728\u7ebf\u767e\u642d\u5404\u79cd\u53a8\u623f",
            "\u539f\u88c5\u6b63\u54c1\u6574\u7bb124\u7f50\uff0c\u51b0\u9547\u66f4\u8fc7\u763e\uff0c\u70e7\u70e4\u706b\u9505\u7edd\u914d",
            "\u5929\u7136\u7389\u77f3\u624b\u5de5\u6253\u78e8\uff0c\u6e29\u6da6\u7ec6\u817b\u4e0a\u624b\u663e\u767d\uff0c\u9644\u7cbe\u7f8e\u793c\u76d2",
            "\u8fdb\u53e3\u673a\u82af\u8d70\u65f6\u7cbe\u51c6\uff0c30\u7c73\u751f\u6d3b\u9632\u6c34\uff0c\u5546\u52a1\u4f11\u95f2\u4e24\u4e0d\u8bef",
            "\u5f53\u5929\u91c7\u6458\u5f53\u5929\u53d1\u8d27\uff0c\u9897\u989725mm\u4ee5\u4e0a\u5927\u679c\uff0c\u574f\u679c\u5305\u8d54",
            "\u8d63\u5357\u6838\u5fc3\u4ea7\u533a\u76f4\u4f9b\uff0c\u7cd6\u5ea6\u5b9e\u6d4b15\u00b0+\uff0c\u76ae\u8584\u8089\u539a\u6c41\u6c34\u8db3",
            "Tritan\u6750\u8d28\u5b89\u5fc3\u65e0\u5f02\u5473\uff0c800ml\u9ec4\u91d1\u5bb9\u91cf\uff0c\u591a\u8272\u53ef\u9009"
    );

    private static int videoCounter = 0;

    public static List<FeedItem> generateFeedItems(int count) {
        List<FeedItem> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            FeedItemType type = (i % 3 == 0) ? FeedItemType.IMAGE_BIG
                    : (i % 3 == 1) ? FeedItemType.IMAGE_SMALL
                    : FeedItemType.VIDEO;

            FeedCategory category = (i % 3 == 0) ? FeedCategory.RECOMMEND
                    : (i % 3 == 1) ? FeedCategory.ECOMMERCE
                    : FeedCategory.LOCAL;

            int idx = i % TITLE_PREFIXES.size();
            String title = "[" + category.getTitle() + "] " + TITLE_PREFIXES.get(idx)
                    + " #" + (i + 1) + TITLE_SUFFIXES.get(idx);

            String coverUrl = "android.resource://" + PACKAGE_NAME
                    + "/drawable/" + COVER_IMAGES.get(i % COVER_IMAGES.size());

            String videoUrl = null;
            if (type == FeedItemType.VIDEO) {
                videoUrl = VIDEO_URLS.get(videoCounter % VIDEO_URLS.size());
                videoCounter++;
            }

            items.add(new FeedItem(
                    "mock_" + (i + 1),
                    title,
                    DESCRIPTIONS.get(idx),
                    type,
                    category,
                    coverUrl,
                    videoUrl,
                    128 + (i * 17 % 1000),
                    12 + (i * 3 % 50),
                    i % 5 == 0,
                    i % 7 == 0,
                    new AdInsight(null, new ArrayList<>(TAG_POOL.get(idx))),
                    System.currentTimeMillis()
            ));
        }
        Collections.shuffle(items);
        return items;
    }
}