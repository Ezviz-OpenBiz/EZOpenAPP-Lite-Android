package com.ezviz.open.view;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/15
 */
public interface BaseView {

    public void showLoadDialog();

    public void showLoadDialog(int stringResId);

    public void showLoadDialog(String string);

    public void dismissLoadDialog();

    public void showToast(String res);

    public void showToast(int resId);

    public void showToast(int resId,int errorCode);

}


