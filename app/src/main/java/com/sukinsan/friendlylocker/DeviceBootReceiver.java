package com.sukinsan.friendlylocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.sukinsan.friendlylocker.service.ProximityService;

/**
 * Created by victor on 12/16/16.
 */

public class DeviceBootReceiver extends BroadcastReceiver {
    private static final String TAG = DeviceBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive " + intent.getAction());
        context.startService(new Intent(context, ProximityService.class));
    }
}
