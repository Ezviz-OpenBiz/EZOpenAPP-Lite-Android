package com.ezviz.open.common;
import com.ezviz.open.R;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/26
 */
public class EZOpenConstant {
    public static final String EXTRA_DEVICE_SERIAL = "DeviceSerial";
    public static final String EXTRA_CAMERA_NO = "CameraNo";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_MODIFY_NAME = "name";
    public static final String EXTRA_MODIFY_NAME_TYPE = "modify_name_type";
    public static final String EXTRA_DEVICE_VERSION_DES = "device_version_des";
    public static final String EXTRA_DEVICE_VERIFYCODE= "device_verifycode ";
    public static final String EXTRA_DEVICE_TYPE = "device_type";
    public static final String EXTRA_WIFI_PASSWORD = "WiFi_password";
    public static final String EXTRA_WIFI_SSID = "WiFi_ssid";
    public static final String EXTRA_SUPPORT_NETWORK = "support_network";
    public static final String EXTRA_SUPPORT_WIFI = "support_WiFi";
    public static final String EXTRA_DEVICE_ADDED = "device_added";
    public static final String EXTRA_FROM_PAGE = "from_page";


    public static final int HTTP_RESUILT_OK = 200;

    /**
     * 设备在线状态
     */
    public static final int DEVICE_ONLINE = 1;
    /**
     * 设备离线状态
     */
    public static final int DEVICE_OFFLINE = 2;

    /**
     * 流畅
     */
    public static final int VIDEO_LEVEL_FLUNET = 0;
    /**
     * 均衡
     */
    public static final int VIDEO_LEVEL_BALANCED = 1;
    /**
     * 高清
     */
    public static final int VIDEO_LEVEL_HD = 2;

    /**
     *  云存储状态
     */
    public class CloudStorageStatus {
        /**
         * 设备不支持云存储
         */
        public static final int DEVICE_NO_SUPPORT_CLOUD_STORAGE = -2;
        /**
         * 设备未开通云存储
         */
        public static final int DEVICE_NOT_THROUGH_THE_CLOUD_STORAGE = -1;

        /**
         * 设备未激活云存储
         */
        public static final int DEVICE_NOT_ACTIVATING_CLOUD_STORAGE = 0;

        /**
         * 设备激活云存储
         */
        public static final int DEVICE_ACTIVATING_CLOUD_STORAGE = 1;

        /**
         * 设备云存储已过期
         */
        public static final int DEVICE_CLOUD_STORAGE_EXPIRED = 2;

    }

    public enum AlarmType {
        /** 红外 PIR Event pir */
        BODY_FEEL(10000, R.drawable.message_infrared, R.string.alarm_type_infrared,R.color.alarm_type_infrared_color),

        /** 水侵报警 Waterlogging Alarm */
        WATER_ALARM(10008, R.drawable.message_water,R.string.alarm_type_water,R.color.alarm_type_water_color),


        /** 热成像火点报警 **/
        THERMAL_IMAGING_FIRE(10041, R.drawable.message_fire,R.string.thermal_imaging_fire,R.color.thermal_imaging_fire_color),

        /** 移动侦测报警 Motion Detection Alarm motiondetect */
        MOTION_DETECTION_ALARM(10002,R.drawable.message_move,R.string.alarm_type_motion_detection,R.color.alarm_type_motion_detection_color),

        /** 未知 */
        UNKNOWN(0, R.drawable.message_other,R.string.alarm_type_other,R.color.alarm_type_other_color);



        private int id;
        private boolean hasCamera;
        private int drawableResId;
        private int detailDrawableResId;
        private int textResId;
        private int colorId;

        AlarmType(int id,int drawableResId, int textResId,int colorId) {
            this.id = id;
            this.drawableResId = drawableResId;
            this.textResId = textResId;
            this.colorId = colorId;
        }




        public static AlarmType getAlarmTypeById(int id) {
            for (AlarmType e : AlarmType.values()) {
                if (id == e.id) return e;
            }
            return UNKNOWN;
        }

        public int getId() {
            return id;
        }

        public int getDrawableResId() {
            return drawableResId;
        }

        public int getTextResId() {
            return textResId;
        }

        public int getColorId() {
            return colorId;
        }
    }
}


