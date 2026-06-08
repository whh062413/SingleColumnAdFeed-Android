package com.wuyiming.singlecolumnadfeed_android.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class InteractionStore {
    private static final String PREF_NAME = "ad_feed_interactions";
    private static final String KEY_LIKED_IDS = "liked_ids";
    private static final String KEY_COLLECTED_IDS = "collected_ids";
    private static final String KEY_EXPOSED_IDS = "exposed_ids";

    private final SharedPreferences prefs;

    private static volatile InteractionStore instance;

    public static InteractionStore getInstance(Context context) {
        if (instance == null) {
            synchronized (InteractionStore.class) {
                if (instance == null) {
                    instance = new InteractionStore(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private InteractionStore(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLiked(String feedId) {
        return prefs.getStringSet(KEY_LIKED_IDS, new HashSet<>()).contains(feedId);
    }

    public void setLiked(String feedId, boolean liked) {
        Set<String> ids = new HashSet<>(prefs.getStringSet(KEY_LIKED_IDS, new HashSet<>()));
        if (liked) {
            ids.add(feedId);
        } else {
            ids.remove(feedId);
        }
        prefs.edit().putStringSet(KEY_LIKED_IDS, ids).apply();
    }

    public boolean isCollected(String feedId) {
        return prefs.getStringSet(KEY_COLLECTED_IDS, new HashSet<>()).contains(feedId);
    }

    public void setCollected(String feedId, boolean collected) {
        Set<String> ids = new HashSet<>(prefs.getStringSet(KEY_COLLECTED_IDS, new HashSet<>()));
        if (collected) {
            ids.add(feedId);
        } else {
            ids.remove(feedId);
        }
        prefs.edit().putStringSet(KEY_COLLECTED_IDS, ids).apply();
    }

    public boolean isExposed(String feedId) {
        return prefs.getStringSet(KEY_EXPOSED_IDS, new HashSet<>()).contains(feedId);
    }

    public void setExposed(String feedId) {
        Set<String> ids = new HashSet<>(prefs.getStringSet(KEY_EXPOSED_IDS, new HashSet<>()));
        ids.add(feedId);
        prefs.edit().putStringSet(KEY_EXPOSED_IDS, ids).apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
