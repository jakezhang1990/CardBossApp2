package com.example.cardbossapp2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cardlan.utils.ByteUtil;
import com.example.cardbossapp2.util.CardReadWriteUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_callInitDev,btn_callCardReset;
    TextView tv_initResult,tv_CardSn, tv_CardSnByte;
    CardReadWriteUtil mCardReadWriteUtil;


    String mReadOrWriteCardSnHexStr;//16进制cardSn值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_callInitDev=findViewById(R.id.btn_callInitDev);
        btn_callCardReset=findViewById(R.id.btn_callCardReset);
        tv_initResult=findViewById(R.id.tv_initResult);
        tv_CardSn=findViewById(R.id.tv_CardSn);
        tv_CardSnByte=findViewById(R.id.tv_CardSnByte);
        mCardReadWriteUtil=new CardReadWriteUtil();

    }

    @Override
    protected void onStart() {
        super.onStart();
        btn_callInitDev.setOnClickListener(this);
        btn_callCardReset.setOnClickListener(this);
    }

    /**
     * 对于同一张卡，充值100元的卡：
     * mReadOrWriteKeyHexStr = ByteUtil.byteArrayToHexString
     *     (terminal.calculateNormalCardKey(resetByte)); 也就是读卡demo：
     *   读取出的结果为：29309D816452
     *
     * mReadOrWriteKeyHexStr = ByteUtil.byteArrayToHexString(resetbyte); 也就是callCardReset demo
     *   读取出的结果为：302951B50800
     */

    @Override
    public void onClick(View v) {
        if (v.equals(btn_callInitDev)){
            //init_device;
            String init_result=mCardReadWriteUtil.initDev();
            tv_initResult.setText(init_result);
        }else if (v.equals(btn_callCardReset)){
            //get card sn
            byte[] cardSn= mCardReadWriteUtil.getCardResetBytes();
            if (ByteUtil.notNull(cardSn)) {
                mReadOrWriteCardSnHexStr = ByteUtil.byteArrayToHexString(cardSn);
                tv_CardSn.setText("cardSn= "+mReadOrWriteCardSnHexStr);
//                tv_CardSnByte.setText(Base64.getEncoder().encodeToString(cardSn));
            } else {
                tv_CardSn.setText("cardSn get failed!");
            }

        }
    }
}