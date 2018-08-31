package com.ezviz.open.view.avctivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ezviz.open.R;
import com.ezviz.open.view.widget.Topbar;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;


public class AddDeviceActivity extends RootActivity {
    private static final String TAG = "DeviceStartAddActivity";
    private Topbar mTopBar;
    private String mDeviceSerial;
    private String mVerifyCode;
    private TextView mDeviceSerialTv;
    private TextView mAddTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        mDeviceSerial = getIntent().getStringExtra("SerialNo");
        mVerifyCode = getIntent().getStringExtra("very_code");
        mDeviceSerialTv = (TextView) findViewById(R.id.string_deviceid);
        mAddTv = (TextView) findViewById(R.id.add_device);
        mDeviceSerialTv.setText(mDeviceSerial);
        mTopBar = (Topbar) findViewById(R.id.topbar);
        mTopBar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
            @Override
            public void onLeftButtonClicked() {
                finish();
            }

            @Override
            public void onRightButtonClicked() {

            }
        });
        mAddTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EZOpenSDK.addDevice(mDeviceSerial, mVerifyCode);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissLoadDialog();
                                    Intent intent = new Intent(AddDeviceActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } catch (BaseException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Request failed = " + e.getErrorCode());
                            showToast("Request failed = " + e.getErrorCode());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissLoadDialog();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
