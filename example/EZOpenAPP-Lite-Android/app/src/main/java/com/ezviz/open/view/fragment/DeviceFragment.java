package com.ezviz.open.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.ezviz.open.presenter.DevicePresenter;
import com.ezviz.open.view.avctivity.AddDeviceActivity;
import com.ezviz.open.R;
/**
 * Description:设备资源相关fragment
 * Created by dingwei3
 *
 * @date : 2016/12/12
 */
public class DeviceFragment extends BaseLazyFragment{

    private DevicePresenter mDevicePresenter;
    private static final int INDEX_CAMERA = 0;
    private static final int INDEX_DEVICE = 1;
    private int[] TOP_ITEMS_ID = {R.string.string_device_top_cameralist,R.string.string_device_top_devicelist};
    private static final String TAG = "DeviceFragment";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageView mAddDeviceImg;


    public DeviceFragment() {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, null);
        mAddDeviceImg = (ImageView) view.findViewById(R.id.img_add_device);
        mAddDeviceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddDeviceActivity.class);
                mContext.startActivity(intent);
            }
        });
        mViewPager = (ViewPager) view.findViewById(R.id.device_tab_content_viewpager);
        mViewPager.setAdapter(new DeviceFragmentPagerAdapter(getChildFragmentManager(),mContext));
        mTabLayout = (TabLayout) view.findViewById(R.id.device_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        return view;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class DeviceFragmentPagerAdapter extends FragmentPagerAdapter {
        private Context context;

        public DeviceFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == INDEX_CAMERA){
                return new CameraListFragment();
            }else if (position == INDEX_DEVICE){
                return new DeviceListFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return TOP_ITEMS_ID.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return context.getString(TOP_ITEMS_ID[position]);
        }
    }
}
