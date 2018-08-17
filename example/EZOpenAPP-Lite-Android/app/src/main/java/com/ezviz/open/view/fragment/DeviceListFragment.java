package com.ezviz.open.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ezviz.open.presenter.DevicePresenter;
import com.ezviz.open.view.DeviceView;
import com.ezviz.open.view.adapter.DeviceAdapter;
import com.ezviz.open.view.avctivity.DeviceSettingActivity;
import com.ezviz.open.view.widget.PullRefreshRealmRecyclerView;
import com.ezviz.open.R;
/**
 * Description:设备列表
 * Created by dingwei3
 *
 * @date : 2016/12/12
 */
public class DeviceListFragment extends BaseLazyFragment implements DeviceView, PullRefreshRealmRecyclerView.OnAutoRefreshingListner, DeviceAdapter.OnDeviceItemClickListener {

    private final static String TAG = "DeviceListFragment";
    private DeviceAdapter mDeviceAdapter;
    private DevicePresenter mDevicePresenter;
    private PullRefreshRealmRecyclerView mPullRefreshRecyclerView;
    public DeviceListFragment(){
        mDevicePresenter = new DevicePresenter(this);
        Log.d(TAG,mDevicePresenter.getRealm().toString());
    }
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devicelist_layout,null);
        mPullRefreshRecyclerView = (PullRefreshRealmRecyclerView) view.findViewById(R.id.message_list_layout);
        mDeviceAdapter = new DeviceAdapter(mContext,mDevicePresenter.getEZOpenDeviceInfoList());
        mDeviceAdapter.setOnDeviceItemClickListener(this);
        mPullRefreshRecyclerView.setAdapter(mDeviceAdapter);
        mPullRefreshRecyclerView.setOnAutoRefreshingListner(this);
        return view;
    }

    @Override
    protected void lazyLoad() {

    }


    @Override
    public void loadFinish() {
        mPullRefreshRecyclerView.onRefreshComplete();
    }

    @Override
    public void refreshFinish() {
        mPullRefreshRecyclerView.onRefreshComplete();
    }

    @Override
    public void onAutoRefreshing() {
        mDevicePresenter.loadDeviceList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDevicePresenter.release();
    }

    @Override
    public void onItemClick(String deviceSerial, int position) {
        // TODO: 2017/1/10 进入设备设置
        DeviceSettingActivity.startDeviceSetting(mContext,deviceSerial);
    }

}


