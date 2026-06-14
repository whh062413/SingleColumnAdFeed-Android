package com.wuyiming.singlecolumnadfeed_android.config;

/**
 * Centralized AI API configuration.
 * Set these values to enable cloud AI insight generation and search.
 * Leave apiKey empty to disable AI features (app will use local fallback).
 */
public final class AiConfig {

    private AiConfig() {}

    /** Your OpenAI-compatible API key. Fill in to enable AI features. */
    public static String apiKey = "sk-67731e2e82604162bd5eb2339a438514";

    /** OpenAI-compatible chat completions endpoint. */
    public static String apiUrl = "https://api.deepseek.com/v1/chat/completions";

    /** Model name to use. */
    public static String model = "deepseek-v4-flash";

    public static boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
