package com.anwensoft.cardvr.activity;

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

public class Activity_Led_Control extends AppCompatActivity {

    final boolean dynamic_DEBUG = false;     //高配版没有动态灯控制;

    MyDataActivity Send_data;
    String locale_language = Locale.getDefault().getLanguage();      //获取当前系统语言;

    private static boolean flag_dynamic = false,flag_light = false,flag_env1 = false,flag_env2 = false,power_flag=true;
    Button button_dynamic,button_light,button_env1,button_env2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(locale_language == "zh")
            setContentView(R.layout.activity_led_control);
        else
            setContentView(R.layout.activity_led_control_en);

        EventBus.getDefault().register(this);
        Send_data = (MyDataActivity)getApplicationContext();

        button_dynamic = (Button) findViewById(R.id.imageButton_led_dynamic);
        button_light = (Button) findViewById(R.id.imageButton_led_light);
        button_env1 = (Button) findViewById(R.id.imageButton_led_env1);
        button_env2 = (Button) findViewById(R.id.imageButton_led_env2);

        //更新状态;
        if (dynamic_DEBUG) {        //高配版没有动态灯和天窗控制;
            update_led_dynamic_Status();
        }
        update_led_light_Status();
        update_led_env1_Status();
        update_led_env2_Status();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventUtil eventUtil){
        if (eventUtil.getMsg().equals("Buffer"))
        {
            byte[] buf= (byte[]) eventUtil.getParam();
            if(buf[5] != 0x7f) {
                if (buf[0] == 0x01)
                    power_flag = false;

                Send_data.setBuf_1(buf[1],(byte)2);

                if (dynamic_DEBUG)
                {
                    if ((buf[1] & ((byte)0x01<<6)) == 0x00)
                    {
                        flag_dynamic = false;
                        update_led_dynamic_Status();
                    } else
                        {
                        flag_dynamic = true;
                        update_led_dynamic_Status();
                    }
                }
                if ((buf[1] & ((byte)0x01<<5)) == 0x00) {
                    flag_light = false;
                    update_led_light_Status();
                } else {
                    flag_light = true;
                    update_led_light_Status();
                }

                if ((buf[1] & ((byte)0x01<<4)) == 0x00) {
                    flag_env1 = false;
                    update_led_env1_Status();
                } else {
                    flag_env1 = true;
                    update_led_env1_Status();
                }
                if ((buf[1] & ((byte)0x01<<3)) == 0x00) {
                    flag_env2 = false;
                    update_led_env2_Status();
                } else {
                    flag_env2 = true;
                    update_led_env2_Status();
                }
            }
        }
    }

    private void update_led_dynamic_Status() {
        if(flag_dynamic) {
            Drawable dra;
            if(locale_language == "zh")
                dra = getResources().getDrawable(R.drawable.activity_led_dynamic_hold);
            else
                dra = getResources().getDrawable(R.drawable.activity_led_dynamic_hold_en);
            button_dynamic.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data |= ((byte)0x01<<6);
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
                dra = getResources().getDrawable(R.drawable.activity_led_dynamic);
            else
                dra = getResources().getDrawable(R.drawable.activity_led_dynamic_en);
            button_dynamic.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data &= ~((byte)0x01<<6);
            Send_data.setBuf_1(data,(byte)2);
            if(power_flag)
                Send_data.setBuf_1((byte)0xff,(byte)7);
            else
                Send_data.setBuf_1((byte)0x00,(byte)7);
            power_flag = false;

            EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
        }
    }
    private void update_led_light_Status()
    {
        if(flag_light) {
            Drawable dra;
            if(locale_language == "zh")
                dra = getResources().getDrawable(R.drawable.activity_led_light_hold);
            else
                dra = getResources().getDrawable(R.drawable.activity_led_light_hold_en);
            button_light.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data |= ((byte)0x01<<5);
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
                dra = getResources().getDrawable(R.drawable.activity_led_light);
            else
                dra = getResources().getDrawable(R.drawable.activity_led_light_en);
            button_light.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data &= ~((byte)0x01<<5);
            Send_data.setBuf_1(data,(byte)2);
            if(power_flag)
                Send_data.setBuf_1((byte)0xff,(byte)7);
            else
                Send_data.setBuf_1((byte)0x00,(byte)7);
            power_flag = false;

            EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
        }
    }
    private void update_led_env1_Status()
    {
        if(flag_env1) {
            Drawable dra;
            if(locale_language == "zh")
                dra = getResources().getDrawable(R.drawable.activity_led_env1_hold);
            else
                dra = getResources().getDrawable(R.drawable.activity_led_env1_hold_en);
            button_env1.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data |= ((byte)0x01<<4);
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
                dra = getResources().getDrawable(R.drawable.activity_led_env1);
            else
                dra = getResources().getDrawable(R.drawable.activity_led_env1_en);
            button_env1.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data &= ~((byte)0x01<<4);
            Send_data.setBuf_1(data,(byte)2);
            if(power_flag)
                Send_data.setBuf_1((byte)0xff,(byte)7);
            else
                Send_data.setBuf_1((byte)0x00,(byte)7);
            power_flag = false;

            EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
        }
    }
    private void update_led_env2_Status()
    {
        if(flag_env2) {
            Drawable dra;
            if(locale_language == "zh")
                dra = getResources().getDrawable(R.drawable.activity_led_env2_hold);
            else
                dra = getResources().getDrawable(R.drawable.activity_led_env2_hold_en);
            button_env2.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data |= ((byte)0x01<<3);
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
                dra = getResources().getDrawable(R.drawable.activity_led_env2);
            else
                dra = getResources().getDrawable(R.drawable.activity_led_env2_en);
            button_env2.setBackground(dra);

            byte data = Send_data.getBuf_1((byte)2);
            data &= ~((byte)0x01<<3);
            Send_data.setBuf_1(data,(byte)2);
            if(power_flag)
                Send_data.setBuf_1((byte)0xff,(byte)7);
            else
                Send_data.setBuf_1((byte)0x00,(byte)7);
            power_flag = false;

            EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_1()));
        }
    }

    public void led_dynamicMessage(View view){
        flag_dynamic = !(flag_dynamic);
        power_flag = true;
        update_led_dynamic_Status();
    }
    public void led_lightMessage(View view){
        flag_light = !(flag_light);
        power_flag = true;
        update_led_light_Status();
    }
    public void led_env1Message(View view){
        flag_env1 = !(flag_env1);
        power_flag = true;
        update_led_env1_Status();
    }
    public void led_env2Message(View view){
        flag_env2 = !(flag_env2);
        power_flag = true;
        update_led_env2_Status();
    }
}
