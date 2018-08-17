package com.ezviz.open.view.avctivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.ezviz.open.view.fragment.AppFragment;
import com.ezviz.open.view.fragment.DeviceFragment;
import com.ezviz.open.view.fragment.MessageFragment;
import com.ezviz.open.view.widget.EZFragmentTabHost;


import java.util.ArrayList;
import com.ezviz.open.R;
public class MainActivity extends RootActivity {
    private FrameLayout mLayout;
    private EZFragmentTabHost mTabHost;
    public int[] BOTTOM_ITEMS_ID = {R.string.string_main_bottom_device, R.string.string_main_bottom_message, R.string.string_main_bottom_app};
    private int[] BOTTOM_ITEMS_IMG_ID = {R.drawable.resource_normal, R.drawable.message_normal, R.drawable.me_normal};
    private int[] BOTTOM_ITEMS_IMG_ID_SELECT = {R.drawable.resource_selected, R.drawable.message_selected, R.drawable.me_selected};

    private ArrayList<BottomView> mViews = new ArrayList<BottomView>();
    private Class fragmentArray[] = {DeviceFragment.class, MessageFragment.class, AppFragment.class};



    private class BottomView {
        ImageView mImageView;
        TextView mTextView;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mLayout = (FrameLayout) findViewById(R.id.home_fragment_layout);
        mTabHost = (EZFragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.home_fragment_layout);
        //得到fragment的个数
        int count = BOTTOM_ITEMS_ID.length;
        for (int i = 0; i < count; i++) {
            View view = getBottomItemView(i);
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(String.valueOf(BOTTOM_ITEMS_ID[i])).setIndicator(view);
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                updateTab(tabId);
            }
        });
        mTabHost.getTabWidget().setDividerDrawable(null);
        updateTab(String.valueOf(BOTTOM_ITEMS_ID[0]));
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getBottomItemView(int index) {
        BottomView bottomView = new BottomView();
        View view = LayoutInflater.from(this).inflate(R.layout.main_bottom_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.bottom_item_imageview);
        TextView textView = (TextView) view.findViewById(R.id.bottom_item_text);
        textView.setText(BOTTOM_ITEMS_ID[index]);
        imageView.setImageResource(BOTTOM_ITEMS_IMG_ID[index]);
        bottomView.mImageView = imageView;
        bottomView.mTextView = textView;
        mViews.add(bottomView);
        return view;
    }

    private void updateTab(String tabid) {
        for (int i = 0; i < BOTTOM_ITEMS_ID.length; i++) {
            if (tabid.equals(String.valueOf(BOTTOM_ITEMS_ID[i]))) {
                mViews.get(i).mTextView.setTextColor(getResources().getColor(R.color.main_item_text_select));
                mViews.get(i).mImageView.setImageResource(BOTTOM_ITEMS_IMG_ID_SELECT[i]);
            } else {
                mViews.get(i).mTextView.setTextColor(getResources().getColor(R.color.main_item_text_normal));
                mViews.get(i).mImageView.setImageResource(BOTTOM_ITEMS_IMG_ID[i]);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
