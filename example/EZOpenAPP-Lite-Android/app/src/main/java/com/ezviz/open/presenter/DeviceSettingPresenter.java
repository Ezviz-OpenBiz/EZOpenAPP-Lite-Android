package com.ezviz.open.presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.http.EZOpenAPI;
import com.ezviz.open.model.EZOpenDeviceInfo;
import com.ezviz.open.model.noconfusion.BaseResponse;
import com.ezviz.open.model.noconfusion.DeviceCloudInfoResp;
import com.ezviz.open.model.noconfusion.DeviceSoundStatusResp;
import com.ezviz.open.utils.EZDeviceDBManager;
import com.ezviz.open.utils.EZLog;
import com.ezviz.open.utils.JsonUtils;
import com.ezviz.open.view.DeviceSettingView;
import com.ezviz.open.view.avctivity.DeviceUpgradeActivity;
import com.ezviz.open.view.avctivity.ModifyNameActivity;
import com.ezviz.open.view.widget.CommomAlertDialog;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZDeviceVersion;
import com.videogo.openapi.bean.EZStorageStatus;
import io.realm.RealmChangeListener;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import com.ezviz.open.R;
/**
 * Description: 设备设置相关操作
 * Created by dingwei3
 *
 * @date : 2016/12/26
 */
public class DeviceSettingPresenter extends BaseRealmPresenter {
    private static final String TAG = "DeviceSettingPresenter";
    private Context mContext;
    private EZOpenDeviceInfo mOpenDeviceInfo;
    private RealmChangeListener mDeviceRealmChangeListener;
    private DeviceSettingView mDeviceSettingView;
    private List<DeviceSettingItem> mDeviceSettingItems;
    private EZDeviceVersion mDeviceVersion;
    private List<EZStorageStatus> mEZStorageStatusList;
    private DeviceCloudInfoResp mDeviceCloudInfo;
    private DeviceSoundStatusResp.DeviceSoundStatus mDeviceSoundStatus;
    private int mDeviceCloudStatus = -100;
    private String mDeviceSerial;

    public DeviceSettingPresenter(Context context, DeviceSettingView deviceSettingView) {
        super();
        mContext = context;
        mDeviceSettingView = deviceSettingView;
        mDeviceRealmChangeListener = new RealmChangeListener<EZOpenDeviceInfo>() {
            @Override
            public void onChange(EZOpenDeviceInfo element) {
                if (element != null && element.isValid()) {
                    Log.d(TAG, "mDeviceRealmChangeListener  element = " + element.getDeviceSerial());
                    mDeviceSettingView.handleEZOpenDeviceInfo();
                }
            }
        };
    }

    /**
     * 准备设备信息
     *
     * @param deviceSerial
     */
    public void prepareOpenDeviceInfo(String deviceSerial) {
        if (TextUtils.isEmpty(deviceSerial)) {
            EZLog.d(TAG, "deviceSerial is null");
            return;
        }
        mDeviceSerial = deviceSerial;
        mOpenDeviceInfo = EZDeviceDBManager.findFirst(deviceSerial);
        mOpenDeviceInfo.addChangeListener(mDeviceRealmChangeListener);
        if (mOpenDeviceInfo != null && mOpenDeviceInfo.isValid()) {
            mDeviceSettingView.handlePrepareInfo();
        }
        getDeviceVersion(deviceSerial);
        getSDcardStatus(deviceSerial);
        getDeviceCloudStatus(deviceSerial);
        getDeviceSoundStatus(deviceSerial);
    }

    public EZOpenDeviceInfo getOpenDeviceInfo() {
        if (TextUtils.isEmpty(mDeviceSerial)){
            return null;
        }
        if (mOpenDeviceInfo == null || !mOpenDeviceInfo.isValid()) {
            mOpenDeviceInfo = EZDeviceDBManager.findFirst(mDeviceSerial);
            if (mOpenDeviceInfo != null && mOpenDeviceInfo.isValid()) {
                mOpenDeviceInfo.addChangeListener(mDeviceRealmChangeListener);
            }else{
                return null;
            }
        }
        return mOpenDeviceInfo;
    }

