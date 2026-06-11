package com.wuyiming.singlecolumnadfeed_android.ai;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wuyiming.singlecolumnadfeed_android.network.AiApiService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI-powered search keyword extraction.
 * Given a user's natural-language search query, calls the AI to expand it
 * into concrete search keywords, then the FeedViewModel uses those keywords
 * for local fuzzy matching.
 */
public class AiSearchService {
    private static final String TAG = "AiSearchService";

    private final AiApiService apiService;
    private final String apiKey;
    private final Gson gson = new Gson();

    public AiSearchService(String apiKey, String apiUrl, String model) {
        this.apiService = new AiApiService(apiUrl, model);
        this.apiKey = apiKey;
    }

    /**
     * Extract search keywords from a natural-language query via AI.
     * Returns a list of Chinese keywords for local matching.
     * Falls back to splitting the raw query if AI fails.
     */
    public List<String> extractKeywords(String query) {
        // Quick path: single word or very short query → use directly
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String trimmed = query.trim();
        if (trimmed.length() <= 2) {
            List<String> direct = new ArrayList<>();
            direct.add(trimmed);
            return direct;
        }

        try {
            return extractCloud(trimmed);
        } catch (Exception e) {
            Log.w(TAG, "AI search extraction failed, using raw split: " + e.getMessage());
            return extractLocal(trimmed);
        }
    }

    private List<String> extractCloud(String query) throws Exception {
        String systemPrompt = "你是中文搜索关键词提取器。将用户的自然语言搜索词拆解为3-8个核心中文关键词。"
                + "返回纯JSON数组: [\"关键词1\", \"关键词2\", ...]";

        String raw = apiService.callRawApi(systemPrompt, query, apiKey);

        // Parse JSON array from AI response
        Type listType = new TypeToken<List<String>>(){}.getType();
        List<String> keywords = gson.fromJson(raw.trim(), listType);

        if (keywords == null || keywords.isEmpty()) {
            throw new Exception("AI returned empty keywords");
        }

        Log.d(TAG, "AI keywords for [" + query + "]: " + keywords);
        return keywords;
    }

    private List<String> extractLocal(String query) {
        // Fallback: split by spaces, punctuation, treat each word as keyword
        List<String> keywords = new ArrayList<>();
        String[] parts = query.split("[\\s,.!?;:，。！？；：]+");
        for (String part : parts) {
            if (part.length() >= 1) {
                keywords.add(part);
            }
        }
        if (keywords.isEmpty()) {
            keywords.add(query);
        }
        return keywords;
    }
}