package com.ezviz.open.view;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/26
 */
public interface PlayView extends BaseView{
    /**
     * 查询设备信息成功
     */
    public void handleEZOpenDeviceInfo();

    /**
     * 查询通道信息成功
     */
    public void handleEZOpenCameraInfo();

    /**
     * 播放信息准备成功回调
     */
    public void handlePrepareInfo();

    /**
     * 设置清晰度成功
     */
    public void handleSetQualitSuccess();


}


