package com.ezviz.open.presenter;

import android.util.Log;
import com.ezviz.open.model.EZOpenCameraInfo;
import com.ezviz.open.model.EZOpenDetectorInfo;
import com.ezviz.open.model.EZOpenDeviceInfo;
import com.ezviz.open.utils.EZDeviceDBManager;
import com.ezviz.open.view.DeviceView;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZDeviceInfo;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Description:设备相关操作
 * Created by dingwei3
 *
 * @date : 2016/12/9
 */
public class DevicePresenter extends BaseRealmPresenter{
    private static final String TAG = "DevicePresenter";
    private DeviceView mDeviceView;
    private RealmResults<EZOpenCameraInfo> mEZOpenCameraInfoRealmResults;
    private RealmResults<EZOpenDeviceInfo> mEZOpenDeviceInfoRealmResults;
    private RealmResults<EZOpenDetectorInfo> mEZOpenDetectorInfoRealmResults;

    public DevicePresenter(DeviceView deviceView){
        super();
        mDeviceView = deviceView;
    }
    public Realm getRealm(){
        return mRealm;
    }

    /**
     * 查询所有预览通道列表
     * @return
     */
    public RealmResults<EZOpenCameraInfo> getEZOpenCameraInfoList(){
        if (mEZOpenCameraInfoRealmResults == null){
            mEZOpenCameraInfoRealmResults = mRealm.where(EZOpenCameraInfo.class).findAll();
        }
        if (mEZOpenCameraInfoRealmResults != null) {
            for (EZOpenCameraInfo c : mEZOpenCameraInfoRealmResults) {
                Log.d(TAG, "getEZOpenCameraInfoList  " + c.getDeviceSerial());
            }
        }
        return mEZOpenCameraInfoRealmResults;
    }

    /**
     * 查询所有设备列表
     * @return
     */
    public RealmResults<EZOpenDeviceInfo> getEZOpenDeviceInfoList(){
        if (mEZOpenDeviceInfoRealmResults == null){
            mEZOpenDeviceInfoRealmResults = mRealm.where(EZOpenDeviceInfo.class).findAll();
        }
        if (mEZOpenDeviceInfoRealmResults != null) {
            for (EZOpenDeviceInfo c : mEZOpenDeviceInfoRealmResults) {
                if (c.isValid()) {
                    Log.d(TAG, "getEZOpenDeviceInfoList  " + c.getDeviceSerial());
                }
            }
        }
        return mEZOpenDeviceInfoRealmResults;
    }

    /**
     * 在线获取设备列表和预览通道列表
     */
    public void loadDeviceList(){
        Observable observable = Observable.create(new Observable.OnSubscribe<List<EZDeviceInfo>>() {
                                                      @Override
                                                      public void call(Subscriber<? super List<EZDeviceInfo>> subscriber) {
                                                          List<EZDeviceInfo> list_result = null;
                                                          try {
                                                              list_result = realLoadDeviceList();
                                                              subscriber.onNext(list_result);
                                                          } catch (BaseException e) {
                                                              e.printStackTrace();
                                                              subscriber.onError(e);
                                                          }
                                                      }
                                                  });
        subscribeAsync(observable, new Subscriber<List<EZDeviceInfo>>() {
            @Override
            public void onCompleted() {
                mDeviceView.loadFinish();
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                onErrorBaseHandle(((BaseException)e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理


                mDeviceView.loadFinish();
            }
            @Override
            public void onNext(List<EZDeviceInfo> list) {
                mDeviceView.loadFinish();
                if (list != null && list.size() > 0) {
                    EZDeviceDBManager.saveEZOpenInfo(list);
                }
            }

        });
    }

    /**
     * 获取EZDeviceInfo并按照app自己存储形式保存
     * @return
     */
    public List<EZDeviceInfo> realLoadDeviceList() throws BaseException {
        int index = 0;

        int pageSize = 20;
        List<EZDeviceInfo> list_result = new ArrayList<EZDeviceInfo>();
        //while (true) {
            List<EZDeviceInfo> list = EZOpenSDK.getDeviceList(index++, pageSize);
            if (list == null || list.size() == 0) {
                //break;
            }
            if (list != null && list.size() > 0) {
                list_result.addAll(list);
            }
        //}

        return list_result;
    }

    /**
     * 在线获取设备列表和预览通道列表
     */
    public void setDeviceDefence(final String deviceSerial , final int defence){
        mDeviceView.showLoadDialog();
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    EZOpenSDK.setDefence(deviceSerial, defence == 0?EZConstants.EZDefenceStatus.EZDefence_IPC_CLOSE:EZConstants.EZDefenceStatus.EZDefence_IPC_OPEN);
                    subscriber.onNext(defence);
                } catch (BaseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer param) {
                EZDeviceDBManager.setDeviceDefence(deviceSerial, param);
                mDeviceView.dismissLoadDialog();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mDeviceView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException)throwable).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理

            }
        });
    }

}


