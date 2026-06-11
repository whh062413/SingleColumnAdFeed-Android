package com.wuyiming.singlecolumnadfeed_android;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;

import com.wuyiming.singlecolumnadfeed_android.config.AiConfig;
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedViewModel;

public class MainActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // ── AI API config (from AiConfig.java) ──
        if (AiConfig.isConfigured()) {
            FeedViewModel.getSharedInstance(getApplication())
                    .configureAi(AiConfig.apiKey, AiConfig.apiUrl, AiConfig.model);
        }

        // ── Compose UI (delegated to Kotlin bridge) ──
        MainContent.setupCompose(this);
    }
}