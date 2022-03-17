package com.example.iotremotedataloggingjava;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class LoggingService extends Service {

    public static String TAG = "LoggingService";
    private MQTTConnector mMQTTConnector;
    private LocationLogging mLocationLogging;
    private SensorLogging mSensorLogging;
    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mMQTTConnector = new MQTTConnector(this);
        mLocationLogging = new LocationLogging(mMQTTConnector, this);
        mSensorLogging = new SensorLogging(mMQTTConnector, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        String channelId = "default";
        String title = context.getString(R.string.app_name);

        int requestCode = 1;
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                channelId, title , NotificationManager.IMPORTANCE_DEFAULT);

        if(notificationManager != null) {
            notificationManager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentText("Logging Service")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();

            startForeground(1, notification);
        }

        mMQTTConnector.connectSubscribe(new RemoteCommandHandler(this));

        mLocationLogging.startLocationLogging();
//        mSensorLogging.startSensorLogging();

        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams (
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.height = 600;
        params.width = 600;
        wm.addView(mPreview, params);

//        startCameraCapture(new Camera.PreviewCallback() {
//            @Override
//            public void onPreviewFrame(byte[] bytes, Camera camera) {
//                Log.d(TAG, "onPreviewFrame");
//            }
//        });

        (new Timer()).scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                Log.d(TAG, "Hello!");
                stopCameraCapture();;
            }
        }, 3000, 60000);

        return START_NOT_STICKY;
        //return START_STICKY;
        //return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        mLocationLogging.stopLocationLogging();
        mSensorLogging.stopSensroLogging();
        mMQTTConnector.disconnect();
        stopCameraCapture();
        stopSelf();
    }

    public void startSensorLogging() {
        mSensorLogging.startSensorLogging();
    }

    public void stopSensorLogging() {
        mSensorLogging.stopSensroLogging();
    }

    public void startLocationLogging() {
        mLocationLogging.startLocationLogging();
    }

    public void stopLocationLogging() {
        mLocationLogging.stopLocationLogging();
    }

    public void startCameraCapture(Camera.PreviewCallback captureCallback) {
        mPreview.startPreview(captureCallback);
    }

    public void stopCameraCapture() {
        mPreview.stopPreview();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
