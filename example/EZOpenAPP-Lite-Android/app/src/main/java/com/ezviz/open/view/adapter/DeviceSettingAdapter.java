package com.ezviz.open.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezviz.open.presenter.DeviceSettingPresenter;
import com.ezviz.open.R;
/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2017/1/11
 */
public class DeviceSettingAdapter extends BaseRecyclerViewAdapter<DeviceSettingPresenter.DeviceSettingItem, DeviceSettingAdapter.DeviceSettingViewHolder> {

    private onDeviceSttingItemClickListener mOnDeviceSttingItemClickListener;
    public DeviceSettingAdapter(Context context) {
        super(context);
    }

    public void setOnDeviceSttingItemClickListener(onDeviceSttingItemClickListener onDeviceSttingItemClickListener) {
        mOnDeviceSttingItemClickListener = onDeviceSttingItemClickListener;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @Override
    public DeviceSettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == DeviceSettingPresenter.DeviceSettingItem.TYPE_DIVIDER) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.device_setting_item_divider, null);
            return new DeviceSettingViewHolder(view, true);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.device_setting_item, null);
            return new DeviceSettingViewHolder(view, false);
        }
    }


    @Override
    public void onBindViewHolder(DeviceSettingViewHolder holder, int position) {
        DeviceSettingPresenter.DeviceSettingItem deviceSettingItem = mList.get(position);
        if (getItemViewType(position) == DeviceSettingPresenter.DeviceSettingItem.TYPE_DIVIDER) {

        } else if (getItemViewType(position) == DeviceSettingPresenter.DeviceSettingItem.TYPE_DELETE) {
            holder.mNextImageView.setVisibility(View.GONE);
            holder.mTitleTextView.setVisibility(View.GONE);
            holder.mSwitch.setVisibility(View.GONE);
            holder.mValueTextView.setVisibility(View.GONE);
            holder.mDeleteTextView.setVisibility(View.VISIBLE);
            holder.mDeleteTextView.setText(deviceSettingItem.getTitleResId());
            holder.mListDivider.setVisibility(deviceSettingItem.isDisplayDivider() ? View.VISIBLE : View.GONE);
        } else {
            holder.mListDivider.setVisibility(deviceSettingItem.isDisplayDivider() ? View.VISIBLE : View.GONE);
            holder.mDeleteTextView.setVisibility(View.GONE);
            holder.mTitleTextView.setVisibility(View.VISIBLE);
            holder.mTitleTextView.setText(deviceSettingItem.getTitleResId());
            if (getItemViewType(position) == DeviceSettingPresenter.DeviceSettingItem.TYPE_NEXT) {
                holder.mNextImageView.setVisibility(View.VISIBLE);
                holder.mValueTextView.setVisibility(View.VISIBLE);
                holder.mSwitch.setVisibility(View.GONE);
                if (deviceSettingItem.getValue() != null && deviceSettingItem.getValue() instanceof String) {
                    holder.mValueTextView.setText((String) deviceSettingItem.getValue());
                }
            } else if (getItemViewType(position) == DeviceSettingPresenter.DeviceSettingItem.TYPE_SWITCH) {
                holder.mNextImageView.setVisibility(View.GONE);
                holder.mValueTextView.setVisibility(View.GONE);
                holder.mSwitch.setVisibility(View.VISIBLE);
                if (deviceSettingItem.getValue() != null && deviceSettingItem.getValue() instanceof Boolean) {
                    if ((Boolean) (deviceSettingItem.getValue())){
                        holder.mSwitch.setImageResource(R.drawable.switch_on);
                    }else{
                        holder.mSwitch.setImageResource(R.drawable.switch_off);
                    }
                }
            }else if (getItemViewType(position) == DeviceSettingPresenter.DeviceSettingItem.TYPE_TEXT) {
                holder.mNextImageView.setVisibility(View.GONE);
                holder.mValueTextView.setVisibility(View.VISIBLE);
                holder.mSwitch.setVisibility(View.GONE);
                holder.mValueTextView.setText((String) deviceSettingItem.getValue());
            }
        }
    }

    class DeviceSettingViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mValueTextView;
        private ImageView mSwitch;
        private ImageView mNextImageView;
        private TextView mDeleteTextView;
        private View mListDivider;

        public DeviceSettingViewHolder(View itemView, boolean isDivider) {
            super(itemView);
            if (!isDivider) {
                mTitleTextView = (TextView) itemView.findViewById(R.id.device_setting_item_title);
                mValueTextView = (TextView) itemView.findViewById(R.id.device_setting_item_value);
                mSwitch = (ImageView) itemView.findViewById(R.id.device_setting_item_switch);
                mNextImageView = (ImageView) itemView.findViewById(R.id.device_setting_item_next);
                mDeleteTextView = (TextView) itemView.findViewById(R.id.device_setting_item_delete);
                mListDivider = itemView.findViewById(R.id.list_divider);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnDeviceSttingItemClickListener != null){
                            if (mList.get(getAdapterPosition()) != null&& mList.get(getAdapterPosition()).getTitleResId() != 0)
                            mOnDeviceSttingItemClickListener.onDeviceSettingItemClick(mList.get(getAdapterPosition()).getTitleResId());
                        }
                    }
                });
                mSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnDeviceSttingItemClickListener != null){
                            if (mList.get(getAdapterPosition()) != null&& mList.get(getAdapterPosition()).getTitleResId() != 0)
                                mOnDeviceSttingItemClickListener.onDeviceSettingSwitchItemClick(mList.get(getAdapterPosition()).getTitleResId());
                        }
                    }
                });
            }
        }
    }

    public interface onDeviceSttingItemClickListener{
        public void  onDeviceSettingItemClick(int titleResId);
        public void onDeviceSettingSwitchItemClick(int titleResId);
    }
}


