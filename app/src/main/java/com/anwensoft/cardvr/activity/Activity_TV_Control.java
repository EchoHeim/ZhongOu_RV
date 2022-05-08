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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

public class Activity_TV_Control extends AppCompatActivity
    implements View.OnTouchListener {

    MyDataActivity Send_data;
    String locale_language = Locale.getDefault().getLanguage();      //获取当前系统语言;

    private static boolean flag_tv_power = false,power_flag=true;
    Button button_tv_power,TV_up,TV_down;
    Thread thread_tv_up = null,thread_tv_down = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(locale_language == "zh")
            setContentView(R.layout.activity_tv_control);
        else
            setContentView(R.layout.activity_tv_control_en);

        EventBus.getDefault().register(this);
        Send_data = (MyDataActivity)getApplicationContext();

        button_tv_power = (Button)findViewById(R.id.imageButton_tv_power);
        TV_up=(Button) findViewById(R.id.imageButton_tv_up);
        TV_down=(Button) findViewById(R.id.imageButton_tv_down);

        TV_up.setOnTouchListener(this);
        TV_down.setOnTouchListener(this);

        update_tv_power_Status();   //更新状态;

        thread_tv_up = new Thread(new Runnable() {
            public void run() {
                while (!thread_tv_up.currentThread().interrupted()) {
                    try {
                        thread_tv_up.sleep(200);
                        Send_data.setBuf_1((byte)0x03,(byte)0);
                        Send_data.setBuf_1((byte)0xff,(byte)7);
                        EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();    // TODO Auto-generated catch block
                        thread_tv_up.interrupt();  //防止一些不重要的异常抛出
                    }
                }
            }
        });
        thread_tv_down = new Thread(new Runnable() {
            public void run() {
                while (!thread_tv_down.currentThread().interrupted()) {
                    try {
                        thread_tv_down.sleep(200);
                        Send_data.setBuf_1((byte)0x04,(byte)0);
                        Send_data.setBuf_1((byte)0xff,(byte)7);
                        EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();    // TODO Auto-generated catch block
                        thread_tv_down.interrupt(); //防止一些不重要的异常抛出
                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventUtil eventUtil){
        if (eventUtil.getMsg().equals("Buffer"))
        {
            byte[] buf= (byte[]) eventUtil.getParam();
            if(buf[5] != 0x7f) {
                if(buf[0] == 0x01)
                    power_flag=false;

                Send_data.setBuf_1(buf[1],(byte)2);

                if ((buf[1] & ((byte)0x01<<2)) == 0x00)
                {
                    flag_tv_power = false;
                    update_tv_power_Status();
                }
                else
                {
                    flag_tv_power = true;
                    update_tv_power_Status();
                }
            }
        }
    }

    private void update_tv_power_Status()
    {
        Drawable dra;
        if(flag_tv_power) {
            if(locale_language == "zh")
                dra = getResources().getDrawable(R.drawable.activity_tv_power_hold);
            else
                dra = getResources().getDrawable(R.drawable.activity_tv_power_hold_en);
            button_tv_power.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data |= ((byte)0x01<<2);
            Send_data.setBuf_1(data,(byte)2);
            if(power_flag)
                Send_data.setBuf_1((byte)0xff,(byte)7);
            else
                Send_data.setBuf_1((byte)0x00,(byte)7);
            power_flag = false;
            EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
        }
        else
        {
            if(locale_language == "zh")
                dra = getResources().getDrawable(R.drawable.activity_tv_power);
            else
                dra = getResources().getDrawable(R.drawable.activity_tv_power_en);
            button_tv_power.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data &= ~((byte)0x01<<2);
            Send_data.setBuf_1(data,(byte)2);
            if(power_flag)
                Send_data.setBuf_1((byte)0xff,(byte)7);
            else
                Send_data.setBuf_1((byte)0x00,(byte)7);
            power_flag = false;
            EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
        }
    }
    public void tv_powerMessage(View view){
        flag_tv_power = !(flag_tv_power);
        power_flag = true;
        update_tv_power_Status();
    }

    @Override
    public boolean onTouch(View v, MotionEvent e)
    {
        if(e.getAction() == MotionEvent.ACTION_DOWN)
        {
            Drawable dra;
            switch (v.getId())
            {
                case R.id.imageButton_tv_up:
                    if(locale_language == "zh")
                        dra = getResources().getDrawable(R.drawable.icon_tv_up_press);
                    else
                        dra = getResources().getDrawable(R.drawable.icon_tv_up_press_en);
                    TV_up.setBackground(dra);
                    thread_tv_up.start(); //开始线程
                    break;
                case R.id.imageButton_tv_down:
                    if(locale_language == "zh")
                        dra = getResources().getDrawable(R.drawable.icon_tv_down_press);
                    else
                        dra = getResources().getDrawable(R.drawable.icon_tv_down_press_en);
                    TV_down.setBackground(dra);
                    thread_tv_down.start(); //开始线程
                    break;
            }
        }
        else if (e.getAction() == MotionEvent.ACTION_UP)
        {
            Drawable dra;
            switch (v.getId()) {
                case R.id.imageButton_tv_up:
                    if(locale_language == "zh")
                        dra = getResources().getDrawable(R.drawable.icon_tv_up);
                    else
                        dra = getResources().getDrawable(R.drawable.icon_tv_up_en);
                    TV_up.setBackground(dra);
                    thread_tv_up.interrupt();  //关闭线程;
                    Send_data.setBuf_1((byte)0x00,(byte)0);
                    Send_data.setBuf_1((byte)0xff,(byte)7);
                    EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));

                    break;
                case R.id.imageButton_tv_down:
                    if(locale_language == "zh")
                        dra = getResources().getDrawable(R.drawable.icon_tv_down);
                    else
                        dra = getResources().getDrawable(R.drawable.icon_tv_down_en);
                    TV_down.setBackground(dra);
                    thread_tv_down.interrupt(); //关闭线程;
                    Send_data.setBuf_1((byte)0x00,(byte)0);
                    Send_data.setBuf_1((byte)0xff,(byte)7);
                    EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));

                    break;
            }
        }
        return true;
    }
}
