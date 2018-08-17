package com.ezviz.open.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ezviz.open.utils.DataManager;
import com.ezviz.open.utils.EZDeviceDBManager;
import com.ezviz.open.utils.EZOpenUtils;
import com.ezviz.open.view.PlayBackView;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCloudRecordFile;
import com.videogo.openapi.bean.EZDeviceRecordFile;

import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import com.ezviz.open.R;
/**
 * Description: 回放
 * Created by dingwei3
 *
 * @date : 2016/12/26
 */
public class PlayBackPresenter extends BaseRealmPresenter {

    private static final String TAG = "PlayBackPresenter";

    private String encryptPassword;
    private PlayBackView mPlayBackView;


    public PlayBackPresenter(PlayBackView playView) {
        super();
        this.mPlayBackView = playView;
    }

    public void prepareInfo(String deviceSerial, int cameraNo) {
        encryptPassword = EZDeviceDBManager.getDevPwd(deviceSerial);
        mPlayBackView.handlePrepareInfo();
    }

    public void preparePlayBackInfo(String deviceSerial) {
        encryptPassword = EZDeviceDBManager.getDevPwd(deviceSerial);
        mPlayBackView.handlePrepareInfo();
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


    /**
     * 查询远程SD卡存储录像信息列表
     *
     * @param deviceSerial 设备序列号
     * @param cameraNo     通道号
     * @param startTime    查询时间范围开始时间
     * @param endTime      查询时间范围结束时间
     */
    public void searchRecordFileFromDevice(final String deviceSerial, final int cameraNo, final Calendar startTime, final Calendar endTime) {
        mPlayBackView.showLoadDialog();
        Observable observable = Observable.create(new Observable.OnSubscribe<List<EZDeviceRecordFile>>() {
            @Override
            public void call(Subscriber<? super List<EZDeviceRecordFile>> subscriber) {
                try {
                    List<EZDeviceRecordFile> list = EZOpenSDK.searchRecordFileFromDevice(deviceSerial, cameraNo, startTime, endTime);
                    subscriber.onNext(list);
                } catch (BaseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Subscriber<List<EZDeviceRecordFile>>() {
            @Override
            public void onCompleted() {
                mPlayBackView.dismissLoadDialog();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                // TODO: 2017/2/23  查询远程SD卡存储录像信息列表失败
                mPlayBackView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException) e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
                mPlayBackView.handleSearchFileFail();
            }

            @Override
            public void onNext(List<EZDeviceRecordFile> ret) {
                mPlayBackView.dismissLoadDialog();
                if (ret != null && ret.size() > 0) {
                    mPlayBackView.handleSearchFileFormDeviceSuccess(ret.get(0));
                } else {
                    // TODO: 2017/1/17 查询远程SD卡存储录像信息列表为空
                    mPlayBackView.handleSearchFileFail();
                }
            }
        });
    }


    /**
     * 查询云存储录像信息列表
     *
     * @param deviceSerial 设备序列号
     * @param cameraNo     camera的序号，EZCameraInfo.cameraNo
     * @param startTime    查询时间范围开始时间
     * @param endTime      查询时间范围结束时间
     * @return 云存储录像信息列表
     * @throws BaseException
     */
    public void searchRecordFileFromCloud(final String deviceSerial, final int cameraNo, final Calendar startTime, final Calendar endTime) {
        mPlayBackView.showLoadDialog();
        Observable observable = Observable.create(new Observable.OnSubscribe<List<EZCloudRecordFile>>() {
            @Override
            public void call(Subscriber<? super List<EZCloudRecordFile>> subscriber) {
                try {
                    List<EZCloudRecordFile> list = EZOpenSDK.searchRecordFileFromCloud(deviceSerial, cameraNo, startTime, endTime);
                    subscriber.onNext(list);
                } catch (BaseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
        subscribeAsync(observable, new Subscriber<List<EZCloudRecordFile>>() {
            @Override
            public void onCompleted() {
                mPlayBackView.dismissLoadDialog();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                // TODO: 2017/2/23  查询云存储录像信息列表失败
                mPlayBackView.dismissLoadDialog();
                onErrorBaseHandle(((BaseException) e).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
                mPlayBackView.handleSearchFileFail();
            }

            @Override
            public void onNext(List<EZCloudRecordFile> ret) {
                mPlayBackView.dismissLoadDialog();
                if (ret != null && ret.size() > 0) {
                    mPlayBackView.handleSearchFileFromCloudSuccess(ret.get(0));
                } else {
                    // TODO: 2017/1/17 查询云存储录像信息列表为空
                    mPlayBackView.handleSearchFileFail();
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
                mPlayBackView.showToast(ret);
            }
        });
    }

    @Override
    public void release() {
        super.release();
    }

    /**
     * 模糊图片的具体方法
     *
     * @param context 上下文对象
     * @return 模糊处理后的图片
     */
    public void blurBitmap(final Context context, final ImageView imageView, final String url, final float blurRadius) {

        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {

                    // 计算图片缩小后的长宽
                    int width = Math.round(resource.getWidth() * 0.4f);
                    int height = Math.round(resource.getHeight() * 0.4f);

                    // 将缩小后的图片做为预渲染的图片
                    Bitmap inputBitmap = Bitmap.createScaledBitmap(resource, width, height, false);
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
                    if (outputBitmap == null && imageView == null) {
                        return;
                    }
                    imageView.setImageBitmap(outputBitmap);

                }
            }
        });
    }
}


