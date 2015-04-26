package com.example.windows7.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by windows 7 on 4/26/2015.
 */
public class MyService extends Service {
    private static final String TAG = MyService.class.getCanonicalName();
    private ScheduledExecutorService executorService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.getHandler() != null) {
                    boolean test = MainActivity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "sending data");
                            Message completeMessage =
                                    MainActivity.getHandler().obtainMessage(420, new MainActivity.Data());
                            completeMessage.sendToTarget();
                        }
                    });
                    Log.e(TAG, String.valueOf(test));
                } else {
                    Log.w(TAG, "null handler");
                    //stopSelf();
                }
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        if (executorService != null) {
            executorService.shutdownNow();
            try {
                executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
        super.onDestroy();
    }
}
