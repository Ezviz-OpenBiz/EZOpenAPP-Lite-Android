package com.ezviz.open.view.avctivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezviz.open.view.fragment.CaptureFragment;
import com.ezviz.open.view.widget.Topbar;

import com.ezviz.open.R;
/**
 * Description:添加设备
 * Created by dingwei3
 *
 * @date : 2016/12/21
 */
public class AddDeviceActivity extends RootActivity implements View.OnClickListener {

    /**
     * 扫描二维码添加
     */
    private static final int TYPE_CODE = 1;
    /**
     * 手动输入序列号添加
     */
    private static final int TYPE_SERIAL = 2;
    private int mType = 0;
    private Topbar mTopbar;
    private ImageView mCodeImg;
    private ImageView mSerialImg;
    private TextView mCodeTextView;
    private TextView mSerialTextView;
    private TextView mCodeTipTextView;
    private LinearLayout mBottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        initView();
    }

    private void initView() {
        mTopbar = (Topbar) findViewById(R.id.capture_topbar);
        mTopbar.setTitle(R.string.string_add_device);
        mCodeImg = (ImageView) findViewById(R.id.img_code);
        mSerialImg = (ImageView) findViewById(R.id.img_serial);
        mCodeTextView = (TextView) findViewById(R.id.text_code);
        mSerialTextView = (TextView) findViewById(R.id.text_serial);
        mCodeTipTextView = (TextView) findViewById(R.id.code_tip_textview);
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar_layout);
        mTopbar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
            @Override
            public void onLeftButtonClicked() {
                finish();
            }

            @Override
            public void onRightButtonClicked() {

            }
        });
        mSerialImg.setOnClickListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new CaptureFragment()).commitAllowingStateLoss();
        mSerialImg.setImageResource(R.drawable.number_normal);
        mCodeImg.setImageResource(R.drawable.code_selected);
        mCodeTextView.setSelected(true);
        mSerialTextView.setSelected(false);
        mCodeTipTextView.setVisibility(View.VISIBLE);
        mBottomBar.setBackgroundColor(android.graphics.Color.parseColor("#00ffffff"));
    }

    @Override
    public void onClick(View view) {
        if (view == mSerialImg) {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, SearchDeviceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
