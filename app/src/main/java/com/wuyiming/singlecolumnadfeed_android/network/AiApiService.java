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
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final Gson gson = new Gson();

    public AdInsight callCloudApi(String title, String description, String apiKey) throws IOException {
        String systemPrompt = "你是广告内容分析专家。根据广告标题和描述，生成一条中文摘要（不超过50字）和3-5个标签。"
                + "返回格式为JSON: {\"summary\": \"...\", \"tags\": [\"...\"]}";

        String userMessage = "广告标题：" + title + "\n广告描述：" + description;

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.7,
                "max_tokens", 300
        );

        Request request = new Request.Builder()
                .url(API_URL)
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
            String content = (String) message.get("content");

            Type insightType = new TypeToken<AdInsight>(){}.getType();
            return gson.fromJson(content, insightType);
        }
    }
}
