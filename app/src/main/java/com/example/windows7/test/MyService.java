package com.example.windows7.test;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutorService;
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
    /*    executorService = Executors.newSingleThreadScheduledExecutor();
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
        }, 1, 5, TimeUnit.SECONDS);*/

        final MyLopper thread = new MyLopper();
        thread.start();

        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        executorService1.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (thread.getHandler() != null) {
                        thread.getHandler().post(new Runnable() {
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
                                }
                            }
                        });
                    } else {
                        Log.e(TAG, "null looper");
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }
        });
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

    private static class MyLopper extends Thread {
        public Handler handler;

        public Handler getHandler() {
            return handler;
        }

        @Override
        public void run() {
            try {
                // preparing a looper on current thread
                // the current thread is being detected implicitly
                Looper.prepare();
                // now, the handler will automatically bind to the
                // Looper that is attached to the current thread
                // You don't need to specify the Looper explicitly
                handler = new Handler();
                // After the following line the thread will start
                // running the message loop and will not normally
                // exit the loop unless a problem happens or you
                // quit() the looper (see below)
                Looper.loop();
            } catch (Throwable t) {
                Log.e(TAG, "halted due to an error", t);
            }
        }
    }

    ;
}
