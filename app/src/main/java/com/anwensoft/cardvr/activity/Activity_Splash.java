package com.anwensoft.cardvr.activity;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.anwensoft.cardvr.R;
import com.anwensoft.cardvr.service.Service_usb;

public class Activity_Splash extends AppCompatActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();

        Intent service_usb=new Intent(this, Service_usb.class);
        startService(service_usb);
    }
    private void initView() {
        handler= new Handler();
        handler.postDelayed(runnable, 1000);
    }
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Intent intent=new Intent(Activity_Splash.this,Activity_Car_home.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK)
            handler.removeCallbacks(runnable);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
