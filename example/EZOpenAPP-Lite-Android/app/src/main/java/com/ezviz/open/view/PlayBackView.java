package com.ezviz.open.view;

import com.videogo.openapi.bean.EZCloudRecordFile;
import com.videogo.openapi.bean.EZDeviceRecordFile;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/26
 */
public interface PlayBackView extends BaseView{
    /**
     * 播放信息准备成功回调
     */
    public void handlePrepareInfo();

    /**
     * 回放文件sdcard查询成功
     */
    public void handleSearchFileFormDeviceSuccess(EZDeviceRecordFile list);
    /**
     * 回放文件云存储查询成功
     */
    public void handleSearchFileFromCloudSuccess(EZCloudRecordFile list);

    /**
     * 回放文件查询失败
     */
    public void handleSearchFileFail();
}


