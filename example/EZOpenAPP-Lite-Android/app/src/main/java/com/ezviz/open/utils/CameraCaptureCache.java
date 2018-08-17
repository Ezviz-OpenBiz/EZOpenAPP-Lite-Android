package com.ezviz.open.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.HashMap;

/**
 * Description: 拍照缓存管理
 * Created by dingwei3
 *
 * @date : 2016/12/29
 */
public class CameraCaptureCache {
    private  static CameraCaptureCache cameraCaptureCache;
    private LruCache<String, Bitmap> mCoverCache;

    public HashMap<String ,Boolean> mCaptureRefeshMap = new HashMap<String,Boolean>();

    public HashMap<String ,Boolean> mCaptureInitMap = new HashMap<String,Boolean>();

    public static void init(){
        cameraCaptureCache = new CameraCaptureCache();
    }

    public static CameraCaptureCache getInstance() {
        return cameraCaptureCache;
    }

    public CameraCaptureCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/16作为缓存的大小。
        int cacheSize = maxMemory / 16;
        mCoverCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public String getKey(String deviceSerial,int cameraNo){
        if (deviceSerial != null){
            return deviceSerial+"_"+cameraNo;
        }
        return null;
    }

    /***
     * 缓存抓图图像
     */
    public void cacheCoverBitmap(String deviceSerial,int cameraNo, Bitmap cover) {
        if (deviceSerial != null && cover != null) {
            mCoverCache.put(DataManager.getInstance().getCameraCoverCaptureImgFilePath(deviceSerial,cameraNo),cover);
        }
    }

    /***
     * 缓存抓图图像
     */
    public void cacheCoverBitmap(String path, Bitmap cover) {
        if (!TextUtils.isEmpty(path) && cover != null) {
            mCoverCache.put(path, cover);
        }
    }

    /***
     * 获取缓存图像
     */
    public Bitmap getCoverCache(String deviceSerial,int cameraNo) {
        if (deviceSerial != null) {
           Bitmap bitmap =  mCoverCache.get(DataManager.getInstance().getCameraCoverCaptureImgFilePath(deviceSerial,cameraNo));
            return bitmap;
        }
        return null;
    }

    /***
     * 获取缓存图像
     */
    public boolean isCoverCache(String deviceSerial, int cameraNo, ImageView imageView) {
        if (deviceSerial != null) {
            Bitmap bitmap =  mCoverCache.get(DataManager.getInstance().getCameraCoverCaptureImgFilePath(deviceSerial,cameraNo));
           if (bitmap != null){
               imageView.setImageBitmap(bitmap);
               return true;
           }
        }
        return false;
    }
    /***
     * 获取缓存图像
     */
    public Bitmap getCoverCacheAsyn(String deviceSerial,int cameraNo) {
        if (deviceSerial != null) {
            Bitmap bitmap =  mCoverCache.get(DataManager.getInstance().getCameraCoverCaptureImgFilePath(deviceSerial,cameraNo));
            return bitmap;
        }
        return null;
    }

    /***
     * 获取缓存图像
     */
    public Bitmap getCoverCache(String path) {
        if (path != null) {
            Bitmap bitmap = mCoverCache.get(path);
            return bitmap;
        }
        return null;
    }

    /***
     * 清除缓存
     */
    public void releaseCache() {
        mCoverCache.evictAll();
    }

    /**
     * @param deviceSerial
     * @param cameraNo
     * @param isInit    是否为初始化列表加载封面
     */
    public void addCoverRefresh(String deviceSerial,int cameraNo,boolean isInit){
        mCaptureRefeshMap.put(getKey(deviceSerial,cameraNo),isInit);
    }
    public void deleteCoverRefresh(String deviceSerial,int cameraNo){
        mCaptureRefeshMap.remove(getKey(deviceSerial,cameraNo));
    }
}


