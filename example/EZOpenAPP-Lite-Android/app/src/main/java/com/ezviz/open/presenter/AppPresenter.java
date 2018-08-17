package com.ezviz.open.presenter;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.ezviz.open.R;
import com.ezviz.open.utils.EZOpenUtils;
import com.ezviz.open.view.AppView;
import com.videogo.openapi.EZOpenSDK;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/27
 */
public class AppPresenter {
    private AppView mAppView;
    public AppPresenter(AppView appView){
        mAppView = appView;
    }

    /**
     * 账号切换
     */
    public void switchAccount(){
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                EZOpenSDK.logout();
                subscriber.onNext(null);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Action1<Void>() {
            @Override
            public void call(Void param) {
                mAppView.handleLogoutSuccess();
            }
        });
    }

    /**
     * 获取当前app版本以及sdk版本
     * @param activity
     * @return
     */
    public String getVersionString(FragmentActivity activity){
        String sdkversion = EZOpenSDK.getVersion();
        String appVersion = EZOpenUtils.getAppVersionNameInfo(activity);
       return String.format(activity.getResources().getString(R.string.version), TextUtils.isEmpty(appVersion)?"":appVersion,TextUtils.isEmpty(sdkversion)?"":sdkversion);
    }

}


