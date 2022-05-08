package com.anwensoft.cardvr.activity;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.anwensoft.cardvr.R;
import com.anwensoft.cardvr.adapter.EventUtil;

import org.greenrobot.eventbus.EventBus;
import java.util.Locale;

public class Activity_Window_Control extends AppCompatActivity
        implements View.OnTouchListener {

    MyDataActivity Send_data;
    String locale_language = Locale.getDefault().getLanguage();      //获取当前系统语言;

    Button window_on,window_off;
    Thread thread_on = null,thread_off = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(locale_language == "zh")
            setContentView(R.layout.activity_window_control);
        else
            setContentView(R.layout.activity_window_control_en);

        Send_data = (MyDataActivity)getApplicationContext();

        window_on=(Button) findViewById(R.id.imageButton_window_on);
        window_off=(Button) findViewById(R.id.imageButton_window_off);

        window_on.setOnTouchListener(this);
        window_off.setOnTouchListener(this);

        thread_on = new Thread(new Runnable() {
            public void run() {
                while (!thread_on.currentThread().interrupted()) {
                    try {
                        thread_on.sleep(200);
                        Send_data.setBuf_1((byte)0x01,(byte)0);
                        Send_data.setBuf_1((byte)0xff,(byte)7);
                        EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();    // TODO Auto-generated catch block
                        thread_on.interrupt();  //防止一些不重要的异常抛出
                    }
                }
            }
        });
        thread_off = new Thread(new Runnable() {
            public void run() {
                while (!thread_off.currentThread().interrupted()) {
                    try {
                        thread_off.sleep(200);
                        Send_data.setBuf_1((byte)0x02,(byte)0);
                        Send_data.setBuf_1((byte)0xff,(byte)7);
                        EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();    // TODO Auto-generated catch block
                        thread_off.interrupt(); //防止一些不重要的异常抛出
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent e)
    {
        if(e.getAction() == MotionEvent.ACTION_DOWN)
        {
            Drawable dra;
            switch (v.getId())
            {
                case R.id.imageButton_window_on:
                    if(locale_language == "zh")
                        dra = getResources().getDrawable(R.drawable.icon_window_on_press);
                    else
                        dra = getResources().getDrawable(R.drawable.icon_window_on_press_en);
                    window_on.setBackground(dra);
                    thread_on.start(); //开始线程
                    break;
                case R.id.imageButton_window_off:
                    if(locale_language == "zh")
                        dra = getResources().getDrawable(R.drawable.icon_window_off_press);
                    else
                        dra = getResources().getDrawable(R.drawable.icon_window_off_press_en);
                    window_off.setBackground(dra);
                    thread_off.start(); //开始线程
                    break;
            }
        }
        else if (e.getAction() == MotionEvent.ACTION_UP)
        {
            Drawable dra;
            switch (v.getId()) {
                case R.id.imageButton_window_on:
                    if(locale_language == "zh")
                        dra = getResources().getDrawable(R.drawable.icon_window_on);
                    else
                        dra = getResources().getDrawable(R.drawable.icon_window_on_en);
                    window_on.setBackground(dra);
                    thread_on.interrupt();  //关闭线程;
                    Send_data.setBuf_1((byte)0x00,(byte)0);
                    Send_data.setBuf_1((byte)0xff,(byte)7);
                    EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
                    break;
                case R.id.imageButton_window_off:
                    if(locale_language == "zh")
                        dra = getResources().getDrawable(R.drawable.icon_window_off);
                    else
                        dra = getResources().getDrawable(R.drawable.icon_window_off_en);
                    window_off.setBackground(dra);
                    thread_off.interrupt(); //关闭线程;
                    Send_data.setBuf_1((byte)0x00,(byte)0);
                    Send_data.setBuf_1((byte)0xff,(byte)7);
                    EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
                    break;
            }
        }
        return true;
    }
}
