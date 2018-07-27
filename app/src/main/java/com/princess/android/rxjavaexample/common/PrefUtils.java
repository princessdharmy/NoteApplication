package com.princess.android.rxjavaexample.common;

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
    public PrefUtils(Context context) {
        mContext = context;
        sharedPreferences = context.getSharedPreferences(Constants.APP_REF, Context.MODE_PRIVATE);
    }

    public static void storeApiKey(String apiKey){
        editor = sharedPreferences.edit();
        editor.putString(Constants.API_KEY, apiKey);
        editor.apply();
    }

    public static String getApiKey(){
        return sharedPreferences.getString(Constants.API_KEY, null);
    }

}
