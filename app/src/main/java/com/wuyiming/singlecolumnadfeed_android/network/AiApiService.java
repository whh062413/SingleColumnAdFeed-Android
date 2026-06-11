package com.wuyiming.singlecolumnadfeed_android.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wuyiming.singlecolumnadfeed_android.data.model.AdInsight;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiApiService {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final String apiUrl;
    private final String model;
    private final Gson gson = new Gson();

    public AiApiService() {
        this("https://api.openai.com/v1/chat/completions", "gpt-4o-mini");
    }

    public AiApiService(String apiUrl, String model) {
        this.apiUrl = apiUrl;
        this.model = model;
    }

    /**
     * General-purpose raw API call. Returns the AI's text response content.
     */
    public String callRawApi(String systemPrompt, String userMessage, String apiKey) throws IOException {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.3,
                "max_tokens", 500
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(gson.toJson(body), JSON))
                .build();

        try (Response response = OkHttpProvider.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI API error: " + response.code());
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            Map<String, Object> responseMap = gson.fromJson(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IOException("AI API returned empty choices");
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        }
    }

    public AdInsight callCloudApi(String title, String description, String apiKey) throws IOException {
        String systemPrompt = "你是广告内容分析专家。根据广告标题和描述，生成一条中文摘要（不超过50字）和3-5个标签。"
                + "返回格式为JSON: {\"summary\": \"...\", \"tags\": [\"...\"]}";

        String raw = callRawApi(systemPrompt,
                "广告标题：" + title + "\n广告描述：" + description, apiKey);

        Type insightType = new TypeToken<AdInsight>(){}.getType();
        return gson.fromJson(raw, insightType);
    }
}