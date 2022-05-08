package com.anwensoft.cardvr.activity;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.anwensoft.cardvr.R;
import com.anwensoft.cardvr.adapter.EventUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

public class Activity_High_pitch extends AppCompatActivity
{
    MyDataActivity Send_data;
    String locale_language = Locale.getDefault().getLanguage();      //获取当前系统语言;

    double multiplier_num_light = 4.04,multiplier_num_color = 6.01;
    private static boolean light_power_flag = true, light_flag = false;
    private static int progress_color_num = 1,progress_display_num = 0,progress_front_num = 0,progress_behind_num = 0;
    ImageView led_env;
    TextView color_num_display,display_num,front_num,behind_num;
    private VerticalSeekBar ChangeColor_SeekBar = null,Display_SeekBar = null,Front_SeekBar = null,Behind_SeekBar = null;
    private CheckBox light_Check = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(locale_language == "zh")
            setContentView(R.layout.activity_high_pitch);
        else
            setContentView(R.layout.activity_high_pitch_en);

        EventBus.getDefault().register(this);
        Send_data = (MyDataActivity)getApplicationContext();

        led_env=(ImageView)findViewById(R.id.imageView_light);
        color_num_display=(TextView)findViewById(R.id.TextView_Color);
        display_num=(TextView)findViewById(R.id.TextView_Display);
        front_num=(TextView)findViewById(R.id.TextView_Front);
        behind_num=(TextView)findViewById(R.id.TextView_Behind);

        ChangeColor_SeekBar = (VerticalSeekBar) findViewById(R.id.Seek_Bar_ChangeColor);
        ChangeColor_SeekBar.setOnSeekBarChangeListener(ChangeColor_SeekBar_ChangeListener);

        Display_SeekBar = (VerticalSeekBar) findViewById(R.id.Seek_Bar_Display);
        Display_SeekBar.setOnSeekBarChangeListener(Display_SeekBar_ChangeListener);

        Front_SeekBar = (VerticalSeekBar) findViewById(R.id.Seek_Bar_Front);
        Front_SeekBar.setOnSeekBarChangeListener(Front_SeekBar_ChangeListener);

        Behind_SeekBar = (VerticalSeekBar) findViewById(R.id.Seek_Bar_Behind);
        Behind_SeekBar.setOnSeekBarChangeListener(Behind_SeekBar_ChangeListener);

        light_Check=(CheckBox)findViewById(R.id.light_checkbox);
        light_Check.setOnCheckedChangeListener(light_CheckBox_ChangeListener);
        light_Check.setOnTouchListener(new CheckBox.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        light_power_flag=true;
                        break;
                    case MotionEvent.ACTION_UP:
                        light_power_flag=true;
                        break;
                }
                return false;
            }
        });

        progress_display_num = Send_data.getBuf_2((byte)0);
        progress_front_num = Send_data.getBuf_2((byte)1);
        progress_behind_num = Send_data.getBuf_2((byte)2);
        progress_color_num = Send_data.getBuf_2((byte)3);
        update_light_status((byte)0xf0);
        light_Check.setChecked(light_flag);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventUtil eventUtil){
        if (eventUtil.getMsg().equals("Buffer"))
        {
            byte[] buf = (byte[]) eventUtil.getParam();

            if(buf[5] == 0x7f) {
                if(buf[6] == 0x00)
                {
                    light_flag = false;
                    Send_data.setBuf_2((byte)0x00,(byte)4);
                    light_Check.setChecked(light_flag);
                }else{
                    light_flag = true;
                    Send_data.setBuf_2((byte)0x80,(byte)4);
                    light_Check.setChecked(light_flag);
                }
                light_Check.setChecked(light_flag);

                progress_display_num = (int)buf[1];
                progress_front_num = (int)buf[2];
                progress_behind_num = (int)buf[3];
                progress_color_num = (int)buf[4];

                update_light_status((byte)0xf0);
            }
        }
    }

    private CheckBox.OnCheckedChangeListener light_CheckBox_ChangeListener= new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                light_flag=true;
                Send_data.setBuf_2((byte)0x80,(byte)4);
                Send_data.setBuf_2((byte)0xff,(byte)7);
