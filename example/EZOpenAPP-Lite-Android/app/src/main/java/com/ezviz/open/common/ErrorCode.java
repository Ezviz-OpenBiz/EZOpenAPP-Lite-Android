package com.ezviz.open.common;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2017/1/12
 */
public class ErrorCode {


    /**
     * 设备不支持云存储
     */
    public static final int HTTP_ERROR_CODE_NOSUPPORT_CLOUND = 60020;

    /**
     * 参数错误	参数为空或格式不正确
     */
    public static final int HTTP_ERROR_CODE_PARAM_ERROR = 10001;

    /**
     * accessToken异常或过期
     */
    public static final int HTTP_ERROR_CODE_ACCESSTOKEN_ERROR = 10002;

    /**
     * 用户不存在
     */
    public static final int HTTP_ERROR_CODE_NO_USER = 10004;

    /**
     * appKey异常
     */
    public static final int HTTP_ERROR_CODE_APPKEY_ERROR = 10005;

    /**
     * 设备不存在
     */
    public static final int HTTP_ERROR_CODE_NO_DEVICE = 20002;

    /**
     * deviceSerial不合法
     */
    public static final int HTTP_ERROR_CODE_DEVICESERIAL_ISVALID = 20014;

    /**
     * 该用户不拥有该设备
     */
    public static final int HTTP_ERROR_CODE_USER_NOT_OWNED = 20018;



    /**
     * 该用户下通道不存在
     */
    public static final int HTTP_ERROR_CODE_NO_CAMERA = 20032;

    /**
     * 数据异常
     */
    public static final int HTTP_ERROR_CODE_DATA_ERROR = 49999;

    /**
     * 未知错误
     */
    public static final int HTTP_ERROR_CODE_UNKNOWN_ERROR = 60012;


}


