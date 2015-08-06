package com.sukinsan.friendlylocker.service;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.sukinsan.friendlylocker.R;
import com.sukinsan.friendlylocker.activity.MainActivity;

import java.io.File;

public class ProximityService extends Service implements SensorEventListener{
    private static final String TAG = ProximityService.class.getSimpleName();

    private PowerManager pm;
    private PowerManager.WakeLock wakeUpScreen;
    private PowerManager.WakeLock shutDownScreen;

    private Handler timeOut = new Handler();
    private Runnable timeOutCallback = new Runnable() {
            public void run() {
                shutDownScreen.acquire();
            }
        };

    private SensorManager mSensorManager;
    private Sensor mProximity;

    private MediaPlayer beep;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProximityService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, getString(R.string.app_name) + " started!", Toast.LENGTH_LONG).show();

        beep = MediaPlayer.create(this,R.raw.beep1_wav);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeUpScreen = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
        shutDownScreen = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "My Tag");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, getString(R.string.app_name) + " stopped :(", Toast.LENGTH_LONG).show();
        //wl.release();
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //wl.acquire();

        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_UI);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(this, MainActivity.class);
        PendingIntent pbIntent = PendingIntent.getActivity(this, 0, bIntent, 0);
        NotificationCompat.Builder bBuilder =
                    new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("is making you happy!!")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(pbIntent);
        Notification barNotif = bBuilder.build();
        this.startForeground(1, barNotif);

        return Service.START_STICKY;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        beep.start();
        if(event.values.length > 0){
            if(event.values[0] == 0.0){
                Log.i(TAG, "shutdown " + event.values[0]);
                timeOut.postDelayed(timeOutCallback, 5000);
            }else{
                Log.i(TAG, "wake up " + event.values[0]);
                timeOut.removeCallbacks(timeOutCallback);
                if(shutDownScreen.isHeld()) {
                    shutDownScreen.release();
                }

                wakeUpScreen.acquire();
                wakeUpScreen.release();
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
