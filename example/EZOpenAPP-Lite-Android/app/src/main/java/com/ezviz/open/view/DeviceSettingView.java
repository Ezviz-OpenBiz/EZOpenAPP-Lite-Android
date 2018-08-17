package com.ezviz.open.view;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/26
 */
public interface DeviceSettingView extends BaseView{
    /**
     * 查询设备信息成功
     */
    public void handleEZOpenDeviceInfo();

    /**
     * 设置项有更新刷新列表
     */
    public void refeshList();

    /**
     * 播放信息准备成功回调
     */
    public void handlePrepareInfo();

    /**
     * 设置清晰度成功
     */
    public void handleSetQualitSuccess();

    /**
     * 设置清晰度失败
     */
    public void handleSetQualityFail();

    /**
     * 抓拍成功
     * @param path  抓图图片存放路径
     */
    public void handleCaptureSuccess(String path);

    /**
     * 删除设备成功
     */
    public void handleDeleteDeviceSuccess();
}


