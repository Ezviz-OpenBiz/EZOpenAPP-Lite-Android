package com.ezviz.open.view.avctivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ezviz.open.view.widget.Topbar;
import com.ezviz.opensdk.wificonfig.APWifiConfig;
import com.ezviz.opensdk.wificonfig.EZWiFiConfigMode;
import com.ezviz.opensdk.wificonfig.EZWifiConfigStatus;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;

import static com.ezviz.open.view.avctivity.SearchDeviceActivity.CONFIG_TYPE_AP;
import static com.ezviz.open.view.avctivity.SearchDeviceActivity.CONFIG_TYPE_SMARTCONFIG;
import static com.ezviz.open.view.avctivity.SearchDeviceActivity.CONFIG_TYPE_SMART_SOUNDWAVE;
import static com.ezviz.open.view.avctivity.SearchDeviceActivity.CONFIG_TYPE_SOUNDWAVE;
import com.ezviz.open.R;
import com.videogo.openapi.EZWifiConfigManager;

public class SmartConfigActivity extends RootActivity {
    private static final String TAG = "SmartConfigActivity";
    private String mDeviceSerial;
    private String mSSID;
    private String mPassword;
    private String mVerifyCode;

    private int mType = -1;

    private ProgressBar mProgressBar;
    private TextView mResetTv;
    private TextView mAddTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_config);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mResetTv = (TextView) findViewById(R.id.reset);
        mAddTv = (TextView) findViewById(R.id.add_device);
        Topbar mTopBar = (Topbar) findViewById(R.id.topbar);
        mTopBar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
            @Override
            public void onLeftButtonClicked() {
                finish();
            }

            @Override
            public void onRightButtonClicked() {

            }
        });
        EZWifiConfigManager.setTimeOut(60);
        mSSID = getIntent().getStringExtra("SSID");
        mPassword = getIntent().getStringExtra("password");
        mDeviceSerial = getIntent().getStringExtra("SerialNo");
        mVerifyCode = getIntent().getStringExtra("very_code");
        mType = getIntent().getIntExtra(SearchDeviceActivity.CONFIG_TYPE_KEY, -1);

        startConfig();
        mResetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConfig();
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
                                    Intent intent = new Intent(SmartConfigActivity.this, MainActivity.class);
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

    private void startConfig() {
        showconfig();
        switch (mType) {
            case CONFIG_TYPE_SMARTCONFIG:
                // TODO: 2018/6/26 smart config wifi
                startModeConfigWifi(EZWiFiConfigMode.EZWiFiConfigSmart);
                break;
            case CONFIG_TYPE_SOUNDWAVE:
                // TODO: 2018/6/26 声波配网
                startModeConfigWifi(EZWiFiConfigMode.EZWiFiConfigWave);
                break;
            case CONFIG_TYPE_AP:
                // TODO: 2018/6/26 ap模式配网
                startApConfig();
                break;
            case CONFIG_TYPE_SMART_SOUNDWAVE:
                // TODO: 2018/6/26 声波配网+普通配网同时进行
                startModeConfigWifi(EZWiFiConfigMode.EZWiFiConfigSmart | EZWiFiConfigMode.EZWiFiConfigWave);
                break;
            default:
                break;
        }
    }

    private void startModeConfigWifi(int mode) {
        EZWifiConfigManager.startConfigWifi(mDeviceSerial, mSSID, mPassword, mode,
            new EZWifiConfigManager.EZStartConfigWifiCallback() {
                @Override
                public void onStartConfigWifiCallback(String deviceSerial, EZWifiConfigStatus status) {
                    if (status == EZWifiConfigStatus.DEVICE_WIFI_CONNECTING) {
                        // TODO: 2018/6/27 设备正在连接中
                        Log.d(TAG,
                            deviceSerial + "  EZWifiConfigManager.EZWifiConfigStatus.DEVICE_WIFI_CONNECTING");
                        showToast("CONNECTING");
                    } else if (status == EZWifiConfigStatus.DEVICE_WIFI_CONNECTED) {
                        // TODO: 2018/6/27  设备wifi连接成功
                        Log.d(TAG, deviceSerial + "  EZWifiConfigManager.EZWifiConfigStatus.DEVICE_WIFI_CONNECTED");
                        showToast("CONNECTED");
                    } else if (status == EZWifiConfigStatus.DEVICE_PLATFORM_REGISTED) {
                        // TODO: 2018/6/27  设备注册平台成功可以添加设备
                        Log.d(TAG,
                            deviceSerial + "  EZWifiConfigManager.EZWifiConfigStatus.DEVICE_PLATFORM_REGISTED");
                        showToast("REGISTED");
                        showSuccess();
                        EZWifiConfigManager.stopConfigWiFi();
                    } else if (status == EZWifiConfigStatus.TIME_OUT) {
                        // TODO: 2018/6/27 超时
                        Log.d(TAG, deviceSerial + "  EZWifiConfigManager.EZWifiConfigStatus.TIME_OUT");
                        showToast(getString(R.string.string_timeout));
                        showReset();
                        EZWifiConfigManager.stopConfigWiFi();
                    }
                }
            });
    }

    private void stopModeConfigWifi() {
        EZWifiConfigManager.stopConfigWiFi();
    }

    private void startApConfig() {
        showconfig();
        EZWifiConfigManager.startAPConfigWifiWithSsid(mDeviceSerial, mSSID, mPassword, mVerifyCode,
            new APWifiConfig.APConfigCallback() {
                @Override
                public void onSuccess() {
                    // TODO: 2018/6/28 配网成功
                    EZWifiConfigManager.stopAPConfigWifiWithSsid();
                    showSuccess();
                    Log.d(TAG, "startAPConfigWifiWithSsid onSuccess mDeviceSerial = " + mDeviceSerial);
                }

                @Override
                public void OnError(int code) {
                    // TODO: 2018/6/28 配网失败
                    EZWifiConfigManager.stopAPConfigWifiWithSsid();
                    Log.d(TAG, "startAPConfigWifiWithSsid  OnError mDeviceSerial = " + mDeviceSerial);
                    showReset();
                }
            });
    }
    private void showReset() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mResetTv.setVisibility(View.VISIBLE);
                mAddTv.setVisibility(View.GONE);
            }
        });
    }
    private void showconfig() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                mResetTv.setVisibility(View.GONE);
                mAddTv.setVisibility(View.GONE);
            }
        });
    }
    private void showSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mResetTv.setVisibility(View.GONE);
                mAddTv.setVisibility(View.VISIBLE);
                Intent intent = new Intent(SmartConfigActivity.this, AddDeviceActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);
                finish();
            }
        });
    }

    private void stopAPConfig() {
        EZWifiConfigManager.stopAPConfigWifiWithSsid();
    }

    @Override
    protected void onStop() {
        stopModeConfigWifi();
        stopAPConfig();
        super.onStop();
    }
}
