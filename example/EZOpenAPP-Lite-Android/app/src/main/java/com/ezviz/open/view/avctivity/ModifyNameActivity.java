package com.ezviz.open.view.avctivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;


import com.ezviz.open.common.EZOpenConstant;
import com.ezviz.open.presenter.ModifyNamePresenter;
import com.ezviz.open.view.ModifyNameView;
import com.ezviz.open.view.widget.Topbar;
import com.ezviz.open.R;
/**
 * Description:修改名称页面
 * Created by dingwei3
 *
 * @date : 2017/1/3
 */
public class ModifyNameActivity extends RootActivity implements ModifyNameView {
    private static final int TYPE_MODIFY_DEVICE_NAME = 1;
    private static final int TYPE_MODIFY_CAMERA_NAME = 2;
    private Topbar mTopbar;
    private String mDeviceSerial;
    private int mCameraNo = -1;
    private int mType;
    private EditText mNameEditText;
    private ModifyNamePresenter mModifyNamePresenter;

    private String mName;

    /**
     * 设备名称修改
     * @param context
     * @param deviceSerial  设备序列号
     */
    public static void startModifyDeviceNameActivity(Context context, String name,String deviceSerial){
        Intent intent = new Intent(context,ModifyNameActivity.class);
        intent.putExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL,deviceSerial);
        intent.putExtra(EZOpenConstant.EXTRA_MODIFY_NAME,name);
        intent.putExtra(EZOpenConstant.EXTRA_MODIFY_NAME_TYPE,TYPE_MODIFY_DEVICE_NAME);
        context.startActivity(intent);
    }

    /**
     * 通道名称修改
     * @param context
     * @param deviceSerial  设备序列号
     */
    public static void startModifyCameraNameActivity(Context context, String name,String deviceSerial,int cameraNo){
        Intent intent = new Intent(context,ModifyNameActivity.class);
        intent.putExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL,deviceSerial);
        intent.putExtra(EZOpenConstant.EXTRA_CAMERA_NO,cameraNo);
        intent.putExtra(EZOpenConstant.EXTRA_MODIFY_NAME,name);
        intent.putExtra(EZOpenConstant.EXTRA_MODIFY_NAME_TYPE,TYPE_MODIFY_CAMERA_NAME);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name);
        mModifyNamePresenter = new ModifyNamePresenter(this);
        initDate();
        initView();
    }

    private void initDate() {
        Intent intent = getIntent();
        mDeviceSerial = intent.getStringExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL);
        mCameraNo = intent.getIntExtra(EZOpenConstant.EXTRA_CAMERA_NO,-1);
        mType = intent.getIntExtra(EZOpenConstant.EXTRA_MODIFY_NAME_TYPE,TYPE_MODIFY_DEVICE_NAME);
        mName = intent.getStringExtra(EZOpenConstant.EXTRA_MODIFY_NAME);
    }

    private void initView() {
        mTopbar = (Topbar) findViewById(R.id.modify_name_topbar);
        if (mType == TYPE_MODIFY_DEVICE_NAME){
            mTopbar.setTitle(R.string.device_name);
        }else if(mType == TYPE_MODIFY_CAMERA_NAME){
            mTopbar.setTitle(R.string.camera_name);
        }
        mTopbar.setRightText(R.string.save);
        mTopbar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
            @Override
            public void onLeftButtonClicked() {
                onBackPressed();
            }
            @Override
            public void onRightButtonClicked() {
                String newName = mNameEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(mDeviceSerial) && !TextUtils.isEmpty(newName) && !mName.equals(newName)) {
                    if (mType == TYPE_MODIFY_DEVICE_NAME) {
                        mModifyNamePresenter.modifyDeviceName(mDeviceSerial, newName);
                    }else if(mType == TYPE_MODIFY_CAMERA_NAME){
                        mModifyNamePresenter.modifyCameraName(mDeviceSerial, mCameraNo,newName);
                    }
                }
            }
        });
        mNameEditText = (EditText) findViewById(R.id.text_name);
        mNameEditText.setText(mName);
        mNameEditText.setSelection(mName.length());
    }

    @Override
    public void handleModifySuccess() {
        finish();
    }
}


