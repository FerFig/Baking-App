package com.ferfig.bakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
    public static final String APP_TAG = "BackingAppByFF";

    public static final String RECIPS_URL_DATA =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public static final String ALL_RECIPS_SAVED_INSTANCE = "com.ferfig.bakingapp.ALL_RECIPS";
    public static final String RECLYCLER_SAVED_INSTANCE = "com.ferfig.bakingapp.RECLYCLER_STATE";

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

}
