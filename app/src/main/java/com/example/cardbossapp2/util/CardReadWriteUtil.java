package com.example.cardbossapp2.util;

import android.util.Log;

import com.cardlan.twoshowinonescreen.CardLanStandardBus;
import com.cardlan.twoshowinonescreen.DeviceCardConfig;
import com.cardlan.utils.ByteUtil;

/**
 * @Description: java类作用描述
 * @Author: jakezhang
 * @CreateDate: 2020/11/22 18:00
 */
public class CardReadWriteUtil {

    private CardLanStandardBus mCardLanDevCtrl;

    private static final int S_Reset_buffer_size = 32;

    private int mInitStatus = -1;
    private boolean mHasInitDev;
    private boolean mHasGetResetBytes = false;






    private String TAG=CardReadWriteUtil.class.getSimpleName();


    public CardReadWriteUtil() {
        mCardLanDevCtrl = new CardLanStandardBus();
    }

    public boolean ismHasInitDev() {
        return mHasInitDev;
    }

    /**
     * Call the{CardLanDevCtrl#callInitDev()},
     * Initialize the device. If it has been initialized, it will not be initialized again
     */
    public String initDev() {
        if (!ismHasInitDev()) {
            /*
            * int if return value equal DeviceCardConfig.INIT_DEVICE_STATUS_SUCCESS, it means init devcie success, else not.
            * */
            mInitStatus = mCardLanDevCtrl.callInitDev();
            if (mInitStatus==DeviceCardConfig.INIT_DEVICE_STATUS_SUCCESS){
                Log.i(TAG, "CardReadWriteUtil.mInitStatus:"+mInitStatus +" ,init devcie success");
            }else{
                Log.i(TAG, "CardReadWriteUtil.mInitStatus:"+mInitStatus +" ,init devcie failed");
            }

            mHasInitDev = (mInitStatus == 0 || mInitStatus == -3 || mInitStatus == -4);
        }
        if (mInitStatus==DeviceCardConfig.INIT_DEVICE_STATUS_SUCCESS){
            return "mInitStatus:"+mInitStatus +" ,init devcie success";
        }else{
            return "mInitStatus:"+mInitStatus +" ,init devcie failed";
        }
    }

    /**
     * get the card sn
     * @return return the card sn byte array, if not search card, it return null
     * * int if return value equal DeviceCardConfig.CARD_RESET_STATUS_MONE_SUCCESS it means mifare1 card ,
     * * else equal DeviceCardConfig.CARD_RESET_STATUS_CPU_SUCCESS it means cpu card．
     */
    public byte[] getCardResetBytes() {
        initDev();
        byte[] resetByte = new byte[S_Reset_buffer_size];
        int cardResult = mCardLanDevCtrl.callCardReset(resetByte);
        if (cardResult== DeviceCardConfig.CARD_RESET_STATUS_MONE_SUCCESS){
            Log.i(TAG, "CardLanDevCtrl.callCardReset:" + cardResult+" ,是M1卡");
        }else if (cardResult== DeviceCardConfig.CARD_RESET_STATUS_CPU_SUCCESS){
            Log.i(TAG, "CardLanDevCtrl.callCardReset:" + cardResult+" ,是CPU卡");
        }else {
            Log.i(TAG, "CardLanDevCtrl.callCardReset:" + cardResult+" ,不是M1卡，也不是CPU卡，是未知卡类型");
        }

        if (cardResult == 1) {
            mHasGetResetBytes = false;
            return null;
        }
        mHasGetResetBytes = true;
        return resetByte;
    }

    private char hexIntStringToChar(String string) {
        if (!ByteUtil.notNull(string)) {
            string = "0";
        }
        return ByteUtil.intStringToChar(Integer.valueOf(string,16).toString());
//        return ByteUtil.intStringToChar(string);
    }

    private byte[] hexToByteArray(String hex) {
        if (!ByteUtil.notNull(hex)) {//参数hex为空则取12F
            hex = "FFFFFFFFFFFF";
        }
        return ByteUtil.hexStringToByteArray(hex);
    }

}
