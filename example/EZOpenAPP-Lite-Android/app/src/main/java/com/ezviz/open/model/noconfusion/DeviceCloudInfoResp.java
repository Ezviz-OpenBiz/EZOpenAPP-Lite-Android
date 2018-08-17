package com.ezviz.open.model.noconfusion;

/**
 * Description:设备云存储状态查询返回值
 * Created by dingwei3
 *
 * @date : 2017/1/12
 */
public class DeviceCloudInfoResp extends BaseResponse {

    private DeviceCloundInfo data;

    public DeviceCloundInfo getDeviceCloundInfo() {
        return data;
    }

    public class DeviceCloundInfo{
        /**
         * 云存储服务所属用户的用户名
         */
        private String userName;
        /**
         * 设备序列号
         */
        private String deviceSerial;
        /**
         * 通道号
         */
        private int channelNo;
        /**
         * 云存储服务录像覆盖周期
         */
        private int totalDays;
        /**
         * 云存储服务状态：-2:设备不支持,-1:未开通云存储,0:未激活,1:激活,2:过期
         */
        private int status;
        /**
         * 云存储服务开始时间，精确到秒
         */
        private int startTime;
        /**
         * 云存储服务结束时间，精确到秒
         */
        private int expireTime;

        /**
         * 云存储服务剩余天数
         */
        private int validDays;

        /**
         * 不同类型云存储服务信息，只有当设备存在两种类型云存储服务才会有此对象
         */
        private DeviceCloundInfo serviceDetail;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

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

        public int getTotalDays() {
            return totalDays;
        }

        public void setTotalDays(int totalDays) {
            this.totalDays = totalDays;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(int expireTime) {
            this.expireTime = expireTime;
        }

        public DeviceCloundInfo getServiceDetail() {
            return serviceDetail;
        }

        public void setServiceDetail(DeviceCloundInfo serviceDetail) {
            this.serviceDetail = serviceDetail;
        }

        public int getValidDays() {
            return validDays;
        }

        public void setValidDays(int validDays) {
            this.validDays = validDays;
        }
    }
}


