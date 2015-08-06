package com.sukinsan.friendlylocker.activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.sukinsan.friendlylocker.R;
import com.sukinsan.friendlylocker.service.ProximityService;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor proximitySensor= sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            Toast.makeText(this, "No Proximity Sensor Found. Sorry, this app will not work on your device! ", Toast.LENGTH_LONG).show();
        }

        findViewById(R.id.btn_friendlylocker_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, ProximityService.class));
            }
        });

        findViewById(R.id.btn_friendlylocker_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, ProximityService.class));
            }
        });
    }
}
