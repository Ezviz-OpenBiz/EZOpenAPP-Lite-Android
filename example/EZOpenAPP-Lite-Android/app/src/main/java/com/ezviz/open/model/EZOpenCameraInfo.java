package com.ezviz.open.model;

import com.ezviz.open.utils.CameraCaptureCache;
import com.videogo.openapi.bean.EZCameraInfo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Description:通道信息类
 * Created by dingwei3
 *
 * @date : 2016/12/5
 */
public class EZOpenCameraInfo extends RealmObject{

    /**
     * 设备序列号加camerNo组成主键存储
     */
    @PrimaryKey
    private String deviceSerial_cameraNo;
    /**
     * camera对应的设备数字序列号
     */
    private String deviceSerial = null;

    /**
     * camera在对应设备上的通道号，若为IPC设备，该字段始终为1
     */
    private int cameraNo = 0;

    /**
     * camera名称，若为IPC设备，和EZDeviceInfo中deviceName会保持一致
     */
    private String cameraName = null;

    /**
     * 分享状态
     * 1-分享所有者，0-未分享，2-分享接受者（表示此摄像头是别人分享给我的）
     */
    private int isShared = 0;

    /**
     * 封面url
     */
    private String cameraCover = null;


    /**
     * 清晰度
     * 0-流畅，1-均衡，2-高清，3-超清
     */
    private int videoLevel = 0;

    /**
     * 在线状态,1-在线，2-不在线
     */
    private int status = 1;

    /**
     * 设备布防状态
     * A1设备：0-睡眠 8-在家 16-外出
     * 非A1设备如IPC：0-撤防 1-布防
     */
    private int defence = 0;

    /**
     * 设备型号
     */
    private String  deviceType;

    /**
     * 设备大类
     */
    private String category = null;

    /**
     *  是否支持布撤防
     */
    private boolean isSupportDefence;
    /**
     *  支持的清晰度信息
     */
    private RealmList<EZOpenVideoQualityInfo> mEZOpenVideoQualityInfos;

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public int getCameraNo() {
        return cameraNo;
    }

    public void setCameraNo(int cameraNo) {
        this.cameraNo = cameraNo;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public int getIsShared() {
        return isShared;
    }

    public void setIsShared(int isShared) {
        this.isShared = isShared;
    }

    public String getCameraCover() {
        return cameraCover;
    }

    public void setCameraCover(String cameraCover) {
        this.cameraCover = cameraCover;
    }

    public int getVideoLevel() {
        return videoLevel;
    }

    public void setVideoLevel(int videoLevel) {
        this.videoLevel = videoLevel;
    }

    public String getDeviceSerial_cameraNo() {
        return deviceSerial_cameraNo;
    }

    public void setDeviceSerial_cameraNo(String deviceSerial_cameraNo) {
        this.deviceSerial_cameraNo = deviceSerial_cameraNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public boolean isSupportDefence() {
        return isSupportDefence;
    }

    public void setSupportDefence(boolean supportDefence) {
        isSupportDefence = supportDefence;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public RealmList<EZOpenVideoQualityInfo> getEZOpenVideoQualityInfos() {
        return mEZOpenVideoQualityInfos;
    }

    public void setEZOpenVideoQualityInfos(RealmList<EZOpenVideoQualityInfo> EZOpenVideoQualityInfos) {
        mEZOpenVideoQualityInfos = EZOpenVideoQualityInfos;
    }

    public void copy(EZCameraInfo cameraInfo){
        setCameraCover(cameraInfo.getCameraCover());
        setCameraName(cameraInfo.getCameraName());
        setCameraNo(cameraInfo.getCameraNo());
        setDeviceSerial(cameraInfo.getDeviceSerial());
        setIsShared(cameraInfo.getIsShared());
        setVideoLevel(cameraInfo.getCurrentVideoLevel().getVideoLevel());
        CameraCaptureCache.getInstance().addCoverRefresh(cameraInfo.getDeviceSerial(),cameraInfo.getCameraNo(),true);
    }
}


