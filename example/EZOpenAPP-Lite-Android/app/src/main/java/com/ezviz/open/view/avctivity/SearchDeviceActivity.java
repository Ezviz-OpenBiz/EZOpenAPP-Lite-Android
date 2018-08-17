package com.ezviz.open.view.avctivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;



import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.view.widget.Topbar;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZProbeDeviceInfo;

import java.util.ArrayList;
import java.util.List;
import com.ezviz.open.R;
import com.videogo.openapi.bean.EZProbeDeviceInfoResult;

public class SearchDeviceActivity extends RootActivity implements View.OnClickListener {

    protected static final String CONFIG_TYPE_KEY = "config_type";
    protected static final int CONFIG_TYPE_WIRED_CONNECTION = 0;
    protected static final int CONFIG_TYPE_SMARTCONFIG = 1;
    protected static final int CONFIG_TYPE_SOUNDWAVE = 2;
    protected static final int CONFIG_TYPE_AP = 3;
    protected static final int CONFIG_TYPE_SMART_SOUNDWAVE = 4;
    private EditText mDeviceSerialEt;
    private EditText mVerifycodeEt;

    private String mDeviceSerial;
    private String mVerifyCode;
    private String mDeviceType;
    private int mType;

    private TextView mSearchTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);
        Intent intent = getIntent();
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
        mDeviceSerialEt = (EditText) findViewById(R.id.seriesNumberEt);
        mVerifycodeEt = (EditText) findViewById(R.id.verifycodeEt);
        mSearchTv = (TextView) findViewById(R.id.search_tv);
        mSearchTv.setOnClickListener(this);
        mDeviceSerial = intent.getStringExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL);
        mVerifyCode = intent.getStringExtra(EZOpenConstant.EXTRA_DEVICE_VERIFYCODE);
        mDeviceType = intent.getStringExtra(EZOpenConstant.EXTRA_DEVICE_TYPE);
        mDeviceSerialEt.setText(mDeviceSerial);
        mVerifycodeEt.setText(mVerifyCode);
        if (!TextUtils.isEmpty(mDeviceSerial)) {
            searchDevice();
        }
    }
    private void searchDevice() {
        mDeviceSerial = mDeviceSerialEt.getText().toString().trim();
        mVerifyCode = mVerifycodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(mDeviceSerial)) {
            showToast("DeviceSerial is null");
            return;
        }
        if (TextUtils.isEmpty(mVerifyCode)) {
            showToast("VerifyCode is null");
            return;
        }
        String intentDeviceSerial = getIntent().getStringExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL);
        if (!mDeviceSerial.equalsIgnoreCase(intentDeviceSerial)){
            //序列号与传到此界面的序列号不一致，证明手动输入，此时不确定设备型号，需要将之前的设备型号置空
            mDeviceType = "";
        }
        // TODO: 2018/6/25 搜索设备
        showLoadDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                    final EZProbeDeviceInfoResult probeDeviceInfoResult = EZOpenSDK.probeDeviceInfo(mDeviceSerial,mDeviceType);
                    if (probeDeviceInfoResult.getBaseException() == null){
                        // TODO: 2018/6/25 添加设备
                        Intent intent = new Intent(SearchDeviceActivity.this, AddDeviceActivity.class);
                        intent.putExtras(getBundle());
                        startActivity(intent);
                    }else{
                        // TODO: 2018/6/25 错误处理
                        switch (probeDeviceInfoResult.getBaseException().getErrorCode()){
                            case 120023:
                                // TODO: 2018/6/25  设备不在线，未被用户添加 （这里需要调用wifi一键配置）
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showSelectNetConfig(probeDeviceInfoResult.getEZProbeDeviceInfo());
                                    }
                                });
                                break;
                            case 120002:
                                // TODO: 2018/6/25  设备不存在，未被用户添加 （这里需要调用wifi一键配置）
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showSelectNetConfig(probeDeviceInfoResult.getEZProbeDeviceInfo());
                                    }
                                });
                                break;
                            case 120029:
                                // TODO: 2018/6/25  设备不在线，已经被自己添加 (这里需要调用wifi一键配置)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showSelectNetConfig(probeDeviceInfoResult.getEZProbeDeviceInfo());
                                    }
                                });
                                break;
                            case 120020:
                                // TODO: 2018/6/25 设备在线，已经被自己添加 (给出提示)
                            case 120022:
                                // TODO: 2018/6/25  设备在线，已经被别的用户添加 (给出提示)
                            case 120024:
                                // TODO: 2018/6/25  设备不在线，已经被别的用户添加 (给出提示)

                            default:
                                // TODO: 2018/6/25 请求异常
                                showToast("Request failed = "
                                        + probeDeviceInfoResult.getBaseException().getErrorCode());
                                break;
                        }
                    }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadDialog();
                    }
                });
            }
        }).start();
    }

    private Bundle getBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("SerialNo", mDeviceSerial);
        bundle.putString("very_code", mVerifyCode);
        bundle.putString("device_type", mDeviceType);
        return bundle;
    }

    @Override
    public void onClick(View view) {
        if (view == mSearchTv) {
            searchDevice();
        }
    }

    private void showSelectNetConfig(EZProbeDeviceInfo probeDeviceInfo) {
        final List<String> list = new ArrayList<String>();
        final List<Integer> list_types = new ArrayList<Integer>();
        list.add(getString(R.string.string_wired_connection));
        list_types.add(CONFIG_TYPE_WIRED_CONNECTION);

        if (probeDeviceInfo == null){
            // 未查询到设备信息，不确定设备支持的配网能力,需要用户根据指示灯判断
            //若设备指示灯蓝色闪烁，请选择
            list.add(getString(R.string.string_smart_connection));
            list_types.add(CONFIG_TYPE_SMARTCONFIG);


            // 若设备指示灯蓝色闪烁，请选择
            list.add(getString(R.string.string_ap_connection));
            list_types.add(CONFIG_TYPE_AP);

        }else{
            // 查询到设备信息，根据能力级选择配网方式
            if (probeDeviceInfo.getSupportWifi() == 3) {
                list.add(getString(R.string.string_smart_connection));
                list_types.add(CONFIG_TYPE_SMARTCONFIG);
            }
            if (probeDeviceInfo.getSupportSoundWave() == 1) {
                list.add(getString(R.string.string_sound_wave_connection));
                list_types.add(CONFIG_TYPE_SOUNDWAVE);
            }
            if (probeDeviceInfo.getSupportAP() == 2) {
                list.add(getString(R.string.string_ap_connection));
                list_types.add(CONFIG_TYPE_AP);
            }
            if (probeDeviceInfo.getSupportSoundWave() == 1 && probeDeviceInfo.getSupportWifi() == 3) {
                list.add(getString(R.string.string_smart_and_sound_wave_connection));
                list_types.add(CONFIG_TYPE_SMART_SOUNDWAVE);
            }
        }


        final String items[] = new String[list.size()];
        list.toArray(items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.string_device_network_mode).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                dialogInterface.dismiss();
                switch (list_types.get(i)) {
                    case CONFIG_TYPE_WIRED_CONNECTION:
                        // TODO: 2018/6/28 有线连接
                        break;
                    case CONFIG_TYPE_SMARTCONFIG:
                        // TODO: 2018/6/26 smart config wifi
                        Intent intent1 = new Intent(SearchDeviceActivity.this, WiFiSelecteActivity.class);
                        intent1.putExtras(getBundle());
                        intent1.putExtra(CONFIG_TYPE_KEY, CONFIG_TYPE_SMARTCONFIG);
                        startActivity(intent1);
                        break;
                    case CONFIG_TYPE_SOUNDWAVE:
                        // TODO: 2018/6/26 声波配网
                        Intent intent2 = new Intent(SearchDeviceActivity.this, WiFiSelecteActivity.class);
                        intent2.putExtra(CONFIG_TYPE_KEY, CONFIG_TYPE_SOUNDWAVE);
                        intent2.putExtras(getBundle());
                        startActivity(intent2);
                        break;
                    case CONFIG_TYPE_AP:
                        // TODO: 2018/6/26 ap模式配网
                        Intent intent3 = new Intent(SearchDeviceActivity.this, WiFiSelecteActivity.class);
                        intent3.putExtra(CONFIG_TYPE_KEY, CONFIG_TYPE_AP);
                        intent3.putExtras(getBundle());
                        startActivity(intent3);
                        break;
                    case CONFIG_TYPE_SMART_SOUNDWAVE:
                        // TODO: 2018/6/26 声波配网+普通配网同时进行
                        Intent intent4 = new Intent(SearchDeviceActivity.this, WiFiSelecteActivity.class);
                        intent4.putExtra(CONFIG_TYPE_KEY, CONFIG_TYPE_SMART_SOUNDWAVE);
                        intent4.putExtras(getBundle());
                        startActivity(intent4);
                        break;
                    default:
                        break;
                }
            }
        }).setNegativeButton(android.R.string.cancel, null).show();
    }
}
