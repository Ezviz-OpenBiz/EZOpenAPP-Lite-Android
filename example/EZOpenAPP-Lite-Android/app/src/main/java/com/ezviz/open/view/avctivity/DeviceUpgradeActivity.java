package com.ezviz.open.view.avctivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ezviz.open.R;

import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.presenter.DeviceUpgradePresenter;

public class DeviceUpgradeActivity extends RootActivity {
    private DeviceUpgradePresenter mDeviceSettingPresenter;

    public static void startDeviceUpgradeActivity(Context context,String deviceVersionDes){
        Intent intent = new Intent(context,DeviceUpgradeActivity.class);
        intent.putExtra(EZOpenConstant.EXTRA_DEVICE_VERSION_DES,deviceVersionDes);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_upgrade);
        mDeviceSettingPresenter = new DeviceUpgradePresenter();
        initDate();
        initView();
    }

    private void initView() {

    }

    private void initDate() {

    }

}
