package com.vojkovladimir.world.provider;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * @author vojkovladimir.
 */
public class CitiesDataBaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "cities.db";
    private static final int DATABASE_VERSION = 1;

    interface Tables {
        String CITY = "city";
    }

    public CitiesDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}