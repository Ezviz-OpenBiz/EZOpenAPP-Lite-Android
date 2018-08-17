package com.ezviz.open.utils;

import com.ezviz.open.model.EZOpenCameraInfo;

import io.realm.Realm;

/**
 * Description: 通道数据库操作类
 * Created by dingwei3
 *
 * @date : 2017/1/4
 */
public class EZCameraDBManager {
    /**
     *  同步查询通道
     * @param deviceSerial
     * @return
     */
    public static EZOpenCameraInfo findFirst(String deviceSerial,int cameraNo){
        EZOpenCameraInfo ezOpenCameraInfo = DataManager.getInstance().getRealm().where(EZOpenCameraInfo.class).equalTo("deviceSerial_cameraNo",deviceSerial+"_"+cameraNo).findFirst();
        if (ezOpenCameraInfo == null || !ezOpenCameraInfo.isValid()){
            return null;
        }
        return ezOpenCameraInfo;
    }

    /**
     * 设置通道预览清晰度
     * @param deviceSerial
     * @param cameraNo
     * @param level
     */
    public static void setDeviceVideoLevel(final String deviceSerial, final int cameraNo, final int level){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EZOpenCameraInfo ezOpenCameraInfo = realm.where(EZOpenCameraInfo.class).equalTo("deviceSerial_cameraNo",deviceSerial+"_"+cameraNo).findFirst();
                ezOpenCameraInfo.setVideoLevel(level);
            }
        });
    }

    /**
     * 设置设备监控点名称，即通道号名称
     * @param deviceSerial 设备序列号
     * @param cameraNo      设备通道号
     * @param name          通道号名称
     */
    public static void setCameraName(final String deviceSerial, final int cameraNo, final String name){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EZOpenCameraInfo ezOpenCameraInfo = realm.where(EZOpenCameraInfo.class).equalTo("deviceSerial_cameraNo",deviceSerial+"_"+cameraNo).findFirst();
                ezOpenCameraInfo.setCameraName(name);
            }
        });
    }
}


