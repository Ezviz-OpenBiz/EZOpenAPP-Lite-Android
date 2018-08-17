package com.ezviz.open.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.io.File;
import java.util.ArrayList;

import com.ezviz.open.R;
import com.ezviz.opensdk.base.data.CameraManager;
import com.ezviz.opensdk.base.data.DeviceManager;
import com.ezviz.opensdk.http.bean.CameraInfoEx;
import com.ezviz.opensdk.http.bean.DeviceInfoEx;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZDeviceInfo;

/**
 * Description: 数据管理
 * Created by dingwei3
 *
 * @date : 2016/12/12
 */
public class DataManager {
    private static DataManager mDateManager;
    private String ROOT_CACHE;
    private String ROOT_PATH;
    private LruBitmapPool mLruBitmapPool;

    private int[] mBackgroundResources = new int[]{R.drawable.bg_b1,R.drawable.bg_b2,R.drawable.bg_b3,R.drawable.bg_b4,R.drawable.bg_b5,R.drawable.bg_b6
            ,R.drawable.bg_b7,R.drawable.bg_b8,R.drawable.bg_b9,R.drawable.bg_b10};
    /**
     * 播放抓拍存放文件夹名称
     */
    private static String EZOPEN_FOLDER;
    /**
     * 播放抓拍存放文件夹名称
     */
    private static String CapturePicturePathName = "CaptureImg";

    /**
     * 录像文件存放文件夹
     */
    private static String RecordFliePathName = "Records";
    /**
     * 抓图存放文件夹名称
     */
    private static String CameraCoverCaptureImgFilePathName = "CameraCoverCaptureImg";
    /**
     * 抓图封面存放文件夹路径
     */
    private static String mCameraCoverCaptureImgFilePath;

    /**
     * 播放抓拍存放文件夹路径
     */
    private static String mCapturePicturePath;

    /**
     * 录像文件存放文件夹路径
     */
    private static String mRecordFliePath;

    public static void init(Context context) {
        mDateManager = new DataManager(context);
        CameraCaptureCache.init();
    }

    public static DataManager getInstance() {
        return mDateManager;
    }

    /**
     * @param context
     */
    public DataManager(Context context) {
        EZOPEN_FOLDER = context.getString(R.string.app_name);
        ROOT_PATH = Environment.getExternalStorageDirectory().toString() + "/" + EZOPEN_FOLDER + "/";
        ROOT_CACHE = getCacheDir(context) + "/";
        mCapturePicturePath = ROOT_PATH + CapturePicturePathName + "/";
        mRecordFliePath = ROOT_PATH+RecordFliePathName+"/";
        mCameraCoverCaptureImgFilePath = ROOT_CACHE + CameraCoverCaptureImgFilePathName + "/";
        File file = new File(mCameraCoverCaptureImgFilePath);
        if (file.isFile() || !file.exists()) {
            file.mkdirs();
        }

        file = new File(mCapturePicturePath);
        if (file.isFile() || !file.exists()) {
            file.mkdirs();
        }

        file = new File(mRecordFliePath);
        if (file.isFile() || !file.exists()) {
            file.mkdirs();
        }
    }

    public File getCacheDir(Context context) {
        File file = null;
        file = context.getExternalCacheDir();
        if (file == null) {
            file = context.getCacheDir();
        }
        return file;
    }

    /**
     * 获取播放抓拍存放文件夹路径
     *
     * @return
     */
    public String getCapturePicturePath() {
        return mCapturePicturePath;
    }

    /**
     * 获取录像文件夹路径
     *
     * @return
     */
    public String getRecodeFilePath() {
        return mRecordFliePath;
    }

    /**
     * 通道封面存放文件路径
     *
     * @param deviceserial
     * @param cameraNo
     * @return
     */
    public String getCameraCoverCaptureImgFilePath(String deviceserial, int cameraNo) {
        if (TextUtils.isEmpty(mCameraCoverCaptureImgFilePath)) {
            return null;
        }
        return mCameraCoverCaptureImgFilePath  + deviceserial + "_" + cameraNo;
    }

    /**
     * 获取realm实例
     *
     * @return
     */
    public Realm getRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("ezopen_app_realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);
        return realm;
    }


    /**
     * 设备列表item的背景数组
     * @return
     */
    public int[] getBackgroundResources() {
        return mBackgroundResources;
    }

    /**
     * 设备列表item的背景
     * @return
     */
    public int getBackgroundResource(int position) {
        return mBackgroundResources[position%mBackgroundResources.length];
    }

