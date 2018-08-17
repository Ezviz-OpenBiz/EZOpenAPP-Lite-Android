package com.ezviz.open.presenter;

import com.ezviz.open.view.DeviceView;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZDeviceUpgradeStatus;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Description:设备升级相关操作
 * Created by dingwei3
 *
 * @date : 2016/12/9
 */
public class DeviceUpgradePresenter extends BasePresenter{
    private static final String TAG = "DevicePresenter";
    private DeviceView mDeviceView;

    public DeviceUpgradePresenter(){
        super();
    }


    /**
     *
     * 获取设备升级状态
     * @param deviceSerial
     */
    public void getDeviceUpgradeStatus(final String deviceSerial){
        Observable.create(new Observable.OnSubscribe<EZDeviceUpgradeStatus>() {
            @Override
            public void call(Subscriber<? super EZDeviceUpgradeStatus> subscriber) {
                try {
                    EZDeviceUpgradeStatus ezDeviceUpgradeStatus =  EZOpenSDK.getDeviceUpgradeStatus(deviceSerial);
                    subscriber.onNext(ezDeviceUpgradeStatus);
                } catch (BaseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Action1<EZDeviceUpgradeStatus>() {
            @Override
            public void call(EZDeviceUpgradeStatus param) {
                mDeviceView.dismissLoadDialog();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mDeviceView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException) throwable).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
            }
        });
    }

    /**
     *
     * 设备升级
     * @param deviceSerial
     */
    public void upgradeDevice(final String deviceSerial){
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    EZOpenSDK.upgradeDevice(deviceSerial);
                    subscriber.onNext(null);
                } catch (BaseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Action1<Void>() {
            @Override
            public void call(Void ret) {

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                onErrorBaseHandle(((BaseException) throwable).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理

            }
        });
    }
}


