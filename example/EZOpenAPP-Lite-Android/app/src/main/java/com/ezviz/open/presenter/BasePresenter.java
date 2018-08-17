package com.ezviz.open.presenter;

import com.ezviz.open.model.noconfusion.BaseResponse;
import com.ezviz.open.utils.EZOpenUtils;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZOpenSDK;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ezviz.open.common.EZOpenConstant.HTTP_RESUILT_OK;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2017/1/5
 */
public class BasePresenter {
    private CompositeSubscription mCompositeSubscription;

    /**
     * @param baseResponse
     * @return  错误码
     */
    public int onBaseReponse(BaseResponse baseResponse){
        int code = Integer.parseInt(baseResponse.code);
        if (code == HTTP_RESUILT_OK) {
            return HTTP_RESUILT_OK;
        }  else{
            // TODO: 2017/2/15 公共错误码处理
            onErrorBaseHandle(code);
        }
        return 0;
    }

    /**
     * 错误码基础处理
     * @param errorCode
     */
    public void onErrorBaseHandle(int errorCode){
        switch (errorCode){
            case ErrorCode.ERROR_WEB_PARAM_ERROR:
                // TODO: 2017/2/15 参数错误
                break;
            case ErrorCode.ERROR_WEB_USER_PASSWORD_ERROR:
                // TODO: 2017/2/15 用户不存在
            case ErrorCode.ERROR_WEB_SESSION_ERROR:
                // TODO: 2017/2/15 access_tocken异常
            case ErrorCode.ERROR_TRANSF_ACCESSTOKEN_ERROR:
            case ErrorCode.ERROR_WEB_SESSION_EXPIRE:
                // TODO: 2017/2/15 access_tocken过期
                EZOpenSDK.logout();
                EZOpenUtils.gotoLogin();
                break;
            case ErrorCode.ERROR_WEB_APPKEY_ERROR:
                // TODO: 2017/2/15 appKey异常
                break;
            case ErrorCode.ERROR_WEB_CAMERA_NOT_EXIT:
                // TODO: 2017/2/15 通道不存在
                break;
            case ErrorCode.ERROR_WEB_DEVICE_NOT_EXIT:
                // TODO: 2017/2/15 设备不存在
                break;
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_IS_NULL:
                // TODO: 2017/2/15 硬件特征码为空，版本过低
                break;
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
                // TODO: 2017/2/15 硬件特征码检测失败，版本过低
                break;
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_OP_ERROR:
                // TODO: 2017/2/15 硬件特征码操作失败
                break;
            case ErrorCode.ERROR_WEB_SERVER_EXCEPTION:
                // TODO: 2017/2/15 服务器异常
                break;
            default:
                break;
        }


    }

    public BasePresenter() {
        this.mCompositeSubscription = new CompositeSubscription();
    }

    /**
     * 添加Rx订阅者
     *
     * @param s 订阅者
     */
    private void addSubscription(Subscription s) {
        mCompositeSubscription.add(s);
    }

    /**
     * 移除Rx订阅者
     *
     * @param s 订阅者
     */
    private void removeSubscription(Subscription s) {
        mCompositeSubscription.remove(s);
    }

    /**
     * Presenter 释放
     * 在Activity.onDestroy、Fragment.onDestroyView调用
     */
    public void release() {
        mCompositeSubscription.unsubscribe();
    }

    /**
     * 订阅
     *
     * @param observable 观察对象
     * @param subscriber 订阅者
     */
    public <T> void subscribe(Observable<T> observable, final Subscriber<T> subscriber) {
        Subscription subscription = observable.subscribe(subscriber);
        addSubscription(subscription);
    }

    /**
     * 订阅
     *
     * @param observable 观察对象
     * @param actionOnNext 动作
     */
    public <T> void subscribe(Observable<T> observable, Action1<T> actionOnNext) {
        Subscription subscription = observable.subscribe(actionOnNext);
        addSubscription(subscription);
    }

    /**
     * 订阅
     *
     * @param observable 观察对象
     * @param actionOnNext 动作
     * @param actionError  error
     */
    public <T> void subscribe(Observable<T> observable, Action1<T> actionOnNext,Action1<Throwable> actionError) {
        Subscription subscription = observable.subscribe(actionOnNext,actionError);
        addSubscription(subscription);
    }
    /**
     * 订阅（走异步处理）
     *
     * @param observable 观察对象
     * @param subscriber 订阅者
     */
    public <T> void subscribeAsync(Observable<T> observable, final Subscriber<T> subscriber) {
        subscribe(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()), subscriber);
    }


    /**
     * 订阅（走异步处理）
     *
     * @param observable 观察对象
     * @param actionOnNext   动作
     */
    public <T> void subscribeAsync(Observable<T> observable, Action1<T> actionOnNext,Action1<Throwable> actionError) {
        subscribe(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()), actionOnNext,actionError);
    }
}


