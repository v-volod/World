package com.vojkovladimir.world.provider;

import android.net.Uri;

import static com.vojkovladimir.world.provider.CitiesContract.City;

/**
 * @author vojkovladimir.
 */
public class CityQuery {

    public static Uri URI = City.CONTENT_URI;

    public static String[] PROJECTION = {
            City._ID,
            City.NAME,
            City.DESCRIPTION,
            City.LATITUDE,
            City.LONGITUDE,
            City.IMAGE_URL
    };

    public interface ColumnID {
        int NAME = 1;
        int DESCRIPTION = 2;
        int LATITUDE = 3;
        int LONGITUDE = 4;
        int IMAGE_URL = 5;
    }

    public static String SORT_ORDER = City.IMAGE_URL + " IS NULL, " + City.NAME + " ASC";
}
