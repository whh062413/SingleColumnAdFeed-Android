package com.wuyiming.singlecolumnadfeed_android.config;

/**
 * Centralized AI API configuration.
 * Set these values before FeedViewModel initializes to enable cloud AI insight generation.
 * Leave apiKey empty or null to disable AI features.
 */
public final class AiConfig {

    private AiConfig() {
        // utility class, not instantiable
    }

    /**
     * Your OpenAI-compatible API key.
     * Set to null or empty string to disable AI insight generation.
     */
    public static String apiKey = "sk-d9e1c3fc012c49bdbcc53f8769008043"; // TODO: set your API key here

    /**
     * OpenAI-compatible chat completions endpoint.
     * Default: OpenAI official endpoint.
     */
    public static String apiUrl = "https://api.deepseek.com/v1/chat/completions";

    /**
     * Model name to use.
     * Default: gpt-4o-mini (fast and cost-effective).
     */
    public static String model = "deepseek-v4-flash";

    /**
     * Convenience check 鈥?returns true when AI is properly configured.
     */
    public static boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}