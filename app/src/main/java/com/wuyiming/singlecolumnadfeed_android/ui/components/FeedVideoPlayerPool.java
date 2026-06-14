package com.wuyiming.singlecolumnadfeed_android.ui.components;

import android.content.Context;
import androidx.media3.exoplayer.ExoPlayer;

public final class FeedVideoPlayerPool {
    private static final SimpleVideoPlayerPool delegate = new SimpleVideoPlayerPool();

    private FeedVideoPlayerPool() {}

    public static ExoPlayer acquire(Context context, String itemId) {
        return delegate.acquire(context, itemId);
    }

    public static void release(String itemId) {
        delegate.release(itemId);
    }

    public static void releaseAll() {
        delegate.releaseAll();
    }
}
