package com.vojkovladimir.world.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

public class CitiesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private CitiesDataBaseHelper mDataBaseHelper;

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case URI_CODE.CITY:
                return CitiesContract.City.CONTENT_TYPE;
            case URI_CODE.CITY_ID:
                return CitiesContract.City.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mDataBaseHelper = new CitiesDataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDataBaseHelper.getReadableDatabase();
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case URI_CODE.CITY:
                queryBuilder.setTables(CitiesDataBaseHelper.Tables.CITY);
                break;
            case URI_CODE.CITY_ID:
                queryBuilder.setTables(CitiesDataBaseHelper.Tables.CITY);
                queryBuilder.appendWhere(CitiesContract.City._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        Cursor cursor = queryBuilder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        //noinspection ConstantConditions
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private interface URI_CODE {
        int CITY = 10;
        int CITY_ID = 11;
    }

    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CitiesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "city", URI_CODE.CITY);
        matcher.addURI(authority, "city/#", URI_CODE.CITY_ID);

        return matcher;
    }

}
