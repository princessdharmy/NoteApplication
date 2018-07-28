package com.princess.android.rxjavaexample.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.princess.android.rxjavaexample.utils.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PrefUtils {

    private Context mContext;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    @Inject
    public PrefUtils() {

    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.APP_REF, Context.MODE_PRIVATE);
    }

    public static void storeApiKey(Context context, String apiKey){
        editor = getSharedPreferences(context).edit();
        editor.putString(Constants.API_KEY, apiKey);
        editor.apply();
    }

    public static String getApiKey(Context context){
        return getSharedPreferences(context).getString(Constants.API_KEY, null);
    }

}
