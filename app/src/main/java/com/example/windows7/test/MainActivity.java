package com.example.windows7.test;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Random;

public class MainActivity extends ActionBarActivity {
    public static final Bus BUS = new Bus();
    private static final String TAG = MainActivity.class.getCanonicalName();

    private static Handler handler;

    private ProgressBar progressBar;

    public static Handler getHandler() {
        return handler;
    }

    private static class MyHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "handleMessage/post event");
            BUS.post((Data) msg.obj);
        }
    }

    public static class Data {
        public String param = "dheerajsachan";
    }

    @Subscribe
    public void onData(Data data) {
        Log.e(TAG, "event received");
        Random random = new Random();
        progressBar.setProgress(random.nextInt(100));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(MainActivity.this, MyService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.circularProgressbar);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        int h = bitmap.getHeight();
        int dp = convertPixelsToDp(h);
        progressBar.setLayoutParams(new RelativeLayout.LayoutParams(convertDpToPixel(dp + 30), convertDpToPixel(dp + 30)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        BUS.register(this);
        handler = new MyHander();
    }

    public int convertPixelsToDp(float px) {
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return (int) dp;
    }

    public int convertDpToPixel(float dp) {
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
          handler = null;
          BUS.unregister(this);
    }
}
