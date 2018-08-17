package com.ezviz.open.model;

import com.ezviz.open.utils.DataManager;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.bean.EZDeviceInfo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Description: 设备信息类
 * Created by dingwei3
 *
 */
public class EZOpenDeviceInfo extends RealmObject{

    /**
     * 设备9位序列号，可以作为EZOpenDeviceInfo对象的唯一标示符
     */
    @PrimaryKey
    private String deviceSerial = null;
    /**
     * 设备下的camera数量，如IPC设备，该值为1，4通道DVR设备，该值为4，等
     * 无camera的设备该值为0
     */
    private int cameraNum;
    /**
     * 设备布防状态
     * A1设备：0-睡眠 8-在家 16-外出
     * 非A1设备如IPC：0-撤防 1-布防
     */
    private int defence = 0;
    /**
     * 设备下探测器数量
     * 为0表示该设备不支持探测器或没有绑定探测器
     */
    private int detectorNum;
    /**
     * 设备封面
     */
    private String deviceCover = null;
    /**
     * 设备名称，当设备为IPC设备时，和对应EZCameraInfo中的CameraName值一致
     */
    private String deviceName = null;

    /**
     * 设备的型号，可用来判断设备为IPC、多通道设备DVR、报警设备还是存储设备等
     */
    private String deviceType = null;
    /**
     * 设备固件版本号
     */
    private String deviceVersion = null;
    /**
     * 设备是否加密 是否加密，0：不加密，1：加密
     */
    private int isEncrypt;
    /**
     * 在线状态,1-在线，2-不在线
     */
    private int status = 1;

    /**
     *  是否支持布撤防
     */
   private boolean isSupportDefence;
    /**
     *  否支持布防计划
     */
    private boolean isSupportDefencePlan;
    /**
     *  是否支持中心镜像
     */
    private boolean isSupportMirrorCenter;
    /**
     *  是否支持云台控制
     */
    private boolean isSupportPTZ;

    /**
     *  是否支持升级
     */
    private boolean isSupportUpgrade;

    /**
     *  是否支持光学缩放(镜头拉近放远)
     */
    private boolean isSupportZoom;

    /**
     *  是否支持语音提示开关
     */
    private boolean  isSupportAudioOnOff;


    /**
     * 支持对讲标志
     * 1--支持全双工对讲
     * 3--支持半双工对讲
     * 0--不支持对讲
     */
    private int supportTalkValue;

    /**
     * 设备大类
     */
    private String category = null;

    /**
     * 设备被用户添加时间，精确到毫秒
     */
    private long addTime;

    /**
     * 设备列表中的排序
     */
    private int position;

    public int getSupportTalkValue() {
        return supportTalkValue;
    }

    public String getDisplayDeviceType() {
        return getDeviceType()+"("+getDeviceSerial()+")";
    }

    public void setSupportTalkValue(int supportTalkValue) {
        this.supportTalkValue = supportTalkValue;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public int getCameraNum() {
        return cameraNum;
    }

    public void setCameraNum(int cameraNum) {
        this.cameraNum = cameraNum;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getDetectorNum() {
        return detectorNum;
    }

    public void setDetectorNum(int detectorNum) {
        this.detectorNum = detectorNum;
    }

    public String getDeviceCover() {
        return deviceCover;
    }

    public void setDeviceCover(String deviceCover) {
        this.deviceCover = deviceCover;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public int getIsEncrypt() {
        return isEncrypt;
    }

    public void setIsEncrypt(int isEncrypt) {
        this.isEncrypt = isEncrypt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSupportDefence() {
        return isSupportDefence;
    }

    public void setSupportDefence(boolean supportDefence) {
        isSupportDefence = supportDefence;
    }

    public boolean isSupportDefencePlan() {
        return isSupportDefencePlan;
    }

    public void setSupportDefencePlan(boolean supportDefencePlan) {
        isSupportDefencePlan = supportDefencePlan;
    }

    public boolean isSupportMirrorCenter() {
        return isSupportMirrorCenter;
    }

    public void setSupportMirrorCenter(boolean supportMirrorCenter) {
        isSupportMirrorCenter = supportMirrorCenter;
    }

    public boolean isSupportPTZ() {
        return isSupportPTZ;
    }

    public void setSupportPTZ(boolean supportPTZ) {
        isSupportPTZ = supportPTZ;
    }

    public boolean isSupportUpgrade() {
        return isSupportUpgrade;
    }

    public void setSupportUpgrade(boolean supportUpgrade) {
        isSupportUpgrade = supportUpgrade;
    }

    public boolean isSupportZoom() {
        return isSupportZoom;
    }

    public void setSupportZoom(boolean supportZoom) {
        isSupportZoom = supportZoom;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public boolean isSupportAudioOnOff() {
        return isSupportAudioOnOff;
    }

    public void setSupportAudioOnOff(boolean supportAudioOnOff) {
        isSupportAudioOnOff = supportAudioOnOff;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public EZOpenDeviceInfo() {

    }

    /**
     * 获取支持对讲模式类型
     *
     * @return 对讲模式类型  0    不支持对讲
     *                        1    支持全双工对讲
     *                        3    支持半双工对讲
     */
    public  int supportTalkMode() {
        switch (supportTalkValue) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 3:
                return 3;
            default:
                break;
        }
        return 0;
    }


    public void copy(EZDeviceInfo deviceInfo){
        setIsEncrypt(deviceInfo.getIsEncrypt());
        setDefence(deviceInfo.getDefence());
        setDeviceCover(deviceInfo.getDeviceCover());
        setDeviceCover(DataManager.getInstance().getDeviceCover(deviceInfo.getDeviceType()));
        setDeviceName(deviceInfo.getDeviceName());
        setDeviceType(deviceInfo.getDeviceType());
        setDeviceVersion(deviceInfo.getDeviceVersion());
        setStatus(deviceInfo.getStatus());
        setSupportDefence(deviceInfo.isSupportDefence());
        setSupportDefencePlan(deviceInfo.isSupportDefencePlan());
        setSupportMirrorCenter(deviceInfo.isSupportMirrorCenter());
        setSupportPTZ(deviceInfo.isSupportPTZ());
        setSupportTalkValue(deviceInfo.supportTalkMode());
        setSupportUpgrade(deviceInfo.isSupportUpgrade());
        setSupportZoom(deviceInfo.isSupportZoom());
        setCameraNum(deviceInfo.getCameraNum());
        setDetectorNum(deviceInfo.getDetectorNum());
        setCategory(deviceInfo.getCategory());
        setAddTime(deviceInfo.getAddTime());
        setSupportAudioOnOff(deviceInfo.isSupportAudioOnOff());
    }
}


