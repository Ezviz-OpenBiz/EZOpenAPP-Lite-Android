package com.ezviz.open.presenter;

import android.text.TextUtils;

import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.http.EZOpenAPI;
import com.ezviz.open.model.noconfusion.BaseResponse;
import com.ezviz.open.utils.EZCameraDBManager;
import com.ezviz.open.utils.EZDeviceDBManager;
import com.ezviz.open.view.ModifyNameView;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;


/**
 * Description: 修改名称
 * Created by dingwei3
 *
 * @date : 2017/1/11
 */
public class ModifyNamePresenter extends BasePresenter{

    private String mDeviceSiral;
    private int mCameraNo;

    private int mType;
    private ModifyNameView mModifyNameView;

    public ModifyNamePresenter(ModifyNameView modifyNameView) {
        mModifyNameView = modifyNameView;
    }
    public void setDeviceSiral(String deviceSiral) {
        mDeviceSiral = deviceSiral;
    }
    public void setCameraNo(int cameraNo) {
        mCameraNo = cameraNo;
    }

    /**
     * 修改设备名称
     */
    public void modifyDeviceName(final String deviceSerial , final String name){
        if (TextUtils.isEmpty(name)){
            return;
        }
        mModifyNameView.showLoadDialog();
        Observable observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean b = EZOpenSDK.setDeviceName(deviceSerial,name);
                    subscriber.onNext(b);
                } catch (BaseException e) {
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Action1<Boolean>() {
            @Override
            public void call(Boolean ret) {
                if (ret){
                    EZDeviceDBManager.setDeviceName(deviceSerial,name);
                    mModifyNameView.handleModifySuccess();
                }
                mModifyNameView.dismissLoadDialog();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                mModifyNameView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException) throwable).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
            }
        });
    }

    /**
     * 修改通道名称
     */
    public void modifyCameraName (final String deviceSerial , final int cameraNo, final String name){
        if (TextUtils.isEmpty(name)){
            return;
        }
        mModifyNameView.showLoadDialog();
        Observable<BaseResponse> observable = EZOpenAPI.mEZOpenAPIService.modifyCameraName(EZOpenSDK.getEZAccessToken().getAccessToken(),deviceSerial,
                cameraNo,name);
        subscribeAsync(observable, new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                mModifyNameView.dismissLoadDialog();
                int code = onBaseReponse(baseResponse);
                if (code == EZOpenConstant.HTTP_RESUILT_OK){
                    // TODO: 2017/1/16 设置监听点通道名称成功
                    EZCameraDBManager.setCameraName(deviceSerial,cameraNo,name);
                    mModifyNameView.handleModifySuccess();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                mModifyNameView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException) throwable).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
            }
        });
    }
}


