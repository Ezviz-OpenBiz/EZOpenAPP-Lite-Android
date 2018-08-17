package com.ezviz.open.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;

import com.ezviz.open.model.DeviceEncrypt;
import com.ezviz.open.model.EZOpenCameraInfo;
import com.ezviz.open.model.EZOpenDeviceInfo;
import com.ezviz.open.utils.CameraCaptureCache;
import com.ezviz.open.utils.DataManager;
import com.ezviz.open.utils.EZCameraDBManager;
import com.ezviz.open.utils.EZDeviceDBManager;
import com.ezviz.open.utils.EZOpenUtils;
import com.ezviz.open.view.PlayView;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import com.ezviz.open.R;
/**
 * Description: 预览
 * Created by dingwei3
 *
 * @date : 2016/12/26
 */
public class PlayPresenter extends BaseRealmPresenter {

    private static final String TAG = "PlayPresenter";
    private RealmResults<EZOpenCameraInfo> mEZOpenCameraInfoRealmResults;
    private RealmResults<EZOpenDeviceInfo> mEZOpenDeviceInfoRealmResults;

    private EZOpenDeviceInfo mOpenDeviceInfo;
    private EZOpenCameraInfo mOpenCameraInfo;
    private DeviceEncrypt mDeviceEncrypt;
    private String encryptPassword;

    private PlayView mPlayView;
    private RealmChangeListener mDeviceRealmChangeListener;
    private RealmChangeListener mCameraRealmChangeListener;

    private String mDeviceSerial;
    private int mCameraNo;

    public PlayPresenter(PlayView playView) {
        super();
        this.mPlayView = playView;
        mDeviceRealmChangeListener = new RealmChangeListener<EZOpenDeviceInfo>() {
            @Override
            public void onChange(EZOpenDeviceInfo element) {
                if (element != null && element.isValid()) {
                    Log.d(TAG, "mDeviceRealmChangeListener  element = " + element.getDeviceSerial());
                    mPlayView.handleEZOpenDeviceInfo();
                }
            }
        };
        mCameraRealmChangeListener = new RealmChangeListener<EZOpenCameraInfo>() {
            @Override
            public void onChange(EZOpenCameraInfo element) {
                if (element != null && element.isValid()) {
                    Log.d(TAG, "mCameraRealmChangeListener  element = " + element.getDeviceSerial() + "  " + element.getCameraNo());
                    mPlayView.handleEZOpenCameraInfo();
                }
            }
        };
    }

    public void prepareInfo(String deviceSerial, int cameraNo) {
        long b = System.currentTimeMillis();
        mDeviceSerial = deviceSerial;
        mCameraNo = cameraNo;
        mOpenDeviceInfo = EZDeviceDBManager.findFirst(deviceSerial);
        mOpenCameraInfo = EZCameraDBManager.findFirst(deviceSerial, cameraNo);
        encryptPassword = EZDeviceDBManager.getDevPwd(deviceSerial);
        mOpenDeviceInfo.addChangeListener(mDeviceRealmChangeListener);
        mOpenCameraInfo.addChangeListener(mCameraRealmChangeListener);
        mPlayView.handlePrepareInfo();
        Log.d(TAG, "prepareInfo  time = " + (System.currentTimeMillis() - b));

    }

    public void preparePlayBackInfo(String deviceSerial) {
        mDeviceSerial = deviceSerial;
        encryptPassword = EZDeviceDBManager.getDevPwd(deviceSerial);
        mPlayView.handlePrepareInfo();
    }

    public String getDeviceEncrypt() {
        return encryptPassword;
    }

    public void setDeviceEncrypt(String deviceSerial, String encryptpassword) {
        if (TextUtils.isEmpty(encryptpassword)) {
            return;
        }
        encryptPassword = encryptpassword;
        EZDeviceDBManager.saveDevPwd(deviceSerial, encryptPassword);
    }

    public EZOpenCameraInfo getOpenCameraInfo() {
        if (mOpenCameraInfo == null || !mOpenCameraInfo.isValid()) {
            mOpenCameraInfo = EZCameraDBManager.findFirst(mDeviceSerial, mCameraNo);
            if (mOpenCameraInfo != null && mOpenCameraInfo.isValid()) {
                mOpenCameraInfo.addChangeListener(mCameraRealmChangeListener);
            }
        }
        return mOpenCameraInfo;
    }

