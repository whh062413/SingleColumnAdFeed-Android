package com.wuyiming.singlecolumnadfeed_android.data.local;

import android.content.Context;

import com.wuyiming.singlecolumnadfeed_android.data.local.db.InteractionDao;
import com.wuyiming.singlecolumnadfeed_android.data.local.db.InteractionDatabase;

import java.util.Set;

/**
 * Facade over the SQLite-backed interaction store.
 * Provides a simple API for checking and setting like/collect state.
 *
 * Persistence model: each feed item''s interaction state lives as a row
 * in the SQLite interactions table. When a row exists, it represents the
 * user''s explicit choice (like or unlike, collect or uncollect).
 * When no row exists, the item''s initial mock state is used as default.
 */
public class InteractionStore {

    private final InteractionDao dao;

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
        InteractionDatabase db = InteractionDatabase.getInstance(context);
        this.dao = new InteractionDao(db);
    }

    // ---- Existence ----

    /**
     * Whether the user has explicitly interacted with this item.
     * If false, the item''s initial mock state should be retained.
     */
    public boolean hasInteraction(String feedId) {
        return dao.hasInteraction(feedId);
    }

    // ---- Likes ----

    public boolean isLiked(String feedId) {
        return dao.isLiked(feedId);
    }

    public void setLiked(String feedId, boolean liked) {
        dao.setLiked(feedId, liked);
    }

    // ---- Collections ----

    public boolean isCollected(String feedId) {
        return dao.isCollected(feedId);
    }

    public void setCollected(String feedId, boolean collected) {
        dao.setCollected(feedId, collected);
    }

    // ---- Bulk ----

    public Set<String> getAllLikedIds() {
        return dao.getAllLikedIds();
    }

    public Set<String> getAllCollectedIds() {
        return dao.getAllCollectedIds();
    }

    // ---- Utilities ----

    public void clear() {
        InteractionDatabase.getInstance(null).getWritableDatabase()
                .execSQL("DELETE FROM " + InteractionDatabase.getTableName());
    }
}
