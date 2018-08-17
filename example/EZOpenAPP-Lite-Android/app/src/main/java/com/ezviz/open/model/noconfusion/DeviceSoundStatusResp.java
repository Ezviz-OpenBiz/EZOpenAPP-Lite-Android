package com.ezviz.open.model.noconfusion;

/**
 * Description: 设备语音开关状态类
 * Created by dingwei3
 *
 * @date : 2017/1/12
 */
public class DeviceSoundStatusResp extends BaseResponse {

    private DeviceSoundStatus data;

    public DeviceSoundStatus getData() {
        return data;
    }

    public class DeviceSoundStatus{
        /**
         * 	设备序列号
         */
        private String deviceSerial;
        /**
         * 通道号
         */
        private int channelNo;
        /**
         * 状态：0-关闭，1-开启
         */
        private int enable = -1;

        public String getDeviceSerial() {
            return deviceSerial;
        }

        public void setDeviceSerial(String deviceSerial) {
            this.deviceSerial = deviceSerial;
        }

        public int getChannelNo() {
            return channelNo;
        }

        public void setChannelNo(int channelNo) {
            this.channelNo = channelNo;
        }

        public int getEnable() {
            return enable;
        }

        public void setEnable(int enable) {
            this.enable = enable;
        }
    }
}


