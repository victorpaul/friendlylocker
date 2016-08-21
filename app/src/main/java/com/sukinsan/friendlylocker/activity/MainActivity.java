package com.sukinsan.friendlylocker.activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.sukinsan.friendlylocker.R;
import com.sukinsan.friendlylocker.entity.Cache;
import com.sukinsan.friendlylocker.service.ProximityService;
import com.sukinsan.friendlylocker.utils.CacheUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button startService;
    private Button endService;

    private CheckBox checkBoxVibrate;
    private CheckBox checkBoxSound;
    private CheckBox checkBoxLockSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBoxVibrate = (CheckBox)findViewById(R.id.checkBox_vibrate);
        checkBoxSound = (CheckBox)findViewById(R.id.checkBox_sound);
        checkBoxLockSound = (CheckBox)findViewById(R.id.checkBox_locksound);
        checkBoxVibrate.setOnClickListener(this);
        checkBoxSound.setOnClickListener(this);
        checkBoxLockSound.setOnClickListener(this);

        CacheUtils.getCache(this, new CacheUtils.Callback() {
            @Override
            public boolean read(Cache cache) {
                checkBoxLockSound.setChecked(cache.isPlaySongOnLock());
                checkBoxSound.setChecked(cache.isPlaySongOnSensor());
                checkBoxVibrate.setChecked(cache.isVibrateOnSensor());
                return false;
            }
        });

        SensorManager sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor proximitySensor= sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            Toast.makeText(this, "No Proximity Sensor Found. Sorry, this app will not work on your device! ", Toast.LENGTH_LONG).show();
            return;
        }

        startService = (Button)findViewById(R.id.btn_friendlylocker_start);
        endService = (Button)findViewById(R.id.btn_friendlylocker_stop);

        //startService(new Intent(MainActivity.this, ProximityService.class));
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, ProximityService.class));
                updateButtonStartStop();
            }
        });

        endService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, ProximityService.class));
                updateButtonStartStop();
            }
        });

        updateButtonStartStop();
    }

    private void updateButtonStartStop(){
        if(isMyServiceRunning()){
            startService.setVisibility(View.GONE);
            endService.setVisibility(View.VISIBLE);
        }else{
            startService.setVisibility(View.VISIBLE);
            endService.setVisibility(View.GONE);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ProximityService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void saveSettings(){
        CacheUtils.getCache(this, new CacheUtils.Callback() {
            @Override
            public boolean read(Cache cache) {
                cache.setPlaySongOnLock(checkBoxLockSound.isChecked());
                cache.setPlaySongOnSensor(checkBoxSound.isChecked());
                cache.setVibrateOnSensor(checkBoxVibrate.isChecked());
                return true;
            }
        });
        stopService(new Intent(MainActivity.this, ProximityService.class));
        startService(new Intent(MainActivity.this, ProximityService.class));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.checkBox_locksound:
            case R.id.checkBox_sound:
            case R.id.checkBox_vibrate:
                saveSettings();
                break;
        }
    }
}
