/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ezviz.open.view.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.model.EZOpenCameraInfo;
import com.ezviz.open.utils.AsyncBitmapTaskTag;
import com.ezviz.open.utils.BitmapWorkManager;
import com.ezviz.open.utils.BitmapWorkerTask;
import com.ezviz.open.utils.CameraCaptureCache;
import com.ezviz.open.utils.DataManager;
import com.ezviz.open.utils.EZLog;
import com.ezviz.open.view.avctivity.RootActivity;
import com.videogo.openapi.EZConstants;
import io.realm.OrderedRealmCollection;
import java.io.File;
import com.ezviz.open.R;
public class CameraAdapter extends BaseRealmRecyclerViewAdapter<EZOpenCameraInfo, CameraAdapter.CameraViewHolder> {

    private RootActivity mACtivity;

    private OnCameraItemClickListener mOnItemClickListener;

    public interface OnCameraItemClickListener {
        public void onItemClick(String deviceSerial, int cameraNo);

        public void onItemSetting(int position);
    }

    public void captureCover(int position){
        CameraCaptureCache.getInstance().addCoverRefresh(getData().get(position).getDeviceSerial(),getData().get(position).getCameraNo(),false);
        notifyItemChanged(position);
    }
    public void setOnItemClickListener(OnCameraItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public CameraAdapter(RootActivity aCtivity, OrderedRealmCollection<EZOpenCameraInfo> data) {
        super(data, true);
        this.mACtivity = aCtivity;
    }

    @Override
    public CameraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_list_item, parent, false);
        return new CameraViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CameraViewHolder holder, int position) {
        EZLog.d("onBindViewHolder","postion = "+ position);
        EZOpenCameraInfo openCameraInfo = getData().get(position);
        holder.title.setText(openCameraInfo.getCameraName());
        holder.mCameraTypeTv.setText(TextUtils.isEmpty(openCameraInfo.getCategory()) ? openCameraInfo.getDeviceType() : openCameraInfo.getCategory() + "(" + openCameraInfo.getDeviceSerial() + ")");
        holder.mLoading.setVisibility(View.GONE);
        holder.mImgCover.setImageResource(R.drawable.images_cache_bg2);
        holder.mImgCover.setTag(R.id.tag_key_capture_bitmap, CameraCaptureCache.getInstance().getKey(openCameraInfo.getDeviceSerial(), openCameraInfo.getCameraNo()));
        captureCamera(holder, openCameraInfo);
        if (openCameraInfo.getStatus() == EZOpenConstant.DEVICE_ONLINE) {
            holder.mOffLineLayout.setVisibility(View.GONE);
            if (openCameraInfo.isSupportDefence()) {
                holder.mDefenceTextView.setVisibility(View.VISIBLE);
                if (openCameraInfo.getDefence() == EZConstants.EZDefenceStatus.EZDefence_IPC_CLOSE.getStatus()) {
                    holder.mDefenceTextView.setEnabled(false);
                } else {
                    holder.mDefenceTextView.setEnabled(true);
                }
            } else {
                holder.mDefenceTextView.setVisibility(View.GONE);
            }
        } else {
            holder.mOffLineLayout.setVisibility(View.VISIBLE);
        }
//      EZGlideUtils.loadImage(mContext,holder.mImgCover,obj.getCameraCover());
    }

    private void captureCamera(CameraViewHolder holder, EZOpenCameraInfo cameraInfo) {
        Bitmap bitmapDrawable = CameraCaptureCache.getInstance().getCoverCache(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo());
        if (bitmapDrawable != null) {
            holder.mImgCover.setImageBitmap(bitmapDrawable);
        } else {
            File file = new File(DataManager.getInstance().getCameraCoverCaptureImgFilePath(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo()));
            if (file.exists() && file.isFile()) {
                BitmapWorkerTask task = new BitmapWorkerTask(mACtivity, holder, cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), BitmapWorkerTask.TYPE_LOAD_LOCAL_FILE);
                task.execute();
            }
        }
        if (cameraInfo.getStatus() == 1) {
            if (CameraCaptureCache.getInstance().mCaptureRefeshMap.containsKey(CameraCaptureCache.getInstance().getKey(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo()))){
                Boolean b = CameraCaptureCache.getInstance().mCaptureRefeshMap.get(CameraCaptureCache.getInstance().getKey(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo()));
                if (b){
                    BitmapWorkManager.cancelPotentialWork(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), holder.mImgCover);
                    BitmapWorkerTask task = new BitmapWorkerTask(mACtivity, holder, cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), BitmapWorkerTask.TYPE_INIT);
                    AsyncBitmapTaskTag asyncBitmapTaskTag = new AsyncBitmapTaskTag(task);
                    holder.mImgCover.setTag(R.id.tag_key_capture_task, asyncBitmapTaskTag);
                    task.execute();
                    holder.mLoading.setVisibility(View.GONE);
                }else{
                    BitmapWorkManager.cancelPotentialWork(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), holder.mImgCover);
                    holder.mLoading.setVisibility(View.VISIBLE);
                    BitmapWorkerTask task = new BitmapWorkerTask(mACtivity, holder, cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), BitmapWorkerTask.TYPE_NORMAL);
                    AsyncBitmapTaskTag asyncBitmapTaskTag = new AsyncBitmapTaskTag(task);
                    holder.mImgCover.setTag(R.id.tag_key_capture_task, asyncBitmapTaskTag);
                    task.execute();
                }
            }else{

            }
        }



