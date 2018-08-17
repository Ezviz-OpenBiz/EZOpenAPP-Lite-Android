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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ezviz.open.model.EZOpenDeviceInfo;
import com.ezviz.open.utils.DataManager;
import com.ezviz.open.utils.DateUtil;
import com.ezviz.open.R;
import io.realm.OrderedRealmCollection;


public class DeviceAdapter extends BaseRealmRecyclerViewAdapter<EZOpenDeviceInfo, DeviceAdapter.DeviceViewHolder> {

    private Context mContext;

    private OnDeviceItemClickListener mOnDeviceItemClickListener;

    public interface OnDeviceItemClickListener {
        public void onItemClick(String deviceSerial, int position);
    }
    public DeviceAdapter(Context context, OrderedRealmCollection<EZOpenDeviceInfo> data) {
        super(data, true);
        this.mContext = context;
    }

    public void setOnDeviceItemClickListener(OnDeviceItemClickListener onDeviceItemClickListener) {
        mOnDeviceItemClickListener = onDeviceItemClickListener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        EZOpenDeviceInfo openDeviceInfo = getData().get(position);
        holder.mDeviceModeTextView.setText(TextUtils.isEmpty(openDeviceInfo.getCategory())?openDeviceInfo.getDeviceType():openDeviceInfo.getCategory());
        holder.mDeviceNameTextView.setText(openDeviceInfo.getDeviceName());
        holder.mDeviceTypeTextView.setText(String.format(mContext.getResources().getString(R.string.device_type),openDeviceInfo.getDeviceType()));
        holder.mDeviceSerialTextView.setText(String.format(mContext.getResources().getString(R.string.device_serial),openDeviceInfo.getDeviceSerial()));
        holder.mDeviceUseDateTextView.setText(String.format(mContext.getResources().getString(R.string.device_use_time), DateUtil.getDataDisplay(openDeviceInfo.getAddTime())));
//      holder.mDeviceStatus.setVisibility(openDeviceInfo.getStatus() == 1?View.GONE:View.VISIBLE);
        holder.mDeviceStatus.setVisibility(View.GONE);
        Glide.with(mContext).load(openDeviceInfo.getDeviceCover())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImgCover);
        holder.mMainView.setBackgroundResource(DataManager.getInstance().getBackgroundResource(openDeviceInfo.getPosition()));
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder{
        public TextView mDeviceModeTextView;
        public TextView mDeviceTypeTextView;
        public TextView mDeviceSerialTextView;
        public TextView mDeviceNameTextView;
        public TextView mDeviceUseDateTextView;
        public ImageView mImgCover;
        public TextView mDeviceStatus;
        public View mMainView;

        public DeviceViewHolder(View view) {
            super(view);
            mDeviceModeTextView = (TextView) view.findViewById(R.id.device_mode);
            mDeviceTypeTextView = (TextView) view.findViewById(R.id.device_type);
            mDeviceNameTextView = (TextView) view.findViewById(R.id.device_name);
            mDeviceSerialTextView = (TextView) view.findViewById(R.id.device_serial);
            mDeviceUseDateTextView = (TextView) view.findViewById(R.id.device_use_time);
            mDeviceModeTextView = (TextView) view.findViewById(R.id.device_mode);
            mImgCover = (ImageView) view.findViewById(R.id.device_cover_img);
            mDeviceStatus = (TextView) view.findViewById(R.id.device_status);
            mMainView = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnDeviceItemClickListener != null){
                        mOnDeviceItemClickListener.onItemClick(getItem(getAdapterPosition()).getDeviceSerial(),getAdapterPosition());
                    }
                }
            });
        }
    }
}