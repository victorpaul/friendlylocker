package com.sukinsan.friendlylocker.service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.sukinsan.friendlylocker.R;
import com.sukinsan.friendlylocker.activity.MainActivity;

public class ProximityService extends Service{
    private static final String TAG = ProximityService.class.getSimpleName();
    private PowerManager.WakeLock wl;
    private PowerManager pm;

    public ProximityService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, getString(R.string.app_name) + " started!", Toast.LENGTH_LONG).show();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "My Tag");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, getString(R.string.app_name) + " stopped :(", Toast.LENGTH_LONG).show();
        wl.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wl.acquire();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(this, MainActivity.class);
        PendingIntent pbIntent = PendingIntent.getActivity(this, 0, bIntent, 0);
        NotificationCompat.Builder bBuilder =
                    new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.app_name) + " is working hard to make you happy!")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(pbIntent);
        Notification barNotif = bBuilder.build();
        this.startForeground(1, barNotif);

        return Service.START_STICKY;
    }


}
