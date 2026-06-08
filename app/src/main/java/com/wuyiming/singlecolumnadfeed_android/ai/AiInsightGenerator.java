package com.wuyiming.singlecolumnadfeed_android.ai;

import android.util.Log;

import com.wuyiming.singlecolumnadfeed_android.data.model.AdInsight;
import com.wuyiming.singlecolumnadfeed_android.network.AiApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AiInsightGenerator {
    private static final String TAG = "AiInsightGenerator";

    private final AiApiService apiService;
    private final String apiKey;

    public AiInsightGenerator(String apiKey) {
        this.apiService = new AiApiService();
        this.apiKey = apiKey;
    }

    public AdInsight generate(String title, String description) {
        // Hybrid: try cloud first, fallback to local
        try {
            return generateCloud(title, description);
        } catch (Exception e) {
            Log.w(TAG, "Cloud AI failed, falling back to local: " + e.getMessage());
            return generateLocal(title, description);
        }
    }

    private AdInsight generateCloud(String title, String description) throws Exception {
        return apiService.callCloudApi(title, description, apiKey);
    }

    private AdInsight generateLocal(String title, String description) {
        String summary = generateLocalSummary(title, description);
        List<String> tags = generateLocalTags(title, description);
        return new AdInsight(summary, tags);
    }

    private String generateLocalSummary(String title, String description) {
        if (description != null && description.length() > 50) {
            return description.substring(0, 47) + "...";
        }
        return title;
    }

    private List<String> generateLocalTags(String title, String description) {
        List<String> tags = new ArrayList<>();
        String combined = (title + " " + (description != null ? description : "")).toLowerCase();

        List<String> keywordChecks = Arrays.asList(
                "电商", "优惠", "促销", "限时", "折扣", "新品",
                "本地", "附近", "门店", "周边", "同城",
                "科技", "数码", "智能", "手机", "电脑",
                "美食", "餐饮", "外卖", "小吃",
                "美容", "护肤", "化妆", "穿搭",
                "教育", "培训", "课程", "学习",
                "旅游", "酒店", "机票", "景点"
        );

        for (String keyword : keywordChecks) {
            if (combined.contains(keyword) && tags.size() < 5) {
                tags.add(keyword);
            }
        }

        if (tags.isEmpty()) {
            tags.add("推荐");
            tags.add("精选");
        }

        return tags;
    }
}
