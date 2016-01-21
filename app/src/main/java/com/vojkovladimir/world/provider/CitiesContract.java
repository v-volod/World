package com.vojkovladimir.world.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author vojkovladimir.
 */
public class CitiesContract {

    public interface CityColumns {
        String NAME = "name";
        String DESCRIPTION = "description";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String IMAGE_URL = "image_url";
    }

    public static final String CONTENT_AUTHORITY = "com.vojkovladimir.world.CitiesProvider";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_CITY = "city";

    public static class City implements BaseColumns, CityColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_CITY).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd." + CONTENT_AUTHORITY + "." + PATH_CITY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd." + CONTENT_AUTHORITY + "." + PATH_CITY;

        public static Uri buildItemUri(long itemId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(itemId)).build();
        }

    }
}
