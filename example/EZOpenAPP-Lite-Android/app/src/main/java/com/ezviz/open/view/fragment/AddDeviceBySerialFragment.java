package com.ezviz.open.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.ezviz.open.R;


/**
 * 通过手动输入序列号添加设备Fragment
 */
public class AddDeviceBySerialFragment extends BaseFragment {

    private static final String TAG = "AddDeviceBySerialFragment";
    private EditText mEditText;
    private Button mButton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_device_by_serial, null);
        mEditText = (EditText) view.findViewById(R.id.seriesNumberEt);
        mButton = (Button) view.findViewById(R.id.btn_next);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String serialNo = mEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(serialNo)) {
                    searchCameraBySN(serialNo);
                }
            }
        });
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
        return view;
    }

    /**
     * 判断序列号是否规范，跳转添加界面进行处理
     * @param serialNo
     */
    private void searchCameraBySN(String serialNo){
        if (TextUtils.isEmpty(serialNo)){
            showToast(R.string.serial_number_is_null);
            return;
        }
        if (serialNo.length() != 9){
            showToast(R.string.serial_number_put_the_right_no);
            return;
        }
//        Intent intent = new Intent(mContext, .class);
//        intent.putExtra(EZOpenConstant.EXTRA_DEVICE_SERIAL,serialNo);
//        mContext.startActivity(intent);
    }
}
