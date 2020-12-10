package com.example.cardbossapp2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cardlan.utils.ByteUtil;
import com.example.cardbossapp2.util.CardReadWriteUtil;
import com.example.cardbossapp2.util.CardReadWriteUtil2;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_callInitDev,btn_callCardReset;
    TextView tv_initResult,tv_CardSn, tv_CardSnByte;
    CardReadWriteUtil mCardReadWriteUtil;


    String mReadOrWriteCardSnHexStr;//16进制cardSn值

    //read
    EditText mEditxt_sector_read, mEditxt_read_index, mEditxt_read_key, mEditxt_read_key_type;
    Button mBtn_read_card;
    TextView mTxtView_read_result;

    EditText mEditxt_sector_write, mEditxt_write_index, mEditxt_wirte_key, mEditxt_write_key_type, mEditxt_wirte_data;
    Button mBtn_write_card;
    TextView mTxtView_write_statusvalue;
    private String TAG=MainActivity.class.getSimpleName();

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
//read
        mEditxt_sector_read = findViewById(R.id.mEditxt_sector_read);
        mEditxt_read_index = findViewById(R.id.mEditxt_read_index);
        mEditxt_read_key = findViewById(R.id.mEditxt_read_key);
        mEditxt_read_key_type = findViewById(R.id.mEditxt_read_key_type);
        mBtn_read_card = findViewById(R.id.mBtn_read_card);
        mTxtView_read_result=findViewById(R.id.mTxtView_read_result);
//write
        mEditxt_sector_write = findViewById(R.id.mEditxt_sector_write);
        mEditxt_write_index = findViewById(R.id.mEditxt_write_index);
        mEditxt_wirte_key = findViewById(R.id.mEditxt_wirte_key);
        mEditxt_write_key_type = findViewById(R.id.mEditxt_write_key_type);
        mEditxt_wirte_data=findViewById(R.id.mEditxt_wirte_data);
        mBtn_write_card=findViewById(R.id.mBtn_write_card);
        mTxtView_write_statusvalue=findViewById(R.id.mTxtView_write_statusvalue);

    }

    @Override
    protected void onStart() {
        super.onStart();
        btn_callInitDev.setOnClickListener(this);
        btn_callCardReset.setOnClickListener(this);
        mBtn_read_card.setOnClickListener(this);
        mBtn_write_card.setOnClickListener(this);
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
            byte[] cardSnByteArray= mCardReadWriteUtil.getCardResetBytes();
            if (ByteUtil.notNull(cardSnByteArray)) {
                mReadOrWriteCardSnHexStr = ByteUtil.byteArrayToHexString(cardSnByteArray);
                tv_CardSn.setText("cardSn= "+mReadOrWriteCardSnHexStr);
            } else {
                tv_CardSn.setText("cardSn get failed!");
            }

        }else if (v.equals(mBtn_read_card)){
            //read M1 card;
            byte[] cardSnByteArray = mCardReadWriteUtil.getCardResetBytes();
            Log.i(TAG,"read cardSn = "+ByteUtil.byteArrayToHexString(cardSnByteArray));
            if (!ByteUtil.notNull(cardSnByteArray)) {
                Toast.makeText(this, "read not find the card !", Toast.LENGTH_SHORT).show();
                return;
            }else {
                String cardSnHexStringNot=CardReadWriteUtil2.byteNotHexStr(cardSnByteArray);
                Log.i(TAG,"read cardSnHexStringNot = "+cardSnHexStringNot);
                String byte4Sn=cardSnHexStringNot.substring(0,7);//截取前8位
                Log.i(TAG,"read substring 4byte hex = "+byte4Sn);
                String keyA="0A"+byte4Sn+"81";
                Log.i(TAG,"read keyA = "+keyA);

                char readSector=10;//10扇区号
//                char readSector = CardReadWriteUtil2.stringToChar(mEditxt_sector_read.getText().toString());
                char readindex = CardReadWriteUtil2.stringToChar(mEditxt_read_index.getText().toString());
                byte sector = ByteUtil.intToByteTwo(readSector);
                byte index = ByteUtil.intToByteTwo(readindex);
                Log.i(TAG,"sector = "+sector +" index= "+index);
                byte[] readTemp = null;
                // the subsequent reads and writes need to be written using the computed read key
                readTemp = mCardReadWriteUtil.callReadJNI(ByteUtil.byteToHex(sector),
                        ByteUtil.byteToHex(index), keyA, null);
                if (ByteUtil.notNull(readTemp)) {
                    mTxtView_read_result.setText(ByteUtil.byteArrayToHexString(readTemp));
                }
            }

        }else if (v.equals(mBtn_write_card)){
            //action for write to M1_card.
            byte[] cardSnByteArray = mCardReadWriteUtil.getCardResetBytes();
            Log.i(TAG,"cardSn = "+ByteUtil.byteArrayToHexString(cardSnByteArray));
            if (!ByteUtil.notNull(cardSnByteArray)) {
                Toast.makeText(this, "write not find the card !", Toast.LENGTH_SHORT).show();
                return;
            }else {
                String cardSnHexStringNot=CardReadWriteUtil2.byteNotHexStr(cardSnByteArray);
                Log.i(TAG,"write cardSnHexStringNot = "+cardSnHexStringNot);
                String byte4Sn=cardSnHexStringNot.substring(0,7);//截取前8位
                Log.i(TAG,"write substring 4byte hex = "+byte4Sn);
                String write_keyA="0A"+byte4Sn+"81";
                Log.i(TAG,"write_keyA = "+write_keyA);

                String writeSector="10";
//                String writeSector=mEditxt_sector_write.getText().toString().trim();
                String writeIndex= TextUtils.isEmpty(mEditxt_write_index.getText().toString().trim())?"0":mEditxt_write_index.getText().toString().trim();

                //int money= 100000;
                String write_data=mEditxt_wirte_data.getText().toString().trim();
                if (TextUtils.isEmpty(write_data)){
                    Toast.makeText(this, "写入数据不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                char write_data_char=CardReadWriteUtil2.stringToChar(write_data);//转为char/int数据
                String write_data_Hex=ByteUtil.intToHexString(write_data_char);//转为16进制字符串

                int writeResult = mCardReadWriteUtil.callWriteJNI(writeSector, writeIndex, write_data_Hex, write_keyA, null);
                Log.i(TAG,"write StatusOfWrite = "+writeResult);

                if (writeResult == 5) {
                    mTxtView_write_statusvalue.setText("StatusOfWrite is:" + writeResult);
                    Toast.makeText(this, "Writeing successfully !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}