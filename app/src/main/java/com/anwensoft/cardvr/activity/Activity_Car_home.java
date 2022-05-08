package com.anwensoft.cardvr.activity;



import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anwensoft.cardvr.R;
import com.anwensoft.cardvr.adapter.EventUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;


/*
*
* 中欧房车双屏控制App
* 版本1：去掉逆变器、天窗、动态灯；
* 版本2：去掉逆变器、旋转高音、动态灯；
*
* */
public class Activity_Car_home extends AppCompatActivity {

    final boolean inverter_DEBUG = false;     //高配版没有逆变器控制;

    MyDataActivity Send_data;
    String locale_language = Locale.getDefault().getLanguage();      //获取当前系统语言;

    private static boolean flag_inverter = false,power_flag = true;
    Button button_inverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(locale_language == "zh")
            setContentView(R.layout.activity_car_home);
        else
            setContentView(R.layout.activity_car_home_en);

        EventBus.getDefault().register(this);
        Send_data = (MyDataActivity)getApplicationContext();

        button_inverter = (Button)findViewById(R.id.imageButton_inverter);
        if (inverter_DEBUG) {
            update_inverter_Status();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventUtil eventUtil){
        if (eventUtil.getMsg().equals("Buffer"))
        {
            byte[] buf = (byte[]) eventUtil.getParam();

            if(buf[5] != 0x7f) {
                if(buf[0] == 0x01)
                    power_flag=false;

                Send_data.setBuf_1(buf[1],(byte)2);

                if(inverter_DEBUG) {
                    if ((buf[1] & ((byte)0x01<<7)) == 0x00)
                    {
                        flag_inverter = false;
                        update_inverter_Status();
                    } else
                        {
                        flag_inverter = true;
                        update_inverter_Status();
                    }
                }
            }
        }
    }

    private void update_inverter_Status()
    {
        if(flag_inverter) {
            Drawable dra;
            if(locale_language == "zh")
                dra = getResources().getDrawable(R.drawable.activity_inverter_hold);
            else
                dra = getResources().getDrawable(R.drawable.activity_inverter_hold_en);

            button_inverter.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data |= ((byte)0x01<<7);
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
            Drawable dra;
            if(locale_language == "zh")
                dra = getResources().getDrawable(R.drawable.activity_inverter);
            else
                dra = getResources().getDrawable(R.drawable.activity_inverter_en);
            button_inverter.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data &= ~((byte)0x01<<7);
            Send_data.setBuf_1(data,(byte)2);
            if(power_flag)
                Send_data.setBuf_1((byte)0xff,(byte)7);
            else
                Send_data.setBuf_1((byte)0x00,(byte)7);
            power_flag = false;

            EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
        }
    }
    public void ledMessage(View view){
        //do something in response to button

        Intent intent = new Intent(this, Activity_Led_Control.class);
        startActivity(intent);
    }

    public void high_pitchMessage(View view){
        //do something in response to button

        Intent intent = new Intent(this,Activity_High_pitch.class);
        startActivity(intent);
    }

    public void tvMessage(View view){
        //do something in response to button

        Intent intent = new Intent(this, Activity_TV_Control.class);
        startActivity(intent);
    }

    public void windowMessage(View view){
        //do something in response to button
        Intent intent = new Intent(this, Activity_Window_Control.class);
        startActivity(intent);
    }
    public void inverterMessage(View view){
        flag_inverter = !(flag_inverter);
        power_flag = true;
        update_inverter_Status();
    }
}
