/*
 * Copyright (c) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cw.tv_yt.data_yt;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

import androidx.annotation.NonNull;

/**
 * VideoProvider is a ContentProvider that provides videos for the rest of applications.
 */
public class VideoProvider_yt extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public VideoDbHelper_yt mOpenHelper;

    // These codes are returned from sUriMatcher#match when the respective Uri matches.
    private static final int VIDEO = 1;
    private static final int VIDEO_WITH_CATEGORY = 2;
    private static final int SEARCH_SUGGEST = 3;
    private static final int REFRESH_SHORTCUT = 4;

    private static final SQLiteQueryBuilder sVideosContainingQueryBuilder;
    private static final String[] sVideosContainingQueryColumns;
    private static final HashMap<String, String> sColumnMap = buildColumnMap();
    public ContentResolver mContentResolver;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mContentResolver = context.getContentResolver();
        mOpenHelper = new VideoDbHelper_yt(context);
        return true;
    }

    static {
        sVideosContainingQueryBuilder = new SQLiteQueryBuilder();
        sVideosContainingQueryBuilder.setTables(VideoContract_yt.VideoEntry.TABLE_NAME);
        sVideosContainingQueryBuilder.setProjectionMap(sColumnMap);
        sVideosContainingQueryColumns = new String[]{
                VideoContract_yt.VideoEntry._ID,
                VideoContract_yt.VideoEntry.COLUMN_NAME,
                VideoContract_yt.VideoEntry.COLUMN_CATEGORY,
                VideoContract_yt.VideoEntry.COLUMN_DESC,
                VideoContract_yt.VideoEntry.COLUMN_VIDEO_URL,
                VideoContract_yt.VideoEntry.COLUMN_BG_IMAGE_URL,
                VideoContract_yt.VideoEntry.COLUMN_STUDIO,
                VideoContract_yt.VideoEntry.COLUMN_CARD_IMG,
                VideoContract_yt.VideoEntry.COLUMN_CONTENT_TYPE,
                VideoContract_yt.VideoEntry.COLUMN_IS_LIVE,
                VideoContract_yt.VideoEntry.COLUMN_VIDEO_WIDTH,
                VideoContract_yt.VideoEntry.COLUMN_VIDEO_HEIGHT,
                VideoContract_yt.VideoEntry.COLUMN_AUDIO_CHANNEL_CONFIG,
                VideoContract_yt.VideoEntry.COLUMN_PURCHASE_PRICE,
                VideoContract_yt.VideoEntry.COLUMN_RENTAL_PRICE,
                VideoContract_yt.VideoEntry.COLUMN_RATING_STYLE,
                VideoContract_yt.VideoEntry.COLUMN_RATING_SCORE,
                VideoContract_yt.VideoEntry.COLUMN_PRODUCTION_YEAR,
                VideoContract_yt.VideoEntry.COLUMN_DURATION,
                VideoContract_yt.VideoEntry.COLUMN_ACTION,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
        };
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = VideoContract_yt.CONTENT_AUTHORITY;

        // For each type of URI to add, create a corresponding code.
        matcher.addURI(authority, VideoContract_yt.PATH_VIDEO, VIDEO);
        matcher.addURI(authority, VideoContract_yt.PATH_VIDEO + "/*", VIDEO_WITH_CATEGORY);

        // Search related URIs.
        matcher.addURI(authority, "search/" + SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(authority, "search/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        return matcher;
    }

    private Cursor getSuggestions(String query) {
        query = query.toLowerCase();
        return sVideosContainingQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                sVideosContainingQueryColumns,
                VideoContract_yt.VideoEntry.COLUMN_NAME + " LIKE ? OR " +
                        VideoContract_yt.VideoEntry.COLUMN_DESC + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"},
                null,
                null,
                null
        );
    }

    private static HashMap<String, String> buildColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(VideoContract_yt.VideoEntry._ID, VideoContract_yt.VideoEntry._ID);
        map.put(VideoContract_yt.VideoEntry.COLUMN_NAME, VideoContract_yt.VideoEntry.COLUMN_NAME);
        map.put(VideoContract_yt.VideoEntry.COLUMN_DESC, VideoContract_yt.VideoEntry.COLUMN_DESC);
        map.put(VideoContract_yt.VideoEntry.COLUMN_CATEGORY, VideoContract_yt.VideoEntry.COLUMN_CATEGORY);
        map.put(VideoContract_yt.VideoEntry.COLUMN_VIDEO_URL,
                VideoContract_yt.VideoEntry.COLUMN_VIDEO_URL);
        map.put(VideoContract_yt.VideoEntry.COLUMN_BG_IMAGE_URL,
                VideoContract_yt.VideoEntry.COLUMN_BG_IMAGE_URL);
        map.put(VideoContract_yt.VideoEntry.COLUMN_CARD_IMG, VideoContract_yt.VideoEntry.COLUMN_CARD_IMG);
        map.put(VideoContract_yt.VideoEntry.COLUMN_STUDIO, VideoContract_yt.VideoEntry.COLUMN_STUDIO);
        map.put(VideoContract_yt.VideoEntry.COLUMN_CONTENT_TYPE,
                VideoContract_yt.VideoEntry.COLUMN_CONTENT_TYPE);
        map.put(VideoContract_yt.VideoEntry.COLUMN_IS_LIVE, VideoContract_yt.VideoEntry.COLUMN_IS_LIVE);
        map.put(VideoContract_yt.VideoEntry.COLUMN_VIDEO_WIDTH,
                VideoContract_yt.VideoEntry.COLUMN_VIDEO_WIDTH);
        map.put(VideoContract_yt.VideoEntry.COLUMN_VIDEO_HEIGHT,
                VideoContract_yt.VideoEntry.COLUMN_VIDEO_HEIGHT);
        map.put(VideoContract_yt.VideoEntry.COLUMN_AUDIO_CHANNEL_CONFIG,
                VideoContract_yt.VideoEntry.COLUMN_AUDIO_CHANNEL_CONFIG);
        map.put(VideoContract_yt.VideoEntry.COLUMN_PURCHASE_PRICE,
                VideoContract_yt.VideoEntry.COLUMN_PURCHASE_PRICE);
        map.put(VideoContract_yt.VideoEntry.COLUMN_RENTAL_PRICE,
                VideoContract_yt.VideoEntry.COLUMN_RENTAL_PRICE);
        map.put(VideoContract_yt.VideoEntry.COLUMN_RATING_STYLE,
                VideoContract_yt.VideoEntry.COLUMN_RATING_STYLE);
        map.put(VideoContract_yt.VideoEntry.COLUMN_RATING_SCORE,
                VideoContract_yt.VideoEntry.COLUMN_RATING_SCORE);
        map.put(VideoContract_yt.VideoEntry.COLUMN_PRODUCTION_YEAR,
                VideoContract_yt.VideoEntry.COLUMN_PRODUCTION_YEAR);
        map.put(VideoContract_yt.VideoEntry.COLUMN_DURATION, VideoContract_yt.VideoEntry.COLUMN_DURATION);
        map.put(VideoContract_yt.VideoEntry.COLUMN_ACTION, VideoContract_yt.VideoEntry.COLUMN_ACTION);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, VideoContract_yt.VideoEntry._ID + " AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
                VideoContract_yt.VideoEntry._ID + " AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case SEARCH_SUGGEST: {
                String rawQuery = "";
                if (selectionArgs != null && selectionArgs.length > 0) {
                    rawQuery = selectionArgs[0];
                }
                retCursor = getSuggestions(rawQuery);
                break;
            }
            case VIDEO: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        VideoContract_yt.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        retCursor.setNotificationUri(mContentResolver, uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            // The application is querying the db for its own contents.
            case VIDEO_WITH_CATEGORY:
                return VideoContract_yt.VideoEntry.CONTENT_TYPE;
            case VIDEO:
                return VideoContract_yt.VideoEntry.CONTENT_TYPE;

            // The Android TV global search is querying our app for relevant content.
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            case REFRESH_SHORTCUT:
                return SearchManager.SHORTCUT_MIME_TYPE;

            // We aren't sure what is being asked of us.
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final Uri returnUri;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case VIDEO: {
                long _id = mOpenHelper.getWritableDatabase().insert(
                        VideoContract_yt.VideoEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = VideoContract_yt.VideoEntry.buildVideoUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        mContentResolver.notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int rowsDeleted;

        if (selection == null) {
            throw new UnsupportedOperationException("Cannot delete without selection specified.");
        }

        switch (sUriMatcher.match(uri)) {
            case VIDEO: {
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        VideoContract_yt.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsDeleted != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        final int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case VIDEO: {
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        VideoContract_yt.VideoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (rowsUpdated != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        System.out.println("VideoProvider_yt / _bulkInsert / uri = " + uri.toString());
        switch (sUriMatcher.match(uri)) {
            case VIDEO: {
                final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                int returnCount = 0;

                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        System.out.println("VideoProvider_yt / _bulkInsert / title = " + value.getAsString("suggest_text_1"));

                        long _id = db.insertWithOnConflict(VideoContract_yt.VideoEntry.TABLE_NAME,
                                null, value, SQLiteDatabase.CONFLICT_REPLACE);

//                        long _id = db.insertWithOnConflict(VideoContract_yt.VideoEntry.TABLE_NAME,
//                                null, value, SQLiteDatabase.CONFLICT_IGNORE);

                        System.out.println("VideoProvider_yt / _bulkInsert / _id = " + _id);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                mContentResolver.notifyChange(uri, null);
                return returnCount;
            }
            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }
}
