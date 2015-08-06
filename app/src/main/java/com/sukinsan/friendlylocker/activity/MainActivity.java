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
import android.widget.Toast;
import com.sukinsan.friendlylocker.R;
import com.sukinsan.friendlylocker.service.ProximityService;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button startService;
    private Button endService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor proximitySensor= sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            Toast.makeText(this, "No Proximity Sensor Found. Sorry, this app will not work on your device! ", Toast.LENGTH_LONG).show();
            return;
        }

        startService = (Button)findViewById(R.id.btn_friendlylocker_start);
        endService = (Button)findViewById(R.id.btn_friendlylocker_stop);

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
}