//
//        if (bitmapDrawable != null) {
//            holder.mImgCover.setImageBitmap(bitmapDrawable);
//        } else {
//            File file = new File(DataManager.getInstance().getCameraCoverCaptureImgFilePath(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo()));
//            if (file.exists() && file.isFile()) {
//                BitmapWorkerTask task = new BitmapWorkerTask(mContext, holder, cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), true);
//                task.execute();
//            } else if (b == null || !b.booleanValue()) {
//                if (cameraInfo.getStatus() == 1) {
//                    EZCameraDB.setNeedCaptureCover(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), false);
//                    BitmapWorkManager.cancelPotentialWork(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), holder.mImgCover);
//                    BitmapWorkerTask task = new BitmapWorkerTask(mContext, holder, cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), false);
//                    AsyncBitmapTaskTag asyncBitmapTaskTag = new AsyncBitmapTaskTag(task);
//                    holder.mImgCover.setTag(R.id.tag_key_capture_task, asyncBitmapTaskTag);
//                    task.execute();
//                }
//            }
//        }
//        if (cameraInfo.getStatus() == 1) {
//            if (b != null && b.booleanValue()) {
//                EZCameraDB.setNeedCaptureCover(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), false);
//                BitmapWorkManager.cancelPotentialWork(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), holder.mImgCover);
//                holder.mLoading.setVisibility(View.VISIBLE);
//                BitmapWorkerTask task = new BitmapWorkerTask(mContext, holder, cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), false);
//                AsyncBitmapTaskTag asyncBitmapTaskTag = new AsyncBitmapTaskTag(task);
//                holder.mImgCover.setTag(R.id.tag_key_capture_task, asyncBitmapTaskTag);
//                task.execute();
//            } else if (cameraInfo.isNeedCaptureCover()) {
//                EZCameraDB.setNeedCaptureCover(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), false);
//                BitmapWorkManager.cancelPotentialWork(cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), holder.mImgCover);
//                BitmapWorkerTask task = new BitmapWorkerTask(mContext, holder, cameraInfo.getDeviceSerial(), cameraInfo.getCameraNo(), false);
//                AsyncBitmapTaskTag asyncBitmapTaskTag = new AsyncBitmapTaskTag(task);
//                holder.mImgCover.setTag(R.id.tag_key_capture_task, asyncBitmapTaskTag);
//                task.execute();
//                holder.mLoading.setVisibility(View.GONE);
//            }
//        }
    }

    public class CameraViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView mImgCover;
        public TextView mCameraTypeTv;
        public ImageView mCameraSetting;
        public RelativeLayout mOffLineLayout;
        public TextView mLoading;
        public TextView mDefenceTextView;

        public CameraViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.camera_title);
            mImgCover = (ImageView) view.findViewById(R.id.camera_cover_img);
            mCameraTypeTv = (TextView) view.findViewById(R.id.camera_type);
            mCameraSetting = (ImageView) view.findViewById(R.id.camera_setting);
            mOffLineLayout = (RelativeLayout) view.findViewById(R.id.offline_layout);
            mLoading = (TextView) view.findViewById(R.id.text_loading);
            mDefenceTextView = (TextView) view.findViewById(R.id.camera_img_defense);
            mOffLineLayout.setClickable(true);
            mImgCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        EZOpenCameraInfo openCameraInfo = getData().get(getAdapterPosition());
                        mOnItemClickListener.onItemClick(openCameraInfo.getDeviceSerial(), openCameraInfo.getCameraNo());
                    }
                }
            });
            mCameraSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        EZOpenCameraInfo openCameraInfo = getData().get(getAdapterPosition());
                        mOnItemClickListener.onItemSetting(getAdapterPosition());
                    }
                }
            });
        }
    }
}