    /**
     * 获取设备最新版本号
     *
     * @param deviceserial
     */
    public void getDeviceVersion(final String deviceserial) {
        Observable observable = Observable.create(new Observable.OnSubscribe<EZDeviceVersion>() {
            @Override
            public void call(Subscriber<? super EZDeviceVersion> subscriber) {
                try {
                    EZDeviceVersion mDeviceVersion = EZOpenSDK.getDeviceVersion(deviceserial);
                    subscriber.onNext(mDeviceVersion);
                } catch (BaseException e) {
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Subscriber<EZDeviceVersion>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mDeviceSettingView.refeshList();
                onErrorBaseHandle(((BaseException) e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理

            }

            @Override
            public void onNext(EZDeviceVersion ezDeviceVersion) {
                mDeviceVersion = ezDeviceVersion;
                mDeviceSettingView.refeshList();
            }
        });
    }


    /**
     * 获取设备存储状态
     *
     * @param deviceserial
     */
    public void getSDcardStatus(final String deviceserial) {
        Observable observable = Observable.create(new Observable.OnSubscribe<List<EZStorageStatus>>() {
            @Override
            public void call(Subscriber<? super List<EZStorageStatus>> subscriber) {
                try {
                    List<EZStorageStatus> List = EZOpenSDK.getStorageStatus(deviceserial);
                    subscriber.onNext(List);
                } catch (BaseException e) {
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Subscriber<List<EZStorageStatus>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mDeviceSettingView.refeshList();
                onErrorBaseHandle(((BaseException) e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理

            }

            @Override
            public void onNext(List<EZStorageStatus> list) {
                mEZStorageStatusList = list;
                mDeviceSettingView.refeshList();
            }
        });
    }

    /**
     * 获取设备云存储状态
     *
     * @param deviceserial
     */
    public void getDeviceCloudStatus(final String deviceserial) {
        Observable<DeviceCloudInfoResp> observable = EZOpenAPI.mEZOpenAPIService.getDeviceCloudInfo(EZOpenSDK.getEZAccessToken().getAccessToken(), deviceserial);
        subscribeAsync(observable, new Action1<DeviceCloudInfoResp>() {
            @Override
            public void call(DeviceCloudInfoResp deviceCloudInfo) {
                mDeviceCloudInfo = deviceCloudInfo;
                int code = Integer.parseInt(deviceCloudInfo.code);
                if (code == EZOpenConstant.HTTP_RESUILT_OK) {
                    mDeviceCloudStatus = mDeviceCloudInfo.getDeviceCloundInfo().getStatus();
                }  else{
                    onErrorBaseHandle(code);
                    // TODO: 2017/2/15 除公共错误码之后的错误码处理
                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * 获取设备语音提示状态
     *
     * @param deviceSerial
     */
    public void getDeviceSoundStatus(final String deviceSerial) {
        Observable<DeviceSoundStatusResp> observable = EZOpenAPI.mEZOpenAPIService.getDeviceSoundStatus(EZOpenSDK.getEZAccessToken().getAccessToken(), deviceSerial);
        subscribeAsync(observable, new Action1<DeviceSoundStatusResp>() {
            @Override
            public void call(DeviceSoundStatusResp deviceSoundStatusResp) {
                EZLog.d(TAG,"hahahaha"+ JsonUtils.toJson(deviceSoundStatusResp));
                int code = onBaseReponse(deviceSoundStatusResp);
                if (code == EZOpenConstant.HTTP_RESUILT_OK) {
                    // TODO: 2017/1/12 设备语音提示状态
                    mDeviceSoundStatus = deviceSoundStatusResp.getData();
                    mDeviceSettingView.refeshList();
                }  else{
                    // TODO: 2017/2/15 除公共错误码之后的错误码处理
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * 设置设备语音提示状态
     *
     * @param deviceSerial
     */
    public void setDeviceSoundStatus(final String deviceSerial, final int enable) {
        mDeviceSettingView.showLoadDialog();
        Observable<BaseResponse> observable = EZOpenAPI.mEZOpenAPIService.setDeviceSoundStatus(EZOpenSDK.getEZAccessToken().getAccessToken(), deviceSerial, enable);
        subscribeAsync(observable, new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {

                mDeviceSettingView.dismissLoadDialog();
                int code = onBaseReponse(baseResponse);
                if (code == EZOpenConstant.HTTP_RESUILT_OK) {
                    // TODO: 2017/1/18 设置设备语音开关成功
                    mDeviceSoundStatus.setEnable(enable);
                    mDeviceSettingView.refeshList();
                } else{
                    // TODO: 2017/2/15 除公共错误码之后的错误码处理
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                mDeviceSettingView.dismissLoadDialog();
            }
        });
    }

    /**
     * 设置设备布撤防
     *
     * @param deviceSerial 设备序列号
     * @param defence      布撤防状态
     */
    public void setDeviceDefence(final String deviceSerial, final int defence) {
        mDeviceSettingView.showLoadDialog();
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    EZOpenSDK.setDefence(deviceSerial, defence == 0 ? EZConstants.EZDefenceStatus.EZDefence_IPC_CLOSE : EZConstants.EZDefenceStatus.EZDefence_IPC_OPEN);
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
                mDeviceSettingView.dismissLoadDialog();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable e) {
                mDeviceSettingView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException) e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
            }
        });
    }

    /**
     * 在线获取设备列表和预览通道列表
     */
    public void setDeviceEncypt(final String deviceSerial, final int encrypt, final String verifyCode) {
        mDeviceSettingView.showLoadDialog();
        Observable observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    EZOpenSDK.setDeviceEncryptStatus(deviceSerial, verifyCode, encrypt == 1);
                    subscriber.onNext(encrypt);
                } catch (BaseException e) {
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Action1<Integer>() {
            @Override
            public void call(Integer ret) {
                EZDeviceDBManager.setDeviceEncrypt(deviceSerial, ret);
                mDeviceSettingView.dismissLoadDialog();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable e) {
                e.printStackTrace();
                mDeviceSettingView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException) e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
            }
        });
    }

    /**
     * 删除设备
     *
     * @param deviceSerial 设备序列号
     */
    public void deleteDevice(final String deviceSerial) {
        mDeviceSettingView.showLoadDialog();
        Observable<Boolean> observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean ret = EZOpenSDK.deleteDevice(deviceSerial);
                    subscriber.onNext(ret);
                } catch (BaseException e) {
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Action1<Boolean>() {
            @Override
            public void call(Boolean ret) {
                mDeviceSettingView.dismissLoadDialog();
                if (ret) {
                    // TODO: 2017/1/13 删除设备成功
                    EZDeviceDBManager.deleteDevice(deviceSerial);
                    mDeviceSettingView.handleDeleteDeviceSuccess();
                } else {
                    // TODO: 2017/1/13 删除设备失败

                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                // TODO: 2017/1/13 删除设备异常处理
                mDeviceSettingView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException) throwable).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
            }
        });
    }

    /**
     * 获取显示的items
     *
     * @return
     */
    public List<DeviceSettingItem> getDeviceSettingItems() {
        if (mDeviceSettingItems == null) {
            mDeviceSettingItems = new ArrayList<DeviceSettingItem>();
        } else {
            mDeviceSettingItems.clear();
        }
        //设备名称
        mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_NEXT, R.string.device_setting_name, getOpenDeviceInfo().getDeviceName(), false));
        //分割线
        mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_DIVIDER, 0, false));
        if (getOpenDeviceInfo().getStatus() == 1) {
            //活动检测开关
            if (getOpenDeviceInfo().isSupportDefence()) {
                mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_SWITCH, R.string.device_setting_defence, getOpenDeviceInfo().getDefence() != 0, true));
            }
            if (mDeviceSoundStatus != null && mDeviceSoundStatus.getEnable() != -1) {
                //设备语音提示开关
                mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_SWITCH, R.string.device_setting_voice_rompt, mDeviceSoundStatus.getEnable() == 1, true));
            }
            //视频/图片加解密开关
            mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_SWITCH, R.string.device_setting_picture_video_entrcy, getOpenDeviceInfo().getIsEncrypt() == 1, false));
            //分割线
            mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_DIVIDER, 0, false));

            //TF卡
            if (mEZStorageStatusList == null) {
                mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_TEXT, R.string.device_setting_sdcard, true));
            } else {
                String storageStatus = "";
                if (mEZStorageStatusList.size() <= 0 || mEZStorageStatusList.get(0).getStatus() == 1) {
                    storageStatus = mContext.getResources().getString(R.string.device_setting_sdcard_status_no);
                } else {
                    storageStatus = mContext.getResources().getString(mEZStorageStatusList.get(0).getStatus() == 2 ? R.string.device_setting_sdcard_status_not_format : R.string.device_setting_sdcard_status_using);
                }
                mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_TEXT, R.string.device_setting_sdcard, storageStatus, true));
            }
        }

        //云存储h5页面有问题，暂时屏蔽
        DeviceSettingItem deviceitem = getDeviceCloudStoreageItem(getOpenDeviceInfo().getStatus() == 1);
        if (deviceitem != null) {
            //云存储
            mDeviceSettingItems.add(deviceitem);
        }
        if (getOpenDeviceInfo().getStatus() == 1) {
            //固件版本
            if (mDeviceVersion == null) {
                mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_TEXT, R.string.device_setting_firmware, false));
            } else {
                mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_TEXT, R.string.device_setting_firmware, mDeviceVersion.getIsNeedUpgrade() != 0 ? mDeviceVersion.getNewestVersion() : mContext.getResources().getString(R.string.device_setting_firmware_newest), false));
            }
        }
        //分割线
        mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_DIVIDER, 0, false));
        //删除设备
        mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_DELETE, R.string.device_setting_del, false));
        //分割线
        mDeviceSettingItems.add(new DeviceSettingItem(DeviceSettingItem.TYPE_DIVIDER, 0, false));
        return mDeviceSettingItems;
    }


    private DeviceSettingItem getDeviceCloudStoreageItem(boolean showDivider) {
        String value = "";
        if (mDeviceCloudStatus == -100) {
            return null;
        }
        // TODO: 2017/1/12 设备云存储状态
        switch (mDeviceCloudStatus) {
            case EZOpenConstant.CloudStorageStatus.DEVICE_NO_SUPPORT_CLOUD_STORAGE:
                // TODO: 2017/1/16 设备不支持云存储
                value = mContext.getResources().getString(R.string.device_setting_cloud_status_nosupport);
                break;
            case EZOpenConstant.CloudStorageStatus.DEVICE_CLOUD_STORAGE_EXPIRED:
                // TODO: 2017/1/16 云存储过期
                value = mContext.getResources().getString(R.string.device_setting_cloud_status_expire);
                break;
            case EZOpenConstant.CloudStorageStatus.DEVICE_ACTIVATING_CLOUD_STORAGE:
                // TODO: 2017/1/16 云存储已激活
                value = mContext.getResources().getString(R.string.device_setting_cloud_status_using);
                break;
            case EZOpenConstant.CloudStorageStatus.DEVICE_NOT_ACTIVATING_CLOUD_STORAGE:
                // TODO: 2017/1/16 云存储未激活
                value = mContext.getResources().getString(R.string.device_setting_cloud_status_not_activating);
                break;
            case EZOpenConstant.CloudStorageStatus.DEVICE_NOT_THROUGH_THE_CLOUD_STORAGE:
                // TODO: 2017/1/16 设备未开通云存储
                value = mContext.getResources().getString(R.string.device_setting_cloud_status_not_open);
                break;
            default:
                break;
        }

        if (!TextUtils.isEmpty(value)) {
            DeviceSettingItem deviceItem = new DeviceSettingItem(DeviceSettingItem.TYPE_NEXT, R.string.device_setting_cloud, value, showDivider);
            return deviceItem;
        }
        return null;
    }

    /**
     * item点击事件处理
     *
     * @param titleResId
     */
    public void onDeviceSettingItemClick(int titleResId) {
        switch (titleResId) {
            case R.string.device_setting_name:
                //点击修改名称
                // TODO: 2017/1/12  点击修改名称
                ModifyNameActivity.startModifyDeviceNameActivity(mContext, getOpenDeviceInfo().getDeviceName(), getOpenDeviceInfo().getDeviceSerial());
                break;
            case R.string.device_setting_sdcard:
                //点击TF卡
                // TODO: 2017/1/12  点击TF卡
                break;
            case R.string.device_setting_cloud:
                //点击云存储
                // TODO: 2017/1/12  点击云存储
                    EZOpenSDK.openCloudPage(getOpenDeviceInfo().getDeviceSerial());
                break;
            case R.string.device_setting_firmware:
                //点击固件版本
                // TODO: 2017/1/12  点击固件版本
                //有升级才响应点击事件
                if (mDeviceVersion != null && mDeviceVersion.getIsNeedUpgrade() != 0 ){
                    DeviceUpgradeActivity.startDeviceUpgradeActivity(mContext, mDeviceVersion.getUpgradeDesc());
                }
                break;
            case R.string.device_setting_del:
                //点击删除设备
                // TODO: 2017/1/12  点击删除设备
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(mContext);
                deleteDialog.setTitle(R.string.string_delete_device_title);
                deleteDialog.setMessage(R.string.string_delete_device_tip);
                deleteDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDevice(mDeviceSerial);
                    }
                });
                deleteDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                deleteDialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * item switch 点击事件处理
     *
     * @param titleResId
     */
    public void onDeviceSettingSwitchItemClick(int titleResId) {
        switch (titleResId) {
            case R.string.device_setting_defence:
                //点击 活动检测
                // TODO: 2017/1/12  活动检测
                setDeviceDefence(getOpenDeviceInfo().getDeviceSerial(), (getOpenDeviceInfo().getDefence() == 0 ? 1 : 0));
                break;
            case R.string.device_setting_voice_rompt:
                //点击 语音提示
                // TODO: 2017/1/12  语音提示
                setDeviceSoundStatus(mDeviceSerial, mDeviceSoundStatus.getEnable() == 0 ? 1 : 0);
                break;
            case R.string.device_setting_picture_video_entrcy:
                //点击视频/图片加密
                // TODO: 2017/1/12  视频/图片加密
                if (getOpenDeviceInfo().getIsEncrypt() == 1) {
                    CommomAlertDialog.DeviceEncryptDialog(mContext, new CommomAlertDialog.VerifyCodeInputListener() {
                        @Override
                        public void onInputVerifyCode(String verifyCode) {
                            if (!TextUtils.isEmpty(verifyCode)) {
                                setDeviceEncypt(mDeviceSerial, getOpenDeviceInfo().getIsEncrypt() == 0 ? 1 : 0, verifyCode);
                            }
                        }
                    }).show();
                } else {
                    setDeviceEncypt(mDeviceSerial, getOpenDeviceInfo().getIsEncrypt() == 0 ? 1 : 0, "");
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void release() {
        if (mOpenDeviceInfo != null) {
            mOpenDeviceInfo.removeChangeListener(mDeviceRealmChangeListener);
        }
    }

    public class DeviceSettingItem {
        public static final int TYPE_DELETE = 0;
        public static final int TYPE_NEXT = 1;
        public static final int TYPE_SWITCH = 2;
        public static final int TYPE_DIVIDER = 3;
        public static final int TYPE_TEXT = 4;
        /**
         * title
         */
        int titleResId;
        /**
         * item类型
         */
        int type;
        /**
         * 是否显示item下方的分割线
         */
        boolean isDisplayDivider;

        Object value;



        DeviceSettingItem(int type, int titleResId, boolean isDisplayDivider) {
            this.type = type;
            this.titleResId = titleResId;
            this.isDisplayDivider = isDisplayDivider;
        }

        DeviceSettingItem(int type, int titleResId, Object value, boolean isDisplayDivider) {
            this.type = type;
            this.titleResId = titleResId;
            this.isDisplayDivider = isDisplayDivider;
            this.value = value;
        }

        public int getTitleResId() {
            return titleResId;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean isDisplayDivider() {
            return isDisplayDivider;
        }

        public void setDisplayDivider(boolean displayDivider) {
            isDisplayDivider = displayDivider;
        }

        public void setTitleResId(int titleResId) {
            this.titleResId = titleResId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}


