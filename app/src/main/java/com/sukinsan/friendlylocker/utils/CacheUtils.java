package com.sukinsan.friendlylocker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.sukinsan.friendlylocker.entity.Cache;

/**
 * Created by victor on 06.08.15.
 */
public class CacheUtils {
    private static final String TAG = CacheUtils.class.getSimpleName();

    public interface Callback{
        boolean read(Cache cache);
    }

    public static void getCache(Context context, Callback callback){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();

        Cache cache = null;
        if (!sharedPreferences.getString(Cache.class.getName(),"").isEmpty()) {
            try {
                cache = gson.fromJson(sharedPreferences.getString(Cache.class.getName(), ""), Cache.class);
            } catch (Exception e) {
                Log.e(TAG, "get cache", e);
            }
        }
        if (cache == null) {
            cache = new Cache();
        }

        Log.i(TAG,"read " + cache.toString());

        if(callback != null){
            if(callback.read(cache)){
                try {
                    sharedPreferences.edit().remove(Cache.class.getName()).commit();
                    sharedPreferences.edit().putString(Cache.class.getName(),gson.toJson(cache)).commit();
                    Log.i(TAG, "save");
                } catch (Exception e) {
                    Log.e(TAG, "save cache", e);
                }
            }
        }
    }
}
