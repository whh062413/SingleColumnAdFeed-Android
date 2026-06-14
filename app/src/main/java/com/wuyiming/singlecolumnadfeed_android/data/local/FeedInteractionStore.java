package com.wuyiming.singlecolumnadfeed_android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class FeedInteractionStore {
    private static final String DATABASE_NAME = "feed_interactions.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_INTERACTIONS = "feed_interactions";
    private static final String COLUMN_ITEM_ID = "item_id";
    private static final String COLUMN_LIKE_STATE = "like_state";
    private static final String COLUMN_COLLECT_STATE = "collect_state";
    private static final String COLUMN_UPDATED_AT = "updated_at";

    private final DatabaseHelper databaseHelper;

    public FeedInteractionStore(Context context) {
        Context appContext = context.getApplicationContext();
        databaseHelper = new DatabaseHelper(appContext);
    }

    public synchronized boolean isLiked(String itemId) {
        Integer state = getState(itemId, COLUMN_LIKE_STATE);
        return state != null && state == 1;
    }

    public synchronized boolean hasLikeOverride(String itemId) {
        return getState(itemId, COLUMN_LIKE_STATE) != null;
    }

    public synchronized boolean isCollected(String itemId) {
        Integer state = getState(itemId, COLUMN_COLLECT_STATE);
        return state != null && state == 1;
    }

    public synchronized boolean hasCollectOverride(String itemId) {
        return getState(itemId, COLUMN_COLLECT_STATE) != null;
    }

    public synchronized void setLiked(String itemId, boolean liked) {
        updateState(itemId, COLUMN_LIKE_STATE, liked ? 1 : 0);
    }

    public synchronized void setCollected(String itemId, boolean collected) {
        updateState(itemId, COLUMN_COLLECT_STATE, collected ? 1 : 0);
    }

    private Integer getState(String itemId, String columnName) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        try (Cursor cursor = database.query(
                TABLE_INTERACTIONS,
                new String[]{columnName},
                COLUMN_ITEM_ID + " = ?",
                new String[]{itemId},
                null, null, null)) {
            if (!cursor.moveToFirst() || cursor.isNull(0)) return null;
            return cursor.getInt(0);
        }
    }

    private void updateState(String itemId, String columnName, int state) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_ID, itemId);
        values.put(columnName, state);
        values.put(COLUMN_UPDATED_AT, System.currentTimeMillis());
        database.insertWithOnConflict(TABLE_INTERACTIONS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        ContentValues updateValues = new ContentValues();
        updateValues.put(columnName, state);
        updateValues.put(COLUMN_UPDATED_AT, System.currentTimeMillis());
        database.update(TABLE_INTERACTIONS, updateValues, COLUMN_ITEM_ID + " = ?", new String[]{itemId});
    }

    private static final class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(
                "CREATE TABLE " + TABLE_INTERACTIONS + " (" +
                    COLUMN_ITEM_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_LIKE_STATE + " INTEGER, " +
                    COLUMN_COLLECT_STATE + " INTEGER, " +
                    COLUMN_UPDATED_AT + " INTEGER NOT NULL" +
                ")"
            );
        }
        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
    }
}
