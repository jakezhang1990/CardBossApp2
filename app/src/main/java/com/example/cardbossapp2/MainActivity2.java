package com.example.cardbossapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.cardbossapp2.util.TimeThread;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 主页面
 */
public class MainActivity2 extends AppCompatActivity {

    private TextView dateTV;
    TimeThread timeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        dateTV=findViewById(R.id.dateTV);
        timeThread=new TimeThread(dateTV);
        timeThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeThread!=null){
            timeThread.interrupt();
        }
    }
}