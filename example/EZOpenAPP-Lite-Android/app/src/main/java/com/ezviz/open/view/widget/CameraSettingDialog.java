package com.ezviz.open.view.widget;

import android.content.Context;



import java.util.ArrayList;
import java.util.List;
import com.ezviz.open.R;
/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/30
 */
public class CameraSettingDialog extends DialogPopFromBottom {

    private OnSettingItemClickLientener mOnSettingItemClickLientener;

    public interface OnSettingItemClickLientener{
        /**
         * 修改通道名称
         */
        public void modifyCameraName(int position);

        /**
         * 刷新通道封面
         */
        public void modifyCameraCover(int position);

        /**
         * 设置设备活动检测
         * @param defence
         */
        public void setDefence(int position,int defence);

        /**
         * 进入更多设置
         */
        public void moreSetting(int position);
    }

    /**
     * 设备是否在线
     */
    private boolean isOnline;

    private int mCameraListPostion = 0;

    /**
     * @param context
     * @param position
     * @param status 设备状态，是否在线
     * @param defence   设备布撤防状态 -1为不支持布撤防
     * @param onSettingItemClickLientener   点击监听
     */
    public CameraSettingDialog(Context context,int position,int  status,int defence, OnSettingItemClickLientener onSettingItemClickLientener) {
        super(context);
        mCameraListPostion = position;
        mOnSettingItemClickLientener = onSettingItemClickLientener;
        List<Integer> list = new ArrayList<Integer>();
        list.add(R.string.modify_camera_name);
        //设备在线设置项
        if (status == 1){
            list.add(R.string.modify_camera_cover);
            if (defence != -1) {
                list.add(defence == 0 ? R.string.on_defence:R.string.off_defence);
            }
        }
        list.add(R.string.camera_more_setting);
        setList(list,true);
        setOnBottomItemClickListener(new OnBottomItemClickListener() {
            @Override
            public void onDialogItemClick(int resId) {
                if (resId == R.string.modify_camera_name){
                    if (mOnSettingItemClickLientener != null){
                        mOnSettingItemClickLientener.modifyCameraName(mCameraListPostion);
                    }
                }else if(resId == R.string.modify_camera_cover){
                    if (mOnSettingItemClickLientener != null){
                        mOnSettingItemClickLientener.modifyCameraCover(mCameraListPostion);
                    }
                }else if(resId == R.string.off_defence){
                    if (mOnSettingItemClickLientener != null){
                        mOnSettingItemClickLientener.setDefence(mCameraListPostion,0);
                    }
                }else if(resId == R.string.on_defence){
                    if (mOnSettingItemClickLientener != null){
                        mOnSettingItemClickLientener.setDefence(mCameraListPostion,1);
                    }
                }else if(resId == R.string.camera_more_setting){
                    if (mOnSettingItemClickLientener != null){
                        mOnSettingItemClickLientener.moreSetting(mCameraListPostion);
                    }
                }
            }
        });
    }
}


