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

    @SuppressWarnings("unchecked")
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
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> responseMap = gson.fromJson(responseBody, mapType);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IOException("AI API returned empty choices");
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        }
    }

    public AdInsight callCloudApi(String title, String description, String apiKey) throws IOException {
        String systemPrompt = "\u4f60\u662f\u5e7f\u544a\u5185\u5bb9\u5206\u6790\u4e13\u5bb6\u3002"
                + "\u6839\u636e\u5e7f\u544a\u6807\u9898\u548c\u63cf\u8ff0\uff0c"
                + "\u751f\u6210\u4e00\u6761\u4e2d\u6587\u6458\u8981\uff08\u4e0d\u8d85\u8fc750\u5b57\uff09"
                + "\u548c3-5\u4e2a\u6807\u7b7e\u3002"
                + "\u8fd4\u56de\u683c\u5f0f\u4e3aJSON: {\"summary\": \"...\", \"tags\": [\"...\"]}";

        String raw = callRawApi(systemPrompt,
                "\u5e7f\u544a\u6807\u9898\uff1a" + title + "\n\u5e7f\u544a\u63cf\u8ff0\uff1a" + description, apiKey);

        Type insightType = new TypeToken<AdInsight>(){}.getType();
        return gson.fromJson(raw, insightType);
    }
}