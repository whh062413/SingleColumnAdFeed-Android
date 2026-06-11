package com.wuyiming.singlecolumnadfeed_android.data.local.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite database for persisting user interactions (likes, collections).
 * 
 * Schema:
 *   interactions(feed_id TEXT PRIMARY KEY, is_liked INTEGER NOT NULL DEFAULT 0,
 *                is_collected INTEGER NOT NULL DEFAULT 0, updated_at INTEGER NOT NULL)
 */
public class InteractionDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ad_feed.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_INTERACTIONS = "interactions";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_INTERACTIONS + " ("
                    + "feed_id TEXT PRIMARY KEY,"
                    + "is_liked INTEGER NOT NULL DEFAULT 0,"
                    + "is_collected INTEGER NOT NULL DEFAULT 0,"
                    + "updated_at INTEGER NOT NULL"
                    + ")";

    private static volatile InteractionDatabase instance;

    public static InteractionDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (InteractionDatabase.class) {
                if (instance == null) {
                    instance = new InteractionDatabase(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private InteractionDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future migrations go here
    }

    public static String getTableName() {
        return TABLE_INTERACTIONS;
    }
}
