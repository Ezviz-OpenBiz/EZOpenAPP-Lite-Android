package com.ezviz.open.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import com.bumptech.glide.Glide;
import com.ezviz.open.view.adapter.CameraAdapter.CameraViewHolder;
import com.ezviz.open.view.avctivity.RootActivity;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import java.util.concurrent.ExecutionException;
import com.ezviz.open.R;
/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/29
 */
public /**
 * 异步下载图片的任务。
 *
 * @author guolin
 */
class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "BitmapWorkerTask";
    private RootActivity mActivity;
    public String deviceSerial;
    public int cameraNo;
    private CameraViewHolder mViewHolder;
    private int mType = 0;
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_LOAD_LOCAL_FILE = 1;
    public final static int TYPE_INIT = 2;

    public BitmapWorkerTask(RootActivity activity, CameraViewHolder holder, String deviceSerial, int cameraNo, int type) {
        mViewHolder = holder;
        this.mActivity = activity;
        this.deviceSerial = deviceSerial;
        this.cameraNo = cameraNo;
        this.mType = type;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        String url = null;
        if (mActivity.isStop){
            onCancelled();
            return null;
        }
        if (mType != TYPE_LOAD_LOCAL_FILE) {
            try {
                url = EZOpenSDK.captureCamera(deviceSerial,cameraNo);
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }else{
            url = DataManager.getInstance().getCameraCoverCaptureImgFilePath(deviceSerial,                                                      cameraNo);
        }
        if (TextUtils.isEmpty(url)){
            return null;
        }
        Bitmap myBitmap = null;
        try {
            myBitmap = Glide.with(mActivity)
            .load(url)
            .asBitmap()
            .into(640,360)
            .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // 下载好后保存到缓存中
        CameraCaptureCache.getInstance().cacheCoverBitmap(DataManager.getInstance().getCameraCoverCaptureImgFilePath(deviceSerial,cameraNo), myBitmap);
        if (mType != TYPE_LOAD_LOCAL_FILE) {
            EZOpenUtils.saveBitmapToFile(myBitmap, DataManager.getInstance().getCameraCoverCaptureImgFilePath(deviceSerial, cameraNo), Bitmap.CompressFormat.PNG);
        }
        return myBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        CameraCaptureCache.getInstance().deleteCoverRefresh(deviceSerial,cameraNo);
//        if (mType == TYPE_INIT) {
//            EZCameraDB.setNeedCaptureCover(deviceSerial, cameraNo, false);
//        }
        if (mViewHolder.mLoading != null && mViewHolder.mImgCover != null && String.valueOf(mViewHolder.mImgCover.getTag(R.id.tag_key_capture_bitmap)).equals(CameraCaptureCache.getInstance().getKey(deviceSerial,cameraNo))){
            mViewHolder.mLoading.setVisibility(View.GONE);
        }
        if (bitmap == null){
            return;
        }
        if (mViewHolder.mImgCover != null && String.valueOf(mViewHolder.mImgCover.getTag(R.id.tag_key_capture_bitmap)).equals(CameraCaptureCache.getInstance().getKey(deviceSerial,cameraNo))){
            mViewHolder.mImgCover.setImageBitmap(bitmap);
        }
    }
}
