package com.wuyiming.singlecolumnadfeed_android.data.local.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;
import java.util.Set;

/**
 * Data access layer for the interactions table.
 * All database operations for likes and collections.
 * 
 * Table schema:
 *   interactions(feed_id TEXT PRIMARY KEY, is_liked INTEGER NOT NULL DEFAULT 0,
 *                is_collected INTEGER NOT NULL DEFAULT 0, updated_at INTEGER NOT NULL)
 */
public class InteractionDao {

    private final InteractionDatabase dbHelper;

    public InteractionDao(InteractionDatabase dbHelper) {
        this.dbHelper = dbHelper;
    }

    // ---- Row existence ----

    /**
     * Check whether an explicit interaction row exists for this feed item.
     * Used to distinguish "never interacted" from "explicitly unliked/uncollected".
     */
    public boolean hasInteraction(String feedId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                InteractionDatabase.getTableName(),
                new String[]{"feed_id"},
                "feed_id = ?",
                new String[]{feedId},
                null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    // ---- Like operations ----

    public boolean isLiked(String feedId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                InteractionDatabase.getTableName(),
                new String[]{"is_liked"},
                "feed_id = ?",
                new String[]{feedId},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) == 1;
            }
        }
        return false;
    }

    public void setLiked(String feedId, boolean liked) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("feed_id", feedId);
        values.put("is_liked", liked ? 1 : 0);
        values.put("updated_at", System.currentTimeMillis());
        db.insertWithOnConflict(
                InteractionDatabase.getTableName(),
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    // ---- Collect operations ----

    public boolean isCollected(String feedId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                InteractionDatabase.getTableName(),
                new String[]{"is_collected"},
                "feed_id = ?",
                new String[]{feedId},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) == 1;
            }
        }
        return false;
    }

    public void setCollected(String feedId, boolean collected) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("feed_id", feedId);
        values.put("is_collected", collected ? 1 : 0);
        values.put("updated_at", System.currentTimeMillis());
        db.insertWithOnConflict(
                InteractionDatabase.getTableName(),
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    // ---- Bulk queries ----

    public Set<String> getAllLikedIds() {
        Set<String> ids = new HashSet<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                InteractionDatabase.getTableName(),
                new String[]{"feed_id"},
                "is_liked = 1",
                null, null, null, null)) {
            while (cursor.moveToNext()) {
                ids.add(cursor.getString(0));
            }
        }
        return ids;
    }

    public Set<String> getAllCollectedIds() {
        Set<String> ids = new HashSet<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                InteractionDatabase.getTableName(),
                new String[]{"feed_id"},
                "is_collected = 1",
                null, null, null, null)) {
            while (cursor.moveToNext()) {
                ids.add(cursor.getString(0));
            }
        }
        return ids;
    }
}
