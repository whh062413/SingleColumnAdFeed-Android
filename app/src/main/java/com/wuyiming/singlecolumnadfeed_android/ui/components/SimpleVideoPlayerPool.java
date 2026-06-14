package com.wuyiming.singlecolumnadfeed_android.ui.components;

import android.content.Context;
import androidx.media3.exoplayer.ExoPlayer;
import java.util.HashMap;
import java.util.Map;

public class SimpleVideoPlayerPool implements VideoPlayerPool {
    private final Map<String, ExoPlayer> players = new HashMap<>();

    @Override
    public ExoPlayer acquire(Context context, String itemId) {
        ExoPlayer player = players.get(itemId);
        if (player == null) {
            player = new ExoPlayer.Builder(context.getApplicationContext()).build();
            players.put(itemId, player);
        }
        return player;
    }

    @Override
    public void release(String itemId) {
        ExoPlayer player = players.remove(itemId);
        if (player != null) {
            player.release();
        }
    }

    @Override
    public void releaseAll() {
        for (ExoPlayer player : players.values()) {
            player.release();
        }
        players.clear();
    }
}
