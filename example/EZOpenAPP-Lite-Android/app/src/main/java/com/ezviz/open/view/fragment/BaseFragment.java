package com.ezviz.open.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.ezviz.open.utils.ToastUtls;
import com.ezviz.open.view.widget.LoadProgressDialog;

import java.util.concurrent.atomic.AtomicBoolean;
import com.ezviz.open.R;
/**
 * Description:Basefragment
 * Created by dingwei3
 *
 * @date : 2016/12/12
 */
public abstract class BaseFragment extends Fragment {

    protected AtomicBoolean isInit = new AtomicBoolean(false);
    protected FragmentActivity mActivity;

    public Context mContext;
    private LoadProgressDialog mLoadProgressDialog;

   public void showLoadDialog(){
        if (mLoadProgressDialog == null){
            mLoadProgressDialog = new LoadProgressDialog(mActivity);
            mLoadProgressDialog.setCancelable(false);
            mLoadProgressDialog.setCanceledOnTouchOutside(false);
        }
       mLoadProgressDialog.show();
    }

    public void showLoadDialog(int stringRes){
        if (mLoadProgressDialog == null){
            mLoadProgressDialog = new LoadProgressDialog(mActivity);
            mLoadProgressDialog.setCancelable(false);
            mLoadProgressDialog.setCanceledOnTouchOutside(false);
            mLoadProgressDialog.setMessage(stringRes);
        }else{
            mLoadProgressDialog.setMessage(stringRes);
        }
        mLoadProgressDialog.show();
    }

    public void showLoadDialog(String string){
        if (mLoadProgressDialog == null){
            mLoadProgressDialog = new LoadProgressDialog(mActivity);
            mLoadProgressDialog.setCancelable(false);
            mLoadProgressDialog.setCanceledOnTouchOutside(false);
            mLoadProgressDialog.setMessage(TextUtils.isEmpty(string)?"":string);
        }else{
            mLoadProgressDialog.setMessage(TextUtils.isEmpty(string)?"":string);
        }
        mLoadProgressDialog.show();
    }

    public void dismissLoadDialog(){
        if (mLoadProgressDialog != null && mLoadProgressDialog.isShowing()) {
            mLoadProgressDialog.dismiss();
        }
    }

    public void showToast(String res){
        ToastUtls.showToast(mContext,res);
    }

    public void showToast(int resId){
        ToastUtls.showToast(mContext,resId);
    }

    public void showToast(int resId,int errorCode){
        ToastUtls.showToast(mContext,resId,errorCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

}
