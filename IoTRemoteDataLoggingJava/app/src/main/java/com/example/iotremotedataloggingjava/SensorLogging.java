package com.example.iotremotedataloggingjava;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

public class SensorLogging implements SensorEventListener {

    public static String TAG = "SensorLogging";
    private MQTTConnector mMQTTConnector;
    private boolean mLoggingEnabled = true;
    private Service mLoggingService;
    private SensorManager mSensorManager;

    SensorLogging(MQTTConnector mqttConnector, LoggingService loggingService) {
        mMQTTConnector = mqttConnector;
        mLoggingService = loggingService;
        mSensorManager = (SensorManager)mLoggingService.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startSensorLogging() {
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        sensors.forEach(sensor -> {
            mSensorManager.registerListener(
                    (SensorEventListener) this,
                    sensor,
                    SensorManager.SENSOR_DELAY_FASTEST
            );
        });
    }

    public void stopSensroLogging() {
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        sensors.forEach(sensor -> {
            mSensorManager.unregisterListener(
                    (SensorEventListener) this,
                    sensor
            );
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, sensorEvent.sensor.toString() + ":" + String.valueOf(sensorEvent.values[0]) );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
