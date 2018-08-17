package com.ezviz.open.view;

import com.videogo.openapi.bean.EZAlarmInfo;

import java.util.List;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/15
 */
public interface MessageView {
    /**
     * 加载更多
     * @param list
     * @param isEnd 是否没有更多需要加载
     */
    public void loadFinish(List<EZAlarmInfo> list,boolean isEnd);

    /**
     * 刷新数据
     * @param list
     * @param isEnd 是否没有更多需要加载
     */
    public void refreshFinish(List<EZAlarmInfo> list,boolean isEnd);

    /**
     * 错误返回
     */
    public void onError();


}


