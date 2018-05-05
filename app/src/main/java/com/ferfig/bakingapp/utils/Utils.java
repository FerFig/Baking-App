package com.ferfig.bakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ferfig.bakingapp.R;

import java.util.Locale;

public class Utils {
    public static final String APP_TAG = "BackingAppByFF";

    public static final String RECIPS_URL_DATA =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public static final String ALL_RECIPS_SAVED_INSTANCE = "com.ferfig.bakingapp.ALL_RECIPS";
    public static final String RECLYCLER_SAVED_INSTANCE = "com.ferfig.bakingapp.RECLYCLER_STATE";
    public static final String RECIPE_DATA_OBJECT = "com.ferfig.bakingapp.RECIPE_DATA";
    public static final String CURRENT_STEP_OBJECT = "com.ferfig.bakingapp.CURRENT_STEP";
    public static final String CURRENT_VIDEO_POSITION = "com.ferfig.bakingapp.CURRENT_POSITION";

    public static final String DATABASE_NAME = "BackingAppByFF.db";
    public static final String DB_TABLE_RECIPES = "recipes";

    public static final long MEDIA_FF_SEEK_TIME_MS = 15000;
    public static final long MEDIA_RW_SEEK_TIME_MS = 5000;

    public static boolean isInternetConectionAvailable(Context context) {
        if (context.getSystemService(Context.CONNECTIVITY_SERVICE)!=null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork;
                activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
        }
        return true;
    }

    public static boolean isTwoPaneLayout(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static boolean isDeviceInLandscape(Context context){
        return context.getResources().getBoolean(R.bool.isInLandscape);
    }

    public static String formatQuantity(float quantity) {
        if((long)quantity == quantity)
            return String.format(Locale.getDefault(), "%d",(long)quantity);
        else
            return String.format("%s", quantity);
    }
}
