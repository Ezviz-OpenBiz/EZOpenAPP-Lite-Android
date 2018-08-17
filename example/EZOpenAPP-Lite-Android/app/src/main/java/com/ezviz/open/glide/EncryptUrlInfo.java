package com.ezviz.open.glide;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/22
 */
public class EncryptUrlInfo {
    public String deviceSerial;
    public String url;
    public String password;
    public boolean isEncrypt;
    public int position;

    public EncryptUrlInfo(String deviceSerial, String url, String password, boolean isEncrypt) {
        this.deviceSerial = deviceSerial;
        this.url = url;
        this.password = password;
        this.isEncrypt = isEncrypt;
    }

    public EncryptUrlInfo(String deviceSerial, String url, boolean isEncrypt) {
        this.deviceSerial = deviceSerial;
        this.url = url;
        this.isEncrypt = isEncrypt;
    }

    public EncryptUrlInfo(){

    }

    public void setPosition(int position) {
        this.position = position;
    }
}


