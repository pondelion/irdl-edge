package com.example.iotremotedataloggingjava;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;


import java.io.File;

public class S3Storage {

    public static final String TAG = "S3Storage";
    private String mBucketName;
    private BasicAWSCredentials mAWSCredentials;
    private AmazonS3Client mS3Client;
    private Context mContext;

    S3Storage(Context context) {
        mContext = context;
        mBucketName = AWSSettings.BUCKET_NAME;
        mAWSCredentials = new BasicAWSCredentials(AWSSettings.AWS_ACCESS_KEY_ID, AWSSettings.AWS_SECRET_ACCESS_KEY);
        mS3Client = new AmazonS3Client(mAWSCredentials);
        mS3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
    }

    public void saveFile(@NonNull String localFilepath, @NonNull String s3Filepath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mS3Client.putObject(mBucketName, s3Filepath, new File(localFilepath));
                    Log.d(TAG, "Saved " + localFilepath + " to " + s3Filepath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

//        TransferUtility transferUtility = new TransferUtility(mS3Client, mContext);
//        TransferObserver observer = transferUtility.upload(
//            mBucketName, s3Filepath, new java.io.File(localFilepath)
//        );
//        observer.setTransferListener(new TransferListener() {
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                Log.d(TAG, "status: " + state);
//            }
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                Log.d(TAG, "progress: " + id + " , bytesCurrent:" + bytesCurrent+" , bytesTotal:" + bytesTotal);
//            }
//            @Override
//            public void onError(int id, Exception ex) {
//                ex.printStackTrace();
//                Log.d(TAG, ex.getMessage());
//            }
//        });
    }
}
