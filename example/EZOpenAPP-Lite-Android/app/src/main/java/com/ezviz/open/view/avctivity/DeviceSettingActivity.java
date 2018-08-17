package com.ezviz.open.view.avctivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.presenter.DeviceSettingPresenter;
import com.ezviz.open.utils.DataManager;
import com.ezviz.open.utils.DateUtil;
import com.ezviz.open.view.DeviceSettingView;
import com.ezviz.open.view.adapter.DeviceSettingAdapter;
import com.ezviz.open.view.widget.Topbar;
import com.ezviz.open.R;
/**
 * Description:设备设置
 * Created by dingwei3
 *
 * @date : 2016/12/21
 */
public class DeviceSettingActivity extends RootActivity implements DeviceSettingView, DeviceSettingAdapter.onDeviceSttingItemClickListener {

    private DeviceSettingPresenter mDeviceSettingPresenter;
    private int position;
    private String mDeviceSerial;
    private Topbar mTopbar;

    public TextView mDeviceModeTextView;
    public TextView mDeviceSerialTextView;
    public TextView mDeviceUseDateTextView;
    public ImageView mImgCover;
    private RecyclerView mRecyclerView;
    private DeviceSettingAdapter mDeviceSettingAdapter;

    /**
     * 开启设备设置界面
     * @param context
     * @param deviceSerial  设备序列号
     */
    public static void startDeviceSetting(Context context,String deviceSerial){
        Intent intent = new Intent(context,DeviceSettingActivity.class);
        intent.putExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL,deviceSerial);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting_layout);
        mDeviceSettingPresenter = new DeviceSettingPresenter(this,this);
        initDate();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        mDeviceSerial = getIntent().getStringExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL);
        mDeviceSettingAdapter = new DeviceSettingAdapter(DeviceSettingActivity.this);
    }

    /**
     * 初始化UI
     */
    private void initView() {
        mTopbar = (Topbar) findViewById(R.id.device_setting_topbar);
        mTopbar.setTitle(R.string.device_setting);
        mTopbar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
            @Override
            public void onLeftButtonClicked() {
                onBackPressed();
            }
            @Override
            public void onRightButtonClicked() {

            }
        });
        mDeviceModeTextView = (TextView) findViewById(R.id.device_mode);
        mDeviceSerialTextView = (TextView) findViewById(R.id.device_serial);
        mDeviceUseDateTextView = (TextView) findViewById(R.id.device_use_time);
        mImgCover = (ImageView) findViewById(R.id.device_cover_img);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_device_setting);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mDeviceSettingAdapter);
    }



    private void refreshUI(){
        findViewById(R.id.device_info_layout).setBackgroundResource(DataManager.getInstance().getBackgroundResource(mDeviceSettingPresenter.getOpenDeviceInfo().getPosition()));
        mDeviceModeTextView.setText(mDeviceSettingPresenter.getOpenDeviceInfo().getCategory());
        mDeviceSerialTextView.setText(String.format(getResources().getString(R.string.device_serial),mDeviceSettingPresenter.getOpenDeviceInfo().getDeviceSerial()));
        mDeviceUseDateTextView.setText(String.format(getResources().getString(R.string.device_use_time),DateUtil.getDataDisplay(mDeviceSettingPresenter.getOpenDeviceInfo().getAddTime())));
        Glide.with(this).load(mDeviceSettingPresenter.getOpenDeviceInfo().getDeviceCover())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImgCover);
        mDeviceSettingAdapter.setList(mDeviceSettingPresenter.getDeviceSettingItems());
        mDeviceSettingAdapter.setOnDeviceSttingItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDeviceSettingPresenter.prepareOpenDeviceInfo(mDeviceSerial);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceSettingPresenter.release();
    }

    @Override
    public void handleEZOpenDeviceInfo() {
        refeshList();
    }

    @Override
    public void refeshList() {
        mDeviceSettingAdapter.setList(mDeviceSettingPresenter.getDeviceSettingItems());
    }

    @Override
    public void handlePrepareInfo() {
        refreshUI();
    }

    @Override
    public void handleSetQualitSuccess() {

    }

    @Override
    public void handleSetQualityFail() {

    }

    @Override
    public void handleCaptureSuccess(String path) {

    }

    @Override
    public void onDeviceSettingItemClick(int titleResId) {
        mDeviceSettingPresenter.onDeviceSettingItemClick(titleResId);
    }

    @Override
    public void onDeviceSettingSwitchItemClick(int titleResId) {
        mDeviceSettingPresenter.onDeviceSettingSwitchItemClick(titleResId);
    }

    @Override
    public void handleDeleteDeviceSuccess() {
        Intent intent = new Intent(DeviceSettingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
