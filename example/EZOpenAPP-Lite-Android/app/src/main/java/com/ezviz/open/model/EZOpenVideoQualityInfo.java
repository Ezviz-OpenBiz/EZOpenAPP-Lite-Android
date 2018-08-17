package com.ezviz.open.model;

import com.videogo.openapi.bean.EZVideoQualityInfo;

import io.realm.RealmObject;

/**
 * Description: 通道清晰度信息类
 * Created by dingwei3
 *
 * @date : 2017/2/28
 */
public class EZOpenVideoQualityInfo extends RealmObject {
    /**
     * 清晰度名称
     */
    private String videoQualityName;
    /**
     * 清晰度等级
     */
    private int videoLevel;

    /**
     * 码流类型
     */
    private int streamType;


    public String getVideoQualityName() {
        return videoQualityName;
    }

    public void setVideoQualityName(String videoQualityName) {
        this.videoQualityName = videoQualityName;
    }

    public int getVideoLevel() {
        return videoLevel;
    }

    public void setVideoLevel(int videoLevel) {
        this.videoLevel = videoLevel;
    }

    public int getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public void copy(EZVideoQualityInfo videoQualityInfo){
        setVideoLevel(videoQualityInfo.getVideoLevel());
        setStreamType(videoQualityInfo.getStreamType());
        setVideoQualityName(videoQualityInfo.getVideoQualityName());
    }

}


