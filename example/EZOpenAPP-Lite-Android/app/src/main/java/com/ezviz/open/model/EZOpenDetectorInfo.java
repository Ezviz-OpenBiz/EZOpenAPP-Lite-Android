package com.ezviz.open.model;

import com.videogo.openapi.bean.EZDetectorInfo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Description:探测器信息类
 * Created by dingwei3
 *
 * @date : 2016/12/6
 */
public class EZOpenDetectorInfo extends RealmObject {

    /**
     * 探测器PrimaryKey
     */
    @PrimaryKey
    private String deviceSerial_detectorSerial;


    /**
     * camera对应的设备数字序列号
     */
    private String deviceSerial = null;
    /**
     * 探测器序列号
     */
    private String detectorSerial;

    /**
     * 探测器类型
     */
    private String detectorType;

    /**
     * 探测器与报警主机是否连通
     * 0-非联通，1-联通
     */
    private int detectorState = 1;

    /**
     * 探测器名称
     */
    private String detectorTypeName;

    /**
     * 防区故障状态，0恢复，1产生
     */
    private int faultZoneStatus;

    /**
     * 电池欠压状态，0恢复，1产生
     */
    private int underVoltageStatus;

    /**
     * 无线干扰状态，0恢复，1产生
     */
    private int wirelessInterferenceStatus;

    /**
     * 设备离线状态，0恢复，1产生
     */
    private int offlineStatus;

    /**
     * 在家模式下，探测器是否布防 0--未布防，1--布防
     */
    private int atHomeEnable;

    /**
     * 外出模式下，探测器是否布防，0--未布防，1--布防
     */
    private int outerEnable;

    /**
     * 睡眠模式下，探测器是否布防，0--未布防，1--布防
     */
    private int sleepEnable;

    public String getDetectorSerial() {
        return detectorSerial;
    }

    public void setDetectorSerial(String detectorSerial) {
        this.detectorSerial = detectorSerial;
    }

    public String getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(String detectorType) {
        this.detectorType = detectorType;
    }

    public int getDetectorState() {
        return detectorState;
    }

    public void setDetectorState(int detectorState) {
        this.detectorState = detectorState;
    }

    public String getDetectorTypeName() {
        return detectorTypeName;
    }

    public void setDetectorTypeName(String detectorTypeName) {
        this.detectorTypeName = detectorTypeName;
    }

    public int getFaultZoneStatus() {
        return faultZoneStatus;
    }

    public void setFaultZoneStatus(int faultZoneStatus) {
        this.faultZoneStatus = faultZoneStatus;
    }

    public int getUnderVoltageStatus() {
        return underVoltageStatus;
    }

    public void setUnderVoltageStatus(int underVoltageStatus) {
        this.underVoltageStatus = underVoltageStatus;
    }

    public int getWirelessInterferenceStatus() {
        return wirelessInterferenceStatus;
    }

    public void setWirelessInterferenceStatus(int wirelessInterferenceStatus) {
        this.wirelessInterferenceStatus = wirelessInterferenceStatus;
    }

    public int getOfflineStatus() {
        return offlineStatus;
    }

    public void setOfflineStatus(int offlineStatus) {
        this.offlineStatus = offlineStatus;
    }

    public int getAtHomeEnable() {
        return atHomeEnable;
    }

    public void setAtHomeEnable(int atHomeEnable) {
        this.atHomeEnable = atHomeEnable;
    }

    public int getOuterEnable() {
        return outerEnable;
    }

    public void setOuterEnable(int outerEnable) {
        this.outerEnable = outerEnable;
    }

    public int getSleepEnable() {
        return sleepEnable;
    }

    public void setSleepEnable(int sleepEnable) {
        this.sleepEnable = sleepEnable;
    }

    public String getDeviceSerial_detectorSerial() {
        return deviceSerial_detectorSerial;
    }

    public void setDeviceSerial_detectorSerial(String deviceSerial_detectorSerial) {
        this.deviceSerial_detectorSerial = deviceSerial_detectorSerial;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public void copy(EZDetectorInfo detectorInfo) {
        setAtHomeEnable(detectorInfo.getAtHomeEnable());
        setDetectorState(detectorInfo.getDetectorState());
        setDetectorType(detectorInfo.getDetectorType());
        setDetectorTypeName(detectorInfo.getDetectorTypeName());
        setFaultZoneStatus(detectorInfo.getFaultZoneStatus());
        setOfflineStatus(detectorInfo.getOfflineStatus());
        setOuterEnable(detectorInfo.getOuterEnable());
        setSleepEnable(detectorInfo.getSleepEnable());
        setUnderVoltageStatus(detectorInfo.getUnderVoltageStatus());
        setWirelessInterferenceStatus(detectorInfo.getWirelessInterferenceStatus());
    }
}


