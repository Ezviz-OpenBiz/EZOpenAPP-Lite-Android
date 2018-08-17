package com.ezviz.open.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Description:设备对应设备验证码关系类
 * Created by dingwei3
 *
 * @date : 2016/12/20
 */
public class DeviceEncrypt extends RealmObject{

    /**
     * 设备序列号
     */
    @PrimaryKey
    private String deviceSerial;

    /**
     * 设备验证码，
     */
    private String  encryptPwd;

    public String getEncryptPwd() {
        return encryptPwd;
    }

    public void setEncryptPwd(String encryptPwd) {
        this.encryptPwd = encryptPwd;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

}


