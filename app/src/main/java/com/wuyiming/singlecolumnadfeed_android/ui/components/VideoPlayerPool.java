package com.wuyiming.singlecolumnadfeed_android.ui.components;

import android.content.Context;
import androidx.media3.exoplayer.ExoPlayer;

public interface VideoPlayerPool {
    ExoPlayer acquire(Context context, String itemId);
    void release(String itemId);
    void releaseAll();
}
