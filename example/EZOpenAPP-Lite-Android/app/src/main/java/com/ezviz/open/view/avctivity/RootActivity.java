package com.ezviz.open.view.avctivity;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ezviz.open.utils.ToastUtls;
import com.ezviz.open.view.widget.LoadProgressDialog;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/28
 */
public class RootActivity extends AppCompatActivity {

    private LoadProgressDialog mLoadProgressDialog;

    public boolean isStop;



    public void showLoadDialog(){
        if (mLoadProgressDialog == null){
            mLoadProgressDialog = new LoadProgressDialog(this);
            mLoadProgressDialog.setCancelable(false);
            mLoadProgressDialog.setCanceledOnTouchOutside(false);
        }
        mLoadProgressDialog.show();
    }

    public void showLoadDialog(int stringRes){
        if (mLoadProgressDialog == null){
            mLoadProgressDialog = new LoadProgressDialog(this);
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
            mLoadProgressDialog = new LoadProgressDialog(this);
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

    public void showToast(final String res){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtls.showToast(RootActivity.this,res);
            }
        });
    }

    public void showToast(final int resId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtls.showToast(RootActivity.this,resId);
            }
        });
    }

    public void showToast(final int resId, final int errorCode){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtls.showToast(RootActivity.this,resId,errorCode);
            }
        });
    }

    public String getErrorTip(int id, int errCode) {
        StringBuffer errorTip = new StringBuffer();
        if (errCode != 0) {
            int errorId = getErrorId(errCode);
            if (errorId != 0) {
                errorTip.append(getString(errorId));
            } else {
                errorTip.append(getString(id)).append(" (").append(errCode).append(")");
            }
        } else {
            errorTip.append(getString(id));
        }
        return errorTip.toString();
    }

    public int getErrorId(int errorCode) {
        int errorId = this.getResources().getIdentifier("error_code_" + errorCode, "string", this.getPackageName());
        return errorId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStop = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        isStop = true;
        super.onStop();

    }
}