/*
                if(light_power_flag)
                {
                    Send_data.setBuf_2((byte)0xff,(byte)7);
                }else
                {
                    Send_data.setBuf_2((byte)0x00,(byte)7);
                }*/
                EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_2()));
                update_light_status((byte)0xe0);
                light_power_flag=false;
                Send_data.setBuf_2((byte)0x00,(byte)7);
            }
            else {
                light_flag=false;
                Send_data.setBuf_2((byte)0x00,(byte)4);
                Send_data.setBuf_2((byte)0xff,(byte)7);

                progress_front_num = progress_display_num;
                progress_behind_num = progress_display_num;
/*
                if(light_power_flag)
                {
                    Send_data.setBuf_2((byte)0xff,(byte)7);
                }else
                {
                    Send_data.setBuf_2((byte)0x00,(byte)7);
                }*/
                EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_2()));
                update_light_status((byte)0xe0);
                light_power_flag=false;
                Send_data.setBuf_2((byte)0x00,(byte)7);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener Display_SeekBar_ChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
        {
            if(Behind_SeekBar.Power_flag||Front_SeekBar.Power_flag||Display_SeekBar.Power_flag)
            {
                Send_data.setBuf_2((byte)0xff,(byte)7);
            }else
            {
                Send_data.setBuf_2((byte)0x00,(byte)7);
            }
            if(light_flag ) {
                progress_display_num = progress;
                update_light_status((byte)0x80);
            }else
            {
                progress_display_num = progress;
                progress_behind_num = progress_display_num;
                progress_front_num = progress_display_num;

                update_light_status((byte)0xe0);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener Front_SeekBar_ChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
        {
            if(Behind_SeekBar.Power_flag||Front_SeekBar.Power_flag||Display_SeekBar.Power_flag)
            {
                Send_data.setBuf_2((byte)0xff,(byte)7);
            }else
            {
                Send_data.setBuf_2((byte)0x00,(byte)7);
            }

            if(light_flag ) {
                progress_front_num = progress;
                update_light_status((byte)0x40);
            } else
            {
                progress_display_num = progress;
                progress_behind_num = progress_display_num;
                progress_front_num = progress_display_num;
                update_light_status((byte)0xe0);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener Behind_SeekBar_ChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
        {
            if(light_flag)
            {
                progress_behind_num=progress;

                if(Behind_SeekBar.Power_flag)
                {
                    Send_data.setBuf_2((byte)0xff,(byte)7);
                }else
                {
                    Send_data.setBuf_2((byte)0x00,(byte)7);
                }
                update_light_status((byte)0x20);
            } else
            {
                progress_display_num = progress;
                progress_behind_num = progress_display_num;
                progress_front_num = progress_display_num;

                if(Behind_SeekBar.Power_flag||Front_SeekBar.Power_flag||Display_SeekBar.Power_flag)
                {
                    Send_data.setBuf_2((byte)0xff,(byte)7);
                }else
                {
                    Send_data.setBuf_2((byte)0x00,(byte)7);
                }
                update_light_status((byte)0xe0);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener ChangeColor_SeekBar_ChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
        {
            progress_color_num = progress;

            if(ChangeColor_SeekBar.Power_flag)
            {
                Send_data.setBuf_2((byte)0xff,(byte)7);
            }else
            {
                Send_data.setBuf_2((byte)0x00,(byte)7);
            }
            update_light_status((byte)0x10);
        }
    };

    private void update_light_status(byte num)
    {
        if ((num&0x80) == 0x80)
        {
            Display_SeekBar.setProgress(progress_display_num);
            display_num.setText(""+progress_display_num+"");
            display_num.setTranslationY(-(int)(multiplier_num_light*progress_display_num));
            Send_data.setBuf_2((byte)progress_display_num,(byte)0);
        }
        if ((num&0x40) == 0x40)
        {
            Front_SeekBar.setProgress(progress_front_num);
            front_num.setText(""+progress_front_num+"");
            front_num.setTranslationY(-(int)(multiplier_num_light*progress_front_num));
            Send_data.setBuf_2((byte)progress_front_num,(byte)1);
        }
        if ((num&0x20) == 0x20)
        {
            Behind_SeekBar.setProgress(progress_behind_num);
            behind_num.setText(""+progress_behind_num+"");
            behind_num.setTranslationY(-(int)(multiplier_num_light*progress_behind_num));
            Send_data.setBuf_2((byte)progress_behind_num,(byte)2);
        }
        if ((num&0x10) == 0x10)
        {
            ChangeColor(progress_color_num);
            ChangeColor_SeekBar.setProgress(progress_color_num);
            Send_data.setBuf_2((byte)progress_color_num,(byte)3);
        }
        EventBus.getDefault().post(new EventUtil("Send_Buffer",Send_data.getWholeBuf_2()));
    }

    private void ChangeColor(int progress)
    {
        Drawable dra;
        progress_color_num = progress;
        String str = String.format("%02d", progress_color_num);

        if(progress_color_num == 0)
            color_num_display.setText(" "+"01"+"");
        else
            color_num_display.setText(" "+str+"");
        color_num_display.setTranslationY(-(int)(multiplier_num_color*progress_color_num));
        switch (progress)
        {
            case 1:
                dra = getResources().getDrawable(R.drawable.icon_lable_big);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_1);
                led_env.setBackground(dra);break;
            case 2:
                dra = getResources().getDrawable(R.drawable.view_2);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_2);
                led_env.setBackground(dra);break;
            case 3:
                dra = getResources().getDrawable(R.drawable.view_3);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_3);
                led_env.setBackground(dra);break;
            case 4:
                dra = getResources().getDrawable(R.drawable.view_4);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_4);
                led_env.setBackground(dra);break;
            case 5:
                dra = getResources().getDrawable(R.drawable.view_5);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_5);
                led_env.setBackground(dra);break;
            case 6:
                dra = getResources().getDrawable(R.drawable.view_6);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_6);
                led_env.setBackground(dra);break;
            case 7:
                dra = getResources().getDrawable(R.drawable.view_7);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_7);
                led_env.setBackground(dra);break;
            case 8:
                dra = getResources().getDrawable(R.drawable.view_8);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_8);
                led_env.setBackground(dra);break;
            case 9:
                dra = getResources().getDrawable(R.drawable.view_9);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_9);
                led_env.setBackground(dra);break;
            case 10:
                dra = getResources().getDrawable(R.drawable.view_10);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_10);
                led_env.setBackground(dra);break;
            case 11:
                dra = getResources().getDrawable(R.drawable.view_11);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_11);
                led_env.setBackground(dra);break;
            case 12:
                dra = getResources().getDrawable(R.drawable.view_12);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_12);
                led_env.setBackground(dra);break;
            case 13:
                dra = getResources().getDrawable(R.drawable.view_13);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_13);
                led_env.setBackground(dra);break;
            case 14:
                dra = getResources().getDrawable(R.drawable.view_14);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_14);
                led_env.setBackground(dra);break;
            case 15:
                dra = getResources().getDrawable(R.drawable.view_15);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_15);
                led_env.setBackground(dra);break;
            case 16:
                dra = getResources().getDrawable(R.drawable.view_16);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_16);
                led_env.setBackground(dra);break;
            case 17:
                dra = getResources().getDrawable(R.drawable.view_17);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_17);
                led_env.setBackground(dra);break;
            case 18:
                dra = getResources().getDrawable(R.drawable.view_18);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_18);
                led_env.setBackground(dra);break;
            case 19:
                dra = getResources().getDrawable(R.drawable.view_19);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_19);
                led_env.setBackground(dra);break;
            case 20:
                dra = getResources().getDrawable(R.drawable.view_20);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_20);
                led_env.setBackground(dra);break;
            case 21:
                dra = getResources().getDrawable(R.drawable.view_21);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_21);
                led_env.setBackground(dra);break;
            case 22:
                dra = getResources().getDrawable(R.drawable.view_22);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_22);
                led_env.setBackground(dra);break;
            case 23:
                dra = getResources().getDrawable(R.drawable.view_23);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_23);
                led_env.setBackground(dra);break;
            case 24:
                dra = getResources().getDrawable(R.drawable.view_24);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_24);
                led_env.setBackground(dra);break;
            case 25:
                dra = getResources().getDrawable(R.drawable.view_25);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_25);
                led_env.setBackground(dra);break;
            case 26:
                dra = getResources().getDrawable(R.drawable.view_26);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_26);
                led_env.setBackground(dra);break;
            case 27:
                dra = getResources().getDrawable(R.drawable.view_27);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_27);
                led_env.setBackground(dra);break;
            case 28:
                dra = getResources().getDrawable(R.drawable.view_28);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_28);
                led_env.setBackground(dra);break;
            case 29:
                dra = getResources().getDrawable(R.drawable.view_29);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_29);
                led_env.setBackground(dra);break;
            case 30:
                dra = getResources().getDrawable(R.drawable.view_30);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_30);
                led_env.setBackground(dra);break;
            case 31:
                dra = getResources().getDrawable(R.drawable.view_31);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_31);
                led_env.setBackground(dra);break;
            case 32:
                dra = getResources().getDrawable(R.drawable.view_32);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_32);
                led_env.setBackground(dra);break;
            case 33:
                dra = getResources().getDrawable(R.drawable.view_33);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_33);
                led_env.setBackground(dra);break;
            case 34:
                dra = getResources().getDrawable(R.drawable.view_34);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_34);
                led_env.setBackground(dra);break;
            case 35:
                dra = getResources().getDrawable(R.drawable.view_35);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_35);
                led_env.setBackground(dra);break;
            case 36:
                dra = getResources().getDrawable(R.drawable.view_36);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_36);
                led_env.setBackground(dra);break;
            case 37:
                dra = getResources().getDrawable(R.drawable.view_37);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_37);
                led_env.setBackground(dra);break;
            case 38:
                dra = getResources().getDrawable(R.drawable.view_38);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_38);
                led_env.setBackground(dra);break;
            case 39:
                dra = getResources().getDrawable(R.drawable.view_39);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_39);
                led_env.setBackground(dra);break;
            case 40:
                dra = getResources().getDrawable(R.drawable.view_40);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_40);
                led_env.setBackground(dra);break;
            case 41:
                dra = getResources().getDrawable(R.drawable.view_41);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_41);
                led_env.setBackground(dra);break;
            case 42:
                dra = getResources().getDrawable(R.drawable.view_42);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_42);
                led_env.setBackground(dra);break;
            case 43:
                dra = getResources().getDrawable(R.drawable.view_43);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_43);
                led_env.setBackground(dra);break;
            case 44:
                dra = getResources().getDrawable(R.drawable.view_44);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_44);
                led_env.setBackground(dra);break;
            case 45:
                dra = getResources().getDrawable(R.drawable.view_45);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_45);
                led_env.setBackground(dra);break;
            case 46:
                dra = getResources().getDrawable(R.drawable.view_46);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_46);
                led_env.setBackground(dra);break;
            case 47:
                dra = getResources().getDrawable(R.drawable.view_47);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_47);
                led_env.setBackground(dra);break;
            case 48:
                dra = getResources().getDrawable(R.drawable.view_48);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_48);
                led_env.setBackground(dra);break;
            case 49:
                dra = getResources().getDrawable(R.drawable.view_49);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_49);
                led_env.setBackground(dra);break;
            case 50:
                dra = getResources().getDrawable(R.drawable.view_50);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_50);
                led_env.setBackground(dra);break;
            case 51:
                dra = getResources().getDrawable(R.drawable.view_51);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_51);
                led_env.setBackground(dra);break;
            case 52:
                dra = getResources().getDrawable(R.drawable.view_52);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_52);
                led_env.setBackground(dra);break;
            case 53:
                dra = getResources().getDrawable(R.drawable.view_53);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_53);
                led_env.setBackground(dra);break;
            case 54:
                dra = getResources().getDrawable(R.drawable.view_54);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_54);
                led_env.setBackground(dra);break;
            case 55:
                dra = getResources().getDrawable(R.drawable.view_55);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_55);
                led_env.setBackground(dra);break;
            case 56:
                dra = getResources().getDrawable(R.drawable.view_56);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_56);
                led_env.setBackground(dra);break;
            case 57:
                dra = getResources().getDrawable(R.drawable.view_57);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_57);
                led_env.setBackground(dra);break;
            case 58:
                dra = getResources().getDrawable(R.drawable.view_58);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_58);
                led_env.setBackground(dra);break;
            case 59:
                dra = getResources().getDrawable(R.drawable.view_59);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_59);
                led_env.setBackground(dra);break;
            case 60:
                dra = getResources().getDrawable(R.drawable.view_60);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_60);
                led_env.setBackground(dra);break;
            case 61:
                dra = getResources().getDrawable(R.drawable.view_61);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_61);
                led_env.setBackground(dra);break;
            case 62:
                dra = getResources().getDrawable(R.drawable.view_62);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_62);
                led_env.setBackground(dra);break;
            case 63:
                dra = getResources().getDrawable(R.drawable.view_63);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_63);
                led_env.setBackground(dra);break;
            case 64:
                dra = getResources().getDrawable(R.drawable.view_64);
                color_num_display.setBackground(dra);
                dra = getResources().getDrawable(R.drawable.color_64);
                led_env.setBackground(dra);break;
        }
    }
}
