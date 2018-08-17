package com.ezviz.open.http;

import com.ezviz.open.model.noconfusion.BaseResponse;
import com.ezviz.open.model.noconfusion.DeviceCloudInfoResp;
import com.ezviz.open.model.noconfusion.DeviceSoundStatusResp;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2017/1/12
 */

public interface EZOpenAPIService{

    /**
     * 获取设备云存储状态
     * @param accessToken   授权过程获取的access_token
     * @param deviceSerial  设备序列号
     * @return
     */
    @FormUrlEncoded
    @POST("/api/lapp/cloud/storage/device/info")
    Observable<DeviceCloudInfoResp> getDeviceCloudInfo(@Field("accessToken")String accessToken, @Field("deviceSerial")String deviceSerial);



    /**
     * 修改监控点名称
     * @param accessToken   授权过程获取的access_token
     * @param deviceSerial  设备序列号
     * @param channelNo     通道号 cameraNo
     * @param name          需要修改的名称
     * @return
     */
    @FormUrlEncoded
    @POST("/api/lapp/camera/name/update")
    Observable<BaseResponse> modifyCameraName(@Field("accessToken")String accessToken, @Field("deviceSerial")String deviceSerial, @Field("channelNo")int channelNo,
                                              @Field("name")String name);

    /**
     * 获取设备提示音开关状态
     * @param accessToken   授权过程获取的access_token
     * @param deviceSerial  设备序列号
     * @return
     */
    @FormUrlEncoded
    @POST("/api/lapp/device/sound/switch/status")
    Observable<DeviceSoundStatusResp> getDeviceSoundStatus(@Field("accessToken")String accessToken, @Field("deviceSerial")String deviceSerial);


    /**
     * 设置设备提示音状态
     * @param accessToken   授权过程获取的access_token
     * @param deviceSerial  设备序列号
     * @param enable        状态：0-关闭，1-开启
     * @return
     */
    @FormUrlEncoded
    @POST("/api/lapp/device/sound/switch/set")
    Observable<BaseResponse> setDeviceSoundStatus(@Field("accessToken")String accessToken, @Field("deviceSerial")String deviceSerial,
                                                  @Field("enable")int enable);

}


