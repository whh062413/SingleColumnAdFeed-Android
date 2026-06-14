package com.wuyiming.singlecolumnadfeed_android;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import com.wuyiming.singlecolumnadfeed_android.config.AiConfig;
import com.wuyiming.singlecolumnadfeed_android.ui.theme.ThemeKt;
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedViewModel;

public class MainActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (AiConfig.isConfigured()) {
            FeedViewModel.getSharedInstance(getApplication())
                    .configureAi(AiConfig.apiKey, AiConfig.apiUrl, AiConfig.model);
        }

        ThemeKt.setupMainContent(this);
    }
}
