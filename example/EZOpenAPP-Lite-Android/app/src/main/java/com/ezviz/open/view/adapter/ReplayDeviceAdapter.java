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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ezviz.open.model.EZOpenDeviceInfo;
import com.ezviz.open.R;
import io.realm.OrderedRealmCollection;


public class ReplayDeviceAdapter extends BaseRealmRecyclerViewAdapter<EZOpenDeviceInfo, ReplayDeviceAdapter.DeviceViewHolder> {

    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        public void onItemClick(String deviceserial);
    }

    public ReplayDeviceAdapter(Context context, OrderedRealmCollection<EZOpenDeviceInfo> data) {
        super(data, true);
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playback_device_list_item, parent, false);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        EZOpenDeviceInfo openDeviceInfo = getData().get(position);
        holder.mDeviceSerial.setText(openDeviceInfo.getDeviceSerial());
        Glide.with(mContext).load(openDeviceInfo.getDeviceCover()).placeholder(R.drawable.device_other).into(holder.mImgCover);
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder{
        public TextView mDeviceSerial;
        public ImageView mImgCover;

        public DeviceViewHolder(View view) {
            super(view);
            mDeviceSerial = (TextView) view.findViewById(R.id.device_serial);
            mImgCover = (ImageView) view.findViewById(R.id.device_cover_img);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(getData().get(getAdapterPosition()).getDeviceSerial());
                    }
                }
            });
        }
    }
}