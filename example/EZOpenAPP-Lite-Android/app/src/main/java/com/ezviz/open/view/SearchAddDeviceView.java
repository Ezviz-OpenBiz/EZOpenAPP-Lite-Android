package com.ezviz.open.view;

import com.videogo.openapi.bean.EZProbeDeviceInfo;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2017/1/20
 */
public interface SearchAddDeviceView extends BaseView {
    public void showErrorPage(int strId,int errorCode);

    public void showQueryingCamera();

    public void handleQueryCameraFail(int errorCode);

    public void handleQueryCameraSuccess(EZProbeDeviceInfo eZProbeDeviceInfo);
}


