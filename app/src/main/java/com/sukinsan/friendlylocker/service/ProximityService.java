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
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.sukinsan.friendlylocker.R;
import com.sukinsan.friendlylocker.activity.MainActivity;

public class ProximityService extends Service implements SensorEventListener{
    private static final String TAG = ProximityService.class.getSimpleName();

    private PowerManager pm;
    private PowerManager.WakeLock wakeUpScreen;
    private PowerManager.WakeLock shutDownScreen;

    private int timeDelay = 3000;
    private Handler timeOutToSleep = new Handler();
    private Runnable timeOutCallbackToSleep = new Runnable() {
            public void run() {
                shutDownScreen.acquire();
                lock.start();
                Log.i(TAG, "shutdown");
                timeOutReleaseProximity.postDelayed(timeOutCallbackReleaseProximity,10000);
            }
        };

    private Handler timeOutReleaseProximity = new Handler();
    private Runnable timeOutCallbackReleaseProximity = new Runnable() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void run() {
            shutDownScreen.release(PowerManager.RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY);
            Log.i(TAG, "proximity shutDownScreen.release");
        }
    };

    private SensorManager mSensorManager;
    private Sensor mProximity;

    private MediaPlayer beep;
    private MediaPlayer lock;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProximityService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, getString(R.string.app_name) + " started!", Toast.LENGTH_LONG).show();

        beep = MediaPlayer.create(this,R.raw.meow1_wav);
        beep.setVolume(0.1f, 0.1f);
        lock = MediaPlayer.create(this,R.raw.lock2_wav);
        lock.setVolume(0.1f,0.1f);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeUpScreen = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "WakeUp");
        shutDownScreen = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "ShutDown");
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
    public void onSensorChanged(final SensorEvent event) {
        timeOutReleaseProximity.removeCallbacks(timeOutCallbackReleaseProximity);
        if(event.values.length > 0){
            if(event.values[0] == 0.0){
                if(timeDelay > 0){ // we do not want to play beep and lock at the same time
                    beep.start();
                }
                timeOutToSleep.postDelayed(timeOutCallbackToSleep, timeDelay);
            }else{
                beep.start();
                Log.i(TAG, "wake up ");
                timeOutToSleep.removeCallbacks(timeOutCallbackToSleep);
                wakeUpScreen.acquire();
                wakeUpScreen.release();
                if(shutDownScreen.isHeld()) {
                    shutDownScreen.release();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