    public EZOpenDeviceInfo getOpenDeviceInfo() {
        if (mOpenDeviceInfo == null || !mOpenDeviceInfo.isValid()) {
            mOpenDeviceInfo = EZDeviceDBManager.findFirst(mDeviceSerial);
            if (mOpenDeviceInfo != null && mOpenDeviceInfo.isValid()) {
                mOpenDeviceInfo.addChangeListener(mDeviceRealmChangeListener);
            }
        }
        return mOpenDeviceInfo;
    }

    /**
     * 设置清晰度
     */
    public void setQuality(final String deviceSerial, final int cameraNo, final int videolevel) {
        if (mOpenCameraInfo.getVideoLevel() == videolevel) {
            Log.d(TAG, "video level is same");
            return;
        }
        mPlayView.showLoadDialog();
        Observable observable = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean ret = EZOpenSDK.setVideoLevel(deviceSerial, cameraNo, videolevel);
                    subscriber.onNext(ret);
                } catch (BaseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                mPlayView.dismissLoadDialog();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                // TODO: 2017/1/17 设置清晰度失败
                mPlayView.dismissLoadDialog();
                mPlayView.showToast(R.string.string_set_quality_fail, ((BaseException) e).getErrorCode());
                onErrorBaseHandle(((BaseException) e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
            }

            @Override
            public void onNext(Boolean ret) {
                mPlayView.dismissLoadDialog();
                if (ret) {
                    EZCameraDBManager.setDeviceVideoLevel(deviceSerial, cameraNo, videolevel);
                    mPlayView.handleSetQualitSuccess();
                } else {
                    // TODO: 2017/1/17 设置清晰度失败
                    mPlayView.showToast(R.string.string_set_quality_fail);
                }
            }
        });
    }

    public void savePicture(final Context context, final EZPlayer ezPlayer) {

        if (ezPlayer == null) {
            return;
        }
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String path = null;
                    Bitmap bmp = ezPlayer.capturePicture();
                    if (bmp != null) {
                        EZOpenUtils.soundPool(context, R.raw.picture);
                        path = DataManager.getCaptureFile();
                        if (TextUtils.isEmpty(path)) {
                            bmp.recycle();
                            bmp = null;
                        } else {
                            boolean ret = EZOpenUtils.saveBitmapToFile(bmp, path, Bitmap.CompressFormat.JPEG);
                            if (ret) {
                                subscriber.onNext(path);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                onErrorBaseHandle(((BaseException) e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理

            }

            @Override
            public void onNext(String ret) {
                mPlayView.showToast(ret);
            }
        });
    }

    @Override
    public void release() {
        if (mOpenDeviceInfo != null) {
            mOpenDeviceInfo.removeChangeListener(mDeviceRealmChangeListener);
        }
        if (mOpenCameraInfo != null) {
            mOpenCameraInfo.removeChangeListener(mCameraRealmChangeListener);
        }
        super.release();
    }



    /**
     * 模糊图片的具体方法
     *
     * @param context 上下文对象
     * @return 模糊处理后的图片
     */
    public Bitmap blurBitmap(Context context,float blurRadius) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Bitmap bitmapDrawable = CameraCaptureCache.getInstance().getCoverCache(mDeviceSerial, mCameraNo);
            if (bitmapDrawable == null){
                return null;
            }
            // 计算图片缩小后的长宽
            int width = Math.round(bitmapDrawable.getWidth() * 0.4f);
            int height = Math.round(bitmapDrawable.getHeight() * 0.4f);

            // 将缩小后的图片做为预渲染的图片
            Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmapDrawable, width, height, false);
            // 创建一张渲染后的输出图片
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            // 创建RenderScript内核对象
            RenderScript rs = RenderScript.create(context);
            // 创建一个模糊效果的RenderScript的工具对象
            ScriptIntrinsicBlur blurScript = null;

            blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
            // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

            // 设置渲染的模糊程度, 25f是最大模糊度
            blurScript.setRadius(blurRadius);
            // 设置blurScript对象的输入内存
            blurScript.setInput(tmpIn);
            // 将输出数据保存到输出内存中
            blurScript.forEach(tmpOut);

            // 将数据填充到Allocation中
            tmpOut.copyTo(outputBitmap);
            return outputBitmap;
        }
        return null;
    }
}