    /**
     * 获取设备封面图片png
     * @param deviceType
     * @return
     */
    public String getDeviceCover(String deviceType){
        if (TextUtils.isEmpty(deviceType)){
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer("");
        String[] strings = deviceType.split("-");
        if (strings.length >= 2){
            stringBuffer.append("https://statics.ys7.com/openweb/device/").append(strings[0]).append("-").append(strings[1]).append("/2.png");
        }
        return stringBuffer.toString();
    }


    public static String getCaptureFile(){
        java.util.Date date = new java.util.Date();
        String path = DataManager.getInstance().getCapturePicturePath() + String.format("%tY", date)
                + String.format("%tm", date) + String.format("%td", date) + "/"
                +String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) +".jpg";
        return path;
    }

    /**
     * 获取录像文件存放路径
     * @return
     */
    public static String getRecordFile(){
        java.util.Date date = new java.util.Date();
        String strRecordFile = DataManager.getInstance().getRecodeFilePath() + String.format("%tY", date)
                + String.format("%tm", date) + String.format("%td", date) + "/"
                + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) + ".mp4";
        return strRecordFile;
    }

    public DecodeDeviceInfo getDecodeDeviceInfo(String resultString){
        DecodeDeviceInfo decodeDeviceInfo = new DecodeDeviceInfo();

        String[] newlineCharacterSet = {
                "\n\r", "\r\n", "\r", "\n"};
        String stringOrigin = resultString;
        // 寻找第一次出现的位置
        int a = -1;
        int firstLength = 1;
        for (String string : newlineCharacterSet) {
            if (a == -1) {
                a = resultString.indexOf(string);
                if (a > stringOrigin.length() - 3) {
                    a = -1;
                }
                if (a != -1) {
                    firstLength = string.length();
                }
            }
        }

        // 扣去第一次出现回车的字符串后，剩余的是第二行以及以后的
        if (a != -1) {
            resultString = resultString.substring(a + firstLength);
        }
        // 寻找最后一次出现的位置
        int b = -1;
        for (String string : newlineCharacterSet) {
            b = resultString.indexOf(string);
            if (b != -1) {
                decodeDeviceInfo.deviceSerial = resultString.substring(0, b);
                firstLength = string.length();
                break;
            }
        }

        // 寻找遗失的验证码阶段
        if (decodeDeviceInfo.deviceSerial != null && b != -1 && (b + firstLength) <= resultString.length()) {
            resultString = resultString.substring(b + firstLength);
        }

        // 再次寻找回车键最后一次出现的位置
        int c = -1;
        for (String string : newlineCharacterSet) {
            c = resultString.indexOf(string);
            if (c != -1) {
                decodeDeviceInfo.deviceVerifyCode = resultString.substring(0, c);
                break;
            }
        }
        //解决xxxxxxx.fasdf\n655507861\nWIBLLX 这种情况
        if (TextUtils.isEmpty(decodeDeviceInfo.deviceVerifyCode) && resultString.length() == 6) {
            decodeDeviceInfo.deviceVerifyCode = resultString;
            resultString = "";
        }
        // 寻找CS-C2-21WPFR 判断是否支持wifi
        if (decodeDeviceInfo.deviceSerial != null && c != -1 && (c + firstLength) <= resultString.length()) {
            resultString = resultString.substring(c + firstLength);
        }
        if (resultString != null && resultString.length() > 0) {
            int d = -1;
            for (String string : newlineCharacterSet) {
                if (d == -1) {
                    d = resultString.indexOf(string);
                    if (d != -1) {
                        decodeDeviceInfo.deviceType = resultString.substring(0, d);
                    }
                }
            }
            if (d == -1) {
                decodeDeviceInfo.deviceType = resultString;
            }
        }

        if (b == -1) {
            decodeDeviceInfo.deviceSerial = resultString;
        }

        if (decodeDeviceInfo.deviceSerial == null) {
            decodeDeviceInfo.deviceSerial = stringOrigin;
        }

        // 去除规格字段：硬盘大小、尺寸大小等，由/、空格隔开
        if(!TextUtils.isEmpty(decodeDeviceInfo.deviceType)) {
            int index = decodeDeviceInfo.deviceType.indexOf("/");
            if(index > 0) {
                decodeDeviceInfo.deviceType = decodeDeviceInfo.deviceType.substring(0, decodeDeviceInfo.deviceType.indexOf("/"));
            }
            index = decodeDeviceInfo.deviceType.indexOf(" ");
            if(index > 0) {
                decodeDeviceInfo.deviceType = decodeDeviceInfo.deviceType.substring(0, decodeDeviceInfo.deviceType.indexOf(" "));
            }
        }
        return decodeDeviceInfo;
    }


    public class DecodeDeviceInfo{
        public String deviceSerial = "";
        public String deviceVerifyCode  = "";
        public String deviceType = "";
    }

    public LruBitmapPool getBitmapPool(Context context){
        if (mLruBitmapPool == null){
            MemorySizeCalculator calculator = new MemorySizeCalculator(context);
            int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
            mLruBitmapPool = new LruBitmapPool(defaultBitmapPoolSize);
        }
        return mLruBitmapPool;
    }
}


