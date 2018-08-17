package com.ezviz.open.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.glide.EncryptUrlInfo;
import com.ezviz.open.utils.DateUtil;
import com.ezviz.open.utils.EZLog;
import com.ezviz.open.utils.EZOpenUtils;
import com.videogo.openapi.bean.EZAlarmInfo;
import com.ezviz.open.R;
/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/15
 */
public class MessageAdapter extends BaseLoadMoreRecyclerViewAdapter<EZAlarmInfo>{

    private onItemClickListener mOnItemClickListener;
    private DrawableRequestBuilder<EncryptUrlInfo> mGlideBuilder;
    public interface onItemClickListener{
        public void onClick(EZAlarmInfo alarmInfo);
    }

    public MessageAdapter(Context context) {
        super(context);
        mGlideBuilder = Glide.with(context)
                .from(EncryptUrlInfo.class)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESULT);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.message_item, parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        EZLog.d("MessageFragment","mList.get(position) time ="+ mList.get(position).getAlarmStartTime() + "  mlist size = "+ mList.size()+"  position = " + position + "holder postion = "+holder.getAdapterPosition());
//        ((MessageViewHolder)(holder)).mMessageTextView.setText(EZOpenConstant.AlarmType.getAlarmTypeById(mList.get(position).getAlarmType()).getTextResId());
        ((MessageViewHolder)(holder)).mMessageTextView.setText(mList.get(position).getAlarmName());
        ((MessageViewHolder)(holder)).mMessageDeviceType.setText(mList.get(position).getCategory()+"("+mList.get(position).getDeviceSerial()+")");
        ((MessageViewHolder)(holder)).mMessageTime.setText(DateUtil.getMessageDataTimeDisplay(mContext,mList.get(position).getAlarmStartTime())+"  "+mList.get(position).getAlarmStartTime());
        EZOpenConstant.AlarmType alarmType = EZOpenConstant.AlarmType.getAlarmTypeById(mList.get(position).getAlarmType());
        ((MessageViewHolder)(holder)).mImageMessageTypeView.setImageResource(alarmType.getDrawableResId());
        ((MessageViewHolder)(holder)).mTextViewMessageType.setText(alarmType.getTextResId());
//        ((MessageViewHolder)(holder)).mTextViewMessageType.setTextColor(alarmType.getColorId());
        ((MessageViewHolder)(holder)).mTextViewMessageType.setTextColor(mContext.getResources().getColor(alarmType.getColorId()));
        boolean isEncrypt = EZOpenUtils.isEncrypt(mList.get(position).getAlarmPicUrl());
        EncryptUrlInfo encryptURLInfo = new EncryptUrlInfo(mList.get(position).getDeviceSerial(),mList.get(position).getAlarmPicUrl(),isEncrypt);
        encryptURLInfo.setPosition(position);
        if (mList.get(position).getRecState() == 0){
            ((MessageViewHolder)(holder)).mVideoImg.setVisibility(View.GONE);
        }else{
            ((MessageViewHolder)(holder)).mVideoImg.setVisibility(View.VISIBLE);
        }
        Glide.clear(((MessageViewHolder)(holder)).mImageCoverView);
//        Glide.with(mContext).load(mList.get(position).getAlarmPicUrl()).placeholder(isEncrypt?R.drawable.alarm_encrypt_image_mid:R.drawable.device_other);
        mGlideBuilder.load(encryptURLInfo).placeholder(isEncrypt?R.drawable.alarm_encrypt_image_big:R.drawable.device_other).into(((MessageViewHolder)(holder)).mImageCoverView);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        ImageView mImageCoverView;
        TextView mMessageTextView;
        TextView mMessageTime;
        TextView mMessageDeviceType;
        ImageView mImageMessageTypeView;
        TextView mTextViewMessageType;
        private ImageView mVideoImg;
        public MessageViewHolder(View itemView) {
            super(itemView);
            mImageCoverView = (ImageView) itemView.findViewById(R.id.message_cover_img);
            mMessageTextView = (TextView) itemView.findViewById(R.id.message_text);
            mMessageTime = (TextView) itemView.findViewById(R.id.message_time_text);
            mMessageDeviceType = (TextView) itemView.findViewById(R.id.device_type_text);
            mImageMessageTypeView = (ImageView) itemView.findViewById(R.id.message_type_img);
            mTextViewMessageType = (TextView) itemView.findViewById(R.id.message_type_text);
            mVideoImg = (ImageView) itemView.findViewById(R.id.img_video);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onClick(mList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}


