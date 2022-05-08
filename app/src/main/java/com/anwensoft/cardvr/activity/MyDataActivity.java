package com.anwensoft.cardvr.activity;

import android.app.Application;

public class MyDataActivity extends Application {

    private static byte send_buf_1[] = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x55,(byte)0x00};
    private static byte send_buf_2[] = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xaa,(byte)0x00};

//    private static byte[] send_buf_1 = new byte[8];
 //   private static byte[] send_buf_2 = new byte[8];

    public void setBuf_1(byte data,byte location){
        this.send_buf_1[location] = data;
    }

    public byte getBuf_1(byte location)
    {
        return send_buf_1[location];
    }

    public void setBuf_2(byte data,byte location){
        this.send_buf_2[location] = data;
    }

    public byte getBuf_2(byte location) {
        return send_buf_2[location];
    }

    public byte[] getWholeBuf_1(){
        return send_buf_1;
    }

    public byte[] getWholeBuf_2(){
        return send_buf_2;
    }
}
