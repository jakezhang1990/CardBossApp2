package com.example.cardbossapp2.util;

import android.annotation.SuppressLint;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Handler;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 日期更新
 * Date: 2020/12/15 18:04
 */
public class TimeThread extends Thread{

    public TextView tvDate;
    private int msgKey1=22;

    public TimeThread(TextView tvDate){
        this.tvDate=tvDate;
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(1000);
                Message msg=new Message();
                msg.what=msgKey1;
                mHandler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (true);
    }


    @SuppressLint("HandlerLeak")
    private android.os.Handler mHandler=new android.os.Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what=msg.what;
            if (what==msgKey1){
                SimpleDateFormat sdf=new SimpleDateFormat("YYYY/MM/dd HH:mm ", Locale.CHINA);
                String date=sdf.format(new Date());
                tvDate.setText(date);
            }
        }
    };
}
