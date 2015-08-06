package com.sukinsan.friendlylocker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sukinsan.friendlylocker.entity.Cache;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by victor on 06.08.15.
 */
public class CacheUtils {
    public static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final String TAG = CacheUtils.class.getSimpleName();

    public interface Callback{
        boolean read(Cache cache);
    }

    public static void getCache(Context context, Callback callback){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Cache cache = null;
        if (!sharedPreferences.getString(Cache.class.getName(),"").isEmpty()) {
            try {
                cache = MAPPER.readValue(sharedPreferences.getString(Cache.class.getName(), ""), Cache.class);
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
                    sharedPreferences.edit().putString(Cache.class.getName(), MAPPER.writeValueAsString(cache)).commit();
                    Log.i(TAG, "save");
                } catch (Exception e) {
                    Log.e(TAG, "save cache", e);
                }
            }
        }
    }
}
