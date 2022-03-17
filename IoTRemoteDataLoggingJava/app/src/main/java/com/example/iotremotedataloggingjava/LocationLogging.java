package com.example.iotremotedataloggingjava;

import android.app.Service;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;

import static android.content.Context.LOCATION_SERVICE;

public class LocationLogging implements LocationListener {

    public static String TAG = "LocationLogging";
    private MQTTConnector mMQTTConnector;
    private boolean mLoggingEnabled = true;
    private Service mLoggingService;
    private LocationManager mLocationManager;

    LocationLogging(MQTTConnector mqttConnector, LoggingService loggingService) {
        mMQTTConnector = mqttConnector;
        mLoggingService = loggingService;
        mLocationManager = (LocationManager) mLoggingService.getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, location.getLatitude() + ", " + location.getLongitude());
        if (mLoggingEnabled) {
            HashMap data = new HashMap();
            data.put("lat", location.getLatitude());
            data.put("lng", location.getLongitude());
//            mMQTTConnector.publishJson(data, AWSSettings.AWS_IOT_LOCATION_TOPIC_NAME);
            toast(data.toString());
        }
    }

    public void enableLogging() {
        this.mLoggingEnabled = true;
    }

    public void disableLogging() {
        this.mLoggingEnabled = false;
    }

    public void startLocationLogging() {
        Log.d(TAG,"locationStart()");

        int minDistanceM = 2;
        int minTimeMs = 2000;
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, minTimeMs, minDistanceM, this
        );
    }

    public void stopLocationLogging() {
        mLocationManager.removeUpdates(this);
    }

    private void toast(String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mLoggingService.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}