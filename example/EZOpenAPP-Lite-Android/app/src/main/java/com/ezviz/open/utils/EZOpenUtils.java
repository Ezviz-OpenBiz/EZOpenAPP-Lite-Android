package com.ezviz.open.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.videogo.openapi.EZOpenSDK;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static okhttp3.internal.Util.closeQuietly;

/**
 * Description: 工具类
 * Created by dingwei3
 *
 * @date : 2017/1/4
 */
public class EZOpenUtils {
    private static final String TAG = "EZOpenUtils";
    /** 密码最大长度 */
    public static final int PSW_MAX_LENGTH = 16;
    /** 报警信息 */
    public static final String EXTRA_ALARM_INFO = "EXTRA_ALARM_INFO";
    /** 抓拍SD卡的最小空间要求(10M) */
    public static final long PIC_MIN_MEM_SPACE = 10485760;

    /** 录像SD卡的最小空间要求(20M) */
    public static final long REC_MIN_MEM_SPACE = 20971520;
    /** KB单位 */
    public static final long KB = 1024;
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断SDCard是否可用
     *
     * @return true - 可用 false - 不可用
     */
    public static boolean isSDCardUseable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean saveBitmapToFile(Bitmap bitmap, String filePath, Bitmap.CompressFormat format) {
        if (bitmap == null || filePath == null) {
            return false;
        }
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (parentFile == null || !parentFile.exists() || parentFile.isFile()) {
            parentFile.mkdirs();
        }
        if (file.exists()) {
            if (!file.delete()) {
                EZLog.e(TAG, "delete fail");
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(format, 100, fileOutputStream);
            fileOutputStream.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeQuietly(fileOutputStream);
        }
        return false;
    }

    /**
     * 转化为GB或者MB或者KB
     *
     * @param value
     * @return
     */
    public static String transformToSize(long value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String size = value / (1024 * 1024 * 1024) < 1 ? (value / (1024 * 1024) < 1 ? (String.valueOf(decimalFormat
                .format(value / (1024.0))) + "KB") : String
                .valueOf(decimalFormat.format(value / (1024 * 1024.0))) + "MB")
                : (String.valueOf(decimalFormat.format(value / (1024 * 1024 * 1024.0))) + "GB");
        return size;
    }

    public static void soundPool(Context context,int rawId){
        SoundPool soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        soundPool.load(context, rawId,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                soundPool.play(1,1, 1, 0, 0, 1);
            }
        });
    }

    public static void gotoLogin(){
        EZOpenSDK.clearStreamInfoCache();
        EZOpenSDK.openLoginPage(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Function:得到app版本
     *
     * @return
     * @author dingwei3
     */
    public static String getAppVersionNameInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此判断方式只能判断获取报警信息列表获取到的报警图片url
     * @param url
     * @return  图片url解析得到图片是否加密
     */
    public static boolean isEncrypt(String url){
        int ret = 0;
        try {
            Uri uri = Uri.parse(url);
            ret = Integer.parseInt(uri.getQueryParameter("isEncrypted"));
        }catch (Exception e){
//          e.printStackTrace();
        }
        return ret == 1;
    }

    /**
     * 获取SDCard 路径
     *
     * @return SDCard 路径
     * @since V1.0
     */
    public static File getSDCardPath() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取SDCard剩下的大小
     *
     * @return SDCard剩下的大小
     * @since V1.0
     */
    public static long getSDCardRemainSize() {
        StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = statfs.getBlockSize();
        long availableBlocks = statfs.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Calendar parseTimeToCalendar(String strTime) {
        if (strTime == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(date);
        return timeCalendar;
    }
}


