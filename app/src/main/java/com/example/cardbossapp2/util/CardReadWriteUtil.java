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

    //输入keyA，如果不足12位则是新卡，使用12F作为keyA,转为字节数组传入so包
    private byte[] hexToByteArray(String hex) {
        if ((!ByteUtil.notNull(hex)) || hex.length()<12){//参数hex为空则取12F
            hex = "FFFFFFFFFFFF";
        }
        return ByteUtil.hexStringToByteArray(hex);
    }

    //转为char数值
    /*private char stringToChar(String string) {
        if (!ByteUtil.notNull(string)) {
            string = "0";
        }

        return ByteUtil.intStringToChar(string);
    }*/


    /**
     * Reads data from a sector or block
     * @param readSectorStr 扇区号 传入的是10进制的数
     * @param readIndexStr 块号
     * @param readkeyHexStr keyA
     * @param readKeyAreaHexStr 表示读的控制位，传入null默认取值0a
     * @return byte[]
     */
    public byte[] callReadJNI(String readSectorStr, String readIndexStr, String readkeyHexStr,
                              String readKeyAreaHexStr) {
        char readSector = CardReadWriteUtil2.stringToChar(readSectorStr);
        char readIndex = CardReadWriteUtil2.stringToChar(readIndexStr);
        byte[] readkeyHex = hexToByteArray(readkeyHexStr);
        if (!ByteUtil.notNull(readKeyAreaHexStr)) {
            readKeyAreaHexStr = "0a";
        }

        if (!readKeyAreaHexStr.equals("0a") && !readKeyAreaHexStr.equals("0b")) {
            readKeyAreaHexStr = "0a";
        }
        char readKeyArea = ByteUtil.hexStringToChar(readKeyAreaHexStr);
        return callReadJNI(readSector, readIndex, readkeyHex, readKeyArea);
    }

    /**
     *
     * @param readSector 扇区号 Sector index
     * @param readindex  块号 block index
     * @param readkey   keyA The check Key for the read operation
     * @param readKeyArea  0a
     * @return byte[]
     */
    public byte[] callReadJNI(char readSector, char readindex, byte[] readkey, char readKeyArea) {

        //Initialization machine
        initDev();
        char one = 1;

        if (!mHasGetResetBytes) {
            byte[] resetByte = new byte[S_Reset_buffer_size];
            int cardResult = mCardLanDevCtrl.callCardReset(resetByte);
        }

        byte[] readMsg = mCardLanDevCtrl.callReadOneSectorDataFromCard(readSector,
                readindex, one,
                readkey, readKeyArea);
        if (ByteUtil.notNull(readMsg)) {
            String realStr = ByteUtil.byteArrayToHexString(readMsg);
            Log.i(TAG, "callReadJNI: realStr = "+realStr);
            return readMsg;
        }

        Log.i(TAG, "callReadJNI: readMsg = null. realStr = null");
        return null;
    }


    /**
     * Writes data to the card
     * @param writeSectorStr 扇区号
     * @param writeindexStr 块号
     * @param writeHexStr  Write hexadecimal
     * @param hexWriteKey  default:"0xFFFFFFFFFFFF"
     * @param writeKeyAreaStr  default:"0b"
     * @return  Returns the status of the write operation was successful
     */
    public int callWriteJNI(String writeSectorStr, String writeindexStr, String writeHexStr, String
            hexWriteKey, String writeKeyAreaStr) {
        char writeSector = CardReadWriteUtil2.stringToChar(writeSectorStr);
        char writeindex = CardReadWriteUtil2.stringToChar(writeindexStr);
        byte[] writeKey = hexToByteArray(hexWriteKey);
        if (!ByteUtil.notNull(writeKeyAreaStr)) {
            writeKeyAreaStr = "0b";
        }
        if (!writeKeyAreaStr.equals("0a") && !writeKeyAreaStr.equals("0b")) {
            writeKeyAreaStr = "0b";
        }
        char writeKeyArea = ByteUtil.hexStringToChar(writeKeyAreaStr);

        return callWriteJNI(writeSector, writeindex, writeHexStr, writeKey, writeKeyArea);
    }

    /**
     *
     * @param writeSector
     * @param writeindex
     * @param writeHexStr
     * @param writeKey
     * @param readKeyArea
     * @return
     */
    public int callWriteJNI(char writeSector, char writeindex, String writeHexStr, byte[]
            writeKey, char readKeyArea) {

        initDev();
        //reset card
        if (!mHasGetResetBytes) {
            byte[] resetByte = new byte[S_Reset_buffer_size];
            int cardResult = mCardLanDevCtrl.callCardReset(resetByte);
        }

        byte[] writeBytes = hexToByteArray(writeHexStr);
        char one = 1;
        int writeResult = mCardLanDevCtrl.callWriteOneSertorDataToCard(writeBytes,
                writeSector,
                writeindex, one,
                writeKey, readKeyArea);
        return writeResult;
    }


}
