package com.ezviz.open.utils;

import android.text.TextUtils;

import com.ezviz.open.model.DeviceEncrypt;
import com.ezviz.open.model.EZOpenCameraInfo;
import com.ezviz.open.model.EZOpenDetectorInfo;
import com.ezviz.open.model.EZOpenDeviceInfo;
import com.ezviz.open.model.EZOpenVideoQualityInfo;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDetectorInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZVideoQualityInfo;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Description: 设备数据库操作类
 * Created by dingwei3
 *
 * @date : 2017/1/4
 */
public class EZDeviceDBManager {
    /**
     * 存储设备相关信息到realm数据库
     *
     * @param list
     */
    public static void saveEZOpenInfo(final List<EZDeviceInfo> list) {
        if (list != null && list.size() > 0) {
            RealmAsyncTask transaction = DataManager.getInstance().getRealm().executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<EZOpenDeviceInfo> ezOpenDeviceInfoRealmResults = realm.where(EZOpenDeviceInfo.class).findAll();
                    ezOpenDeviceInfoRealmResults.deleteAllFromRealm();

                    RealmResults<EZOpenCameraInfo> ezOpenCameraInfoRealmResults = realm.where(EZOpenCameraInfo.class).findAll();
                    ezOpenCameraInfoRealmResults.deleteAllFromRealm();

                    RealmResults<EZOpenDetectorInfo> ezOpenDetectorInfoRealmResults = realm.where(EZOpenDetectorInfo.class).findAll();
                    ezOpenDetectorInfoRealmResults.deleteAllFromRealm();

                    for (int i = 0;i<list.size();i++){
                        EZDeviceInfo deviceInfo = list.get(i);
                        EZOpenDeviceInfo opendeviceInfo = realm.createObject(EZOpenDeviceInfo.class, deviceInfo.getDeviceSerial());
                        opendeviceInfo.copy(deviceInfo);
                        opendeviceInfo.setPosition(i);
                        if (deviceInfo.getCameraNum() > 0 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() > 0) {
                            for (EZCameraInfo cameraInfo : deviceInfo.getCameraInfoList()) {
                                EZOpenCameraInfo opencameraInfo = realm.createObject(EZOpenCameraInfo.class, cameraInfo.getDeviceSerial() + "_" + cameraInfo.getCameraNo());
                                opencameraInfo.copy(cameraInfo);
                                opencameraInfo.setStatus(opendeviceInfo.getStatus());
                                opencameraInfo.setDeviceType(opendeviceInfo.getDeviceType());
                                opencameraInfo.setDefence(opendeviceInfo.getDefence());
                                opencameraInfo.setSupportDefence(opendeviceInfo.isSupportDefence());
                                opencameraInfo.setCategory(opendeviceInfo.getCategory());

                                if (cameraInfo.getVideoQualityInfos() != null){
                                    RealmList<EZOpenVideoQualityInfo> list1 = new RealmList<EZOpenVideoQualityInfo>();
                                    for (EZVideoQualityInfo videoQualityInfo:cameraInfo.getVideoQualityInfos()){
                                        EZOpenVideoQualityInfo ezOpenVideoQualityInfo = realm.createObject(EZOpenVideoQualityInfo.class);
                                        ezOpenVideoQualityInfo.copy(videoQualityInfo);
                                        list1.add(ezOpenVideoQualityInfo);
                                    }
                                    opencameraInfo.setEZOpenVideoQualityInfos(list1);
                                }
                            }
                        }
                        if (deviceInfo.getDetectorNum() > 0 && deviceInfo.getDetectorInfoList() != null && deviceInfo.getDetectorInfoList().size() > 0) {
                            for (EZDetectorInfo detectorInfo : deviceInfo.getDetectorInfoList()) {
                                EZOpenDetectorInfo opendetectorInfo = realm.createObject(EZOpenDetectorInfo.class, deviceInfo.getDeviceSerial()+"_"+detectorInfo.getDetectorSerial());
                                opendetectorInfo.copy(detectorInfo);
                                opendetectorInfo.setDeviceSerial(deviceInfo.getDeviceSerial());
                            }
                        }
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }
            });
        }
    }




    /**
     * 从数据库中获取设备验证码
     * @param deviceSerial
     * @return
     */
    public static String getDevPwd(String deviceSerial){
        Realm realm = DataManager.getInstance().getRealm();
        DeviceEncrypt deviceEncrypt = null;
        String password = "";
        deviceEncrypt = realm.where(DeviceEncrypt.class).equalTo("deviceSerial",deviceSerial).findFirst();
        if (deviceEncrypt != null && deviceEncrypt.isValid()){
            password = deviceEncrypt.getEncryptPwd();
        }
        realm.close();
        return password;
    }

    /**
     * 保存设备验证码到数据库
     * @param deviceSerial
     * @param password
     * @return
     */
    public static void saveDevPwd(final String deviceSerial, final String password){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DeviceEncrypt deviceEncrypt = realm.where(DeviceEncrypt.class).equalTo("deviceSerial",deviceSerial).findFirst();
                if (deviceEncrypt != null && deviceEncrypt.isValid()){
                    deviceEncrypt.deleteFromRealm();
                }
                DeviceEncrypt newDeviceEncrypt = realm.createObject(DeviceEncrypt.class,deviceSerial);
                String dest = "";
                if (!TextUtils.isEmpty(password)) {
                    dest = password;
                }
                if (!TextUtils.isEmpty(dest)) {
                    newDeviceEncrypt.setEncryptPwd(dest);
                }
            }
        });
    }

    /**
     * 设置设备是否开启活动检测
     * @param deviceSerial
     * @param defence
     */
    public static void setDeviceDefence(final String deviceSerial, final int defence){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EZOpenDeviceInfo ezOpenDeviceInfo = realm.where(EZOpenDeviceInfo.class).equalTo("deviceSerial",deviceSerial).findFirst();
                if (ezOpenDeviceInfo != null && ezOpenDeviceInfo.isValid()){
                    ezOpenDeviceInfo.setDefence(defence);
                }
                RealmResults<EZOpenCameraInfo> ezOpenCameraInfos = realm.where(EZOpenCameraInfo.class).equalTo("deviceSerial",deviceSerial).findAll();
                if (ezOpenCameraInfos != null && ezOpenCameraInfos.isValid()){
                    for (EZOpenCameraInfo ezOpenCameraInfo:ezOpenCameraInfos){
                        ezOpenCameraInfo.setDefence(ezOpenDeviceInfo.getDefence());
                    }
                }
            }
        });
    }

    /**
     * 设置设备是否开启活动检测
     * @param deviceSerial
     * @param encrypt
     */
    public static void setDeviceEncrypt(final String deviceSerial, final int encrypt){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EZOpenDeviceInfo ezOpenDeviceInfo = realm.where(EZOpenDeviceInfo.class).equalTo("deviceSerial",deviceSerial).findFirst();
                if (ezOpenDeviceInfo != null && ezOpenDeviceInfo.isValid()){
                    ezOpenDeviceInfo.setIsEncrypt(encrypt);
                }
            }
        });
    }

    /**
     * 设置设备是否在线
     * @param deviceSerial
     * @param isOnline  是否在线
     */
    public static void setDeviceStatua(final String deviceSerial, final boolean isOnline){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EZOpenDeviceInfo ezOpenDeviceInfo = realm.where(EZOpenDeviceInfo.class).equalTo("deviceSerial",deviceSerial).findFirst();
                if (ezOpenDeviceInfo != null && ezOpenDeviceInfo.isValid()){
                    ezOpenDeviceInfo.setStatus(isOnline?1:2);
                }
                RealmResults<EZOpenCameraInfo> ezOpenCameraInfos = realm.where(EZOpenCameraInfo.class).equalTo("deviceSerial",deviceSerial).findAll();
                if (ezOpenCameraInfos != null && ezOpenCameraInfos.isValid()){
                    for (EZOpenCameraInfo ezOpenCameraInfo:ezOpenCameraInfos){
                        ezOpenCameraInfo.setStatus(ezOpenDeviceInfo.getStatus());
                    }
                }
            }
        });
    }

    /**
     *  同步查询设备信息
     * @param deviceSerial
     * @return
     */
    public static EZOpenDeviceInfo findFirst(String deviceSerial){
        EZOpenDeviceInfo ezOpenDeviceInfo = DataManager.getInstance().getRealm().where(EZOpenDeviceInfo.class).equalTo("deviceSerial",deviceSerial).findFirst();
        if (ezOpenDeviceInfo == null || !ezOpenDeviceInfo.isValid()){
            return null;
        }
        ezOpenDeviceInfo.removeAllChangeListeners();
        return ezOpenDeviceInfo;
    }

    /**
     * 保存设备名称到数据库
     * @param deviceSerial
     * @param name  设备名称
     */
    public static void setDeviceName(final String deviceSerial, final String name){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                EZOpenDeviceInfo ezOpenDeviceInfo = realm.where(EZOpenDeviceInfo.class).equalTo("deviceSerial",deviceSerial).findFirst();
                if (ezOpenDeviceInfo != null && ezOpenDeviceInfo.isValid()){
                    ezOpenDeviceInfo.setDeviceName(name);
                }
                RealmResults<EZOpenCameraInfo> ezOpenCameraInfos = realm.where(EZOpenCameraInfo.class).equalTo("deviceSerial",deviceSerial).findAll();
                if (ezOpenCameraInfos != null && ezOpenCameraInfos.isValid()){
                    for (EZOpenCameraInfo ezOpenCameraInfo:ezOpenCameraInfos){
                        ezOpenCameraInfo.setCameraName(ezOpenDeviceInfo.getDeviceName());
                    }
                }
            }
        });
    }

    /**
     * 从数据库删除设备相关信息
     * @param deviceSerial
     */
    public static void deleteDevice(final String deviceSerial){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //删除设备
                EZOpenDeviceInfo ezOpenDeviceInfo = realm.where(EZOpenDeviceInfo.class).equalTo("deviceSerial",deviceSerial).findFirst();
                if (ezOpenDeviceInfo != null && ezOpenDeviceInfo.isValid()){
                    ezOpenDeviceInfo.deleteFromRealm();
                }
                //删除设备下所有通道
                RealmResults ezOpenCameraInfos =  realm.where(EZOpenCameraInfo.class).equalTo("deviceSerial",deviceSerial).findAll();
                if (ezOpenCameraInfos != null && ezOpenCameraInfos.isValid()){
                    ezOpenCameraInfos.deleteAllFromRealm();
                }

                //删除设备下探测器
                RealmResults ezOpenDetectorInfos = realm.where(EZOpenDetectorInfo.class).equalTo("deviceSerial",deviceSerial).findAll();
                if (ezOpenDetectorInfos != null && ezOpenDetectorInfos.isValid()){
                    ezOpenDetectorInfos.deleteAllFromRealm();
                }
            }
        });
    }

    /**
     * 从数据库删除所有设备信息
     */
    public static void deleteAllDevice(){
        Realm realm = DataManager.getInstance().getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //删除设备
                RealmResults ezOpenDeviceInfos = realm.where(EZOpenDeviceInfo.class).findAll();
                if (ezOpenDeviceInfos != null && ezOpenDeviceInfos.isValid()){
                    ezOpenDeviceInfos.deleteAllFromRealm();
                }
                //删除设备下所有通道
                RealmResults ezOpenCameraInfos = realm.where(EZOpenCameraInfo.class).findAll();
                if (ezOpenCameraInfos != null && ezOpenCameraInfos.isValid()){
                    ezOpenCameraInfos.deleteAllFromRealm();
                }

                //删除设备下探测器
                RealmResults ezOpenDetectorInfos = realm.where(EZOpenDetectorInfo.class).findAll();
                if (ezOpenDetectorInfos != null && ezOpenDetectorInfos.isValid()){
                    ezOpenDetectorInfos.deleteAllFromRealm();
                }
            }
        });
    }
}


