package com.ezviz.open.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import com.ezviz.open.presenter.AppPresenter;
import com.ezviz.open.utils.EZOpenUtils;
import com.ezviz.open.view.AppView;
import com.videogo.main.EzvizWebViewActivity;

import static com.ezviz.open.utils.EZDeviceDBManager.deleteAllDevice;
import com.ezviz.open.R;
/**
 * Description: 我的界面
 * Created by dingwei3
 *
 * @date : 2016/12/12
 */
public class AppFragment extends BaseLazyFragment implements AppView, View.OnClickListener {

    private final static String TAG = "BaseLazyFragment";
    private TextView mSwitchLogin;
    private AppPresenter mAppPresenter;
    private TextView mTextViewVersion;

    private View mModifyPasswordView;

    public AppFragment() {
        mAppPresenter = new AppPresenter(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app, container, false);
        mSwitchLogin = (TextView) view.findViewById(R.id.text_switch_login);
        mTextViewVersion = (TextView) view.findViewById(R.id.text_version);
        mTextViewVersion.setText(mAppPresenter.getVersionString(mActivity));
        mSwitchLogin.setOnClickListener(this);
        intitSettingView(view);
        return view;
    }

    private void intitSettingView(View view) {
        mModifyPasswordView = view.findViewById(R.id.modify_password_layout);
        mModifyPasswordView.setOnClickListener(this);
    }


    @Override
    protected void lazyLoad() {

    }

    private void switchAccount() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(mActivity);
        exitDialog.setTitle(R.string.exit);
        exitDialog.setMessage(R.string.exit_tip);
        exitDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoadDialog(R.string.loading_logout);
                mAppPresenter.switchAccount();
            }
        });
        exitDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        exitDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void handleLogoutSuccess() {
        dismissLoadDialog();
        EZOpenUtils.gotoLogin();
        deleteAllDevice();
        mActivity.finish();
    }

    @Override
    public void onClick(View view) {
     if (view == mModifyPasswordView) {
            // TODO: 2016/12/28 修改密码
            Intent intent = new Intent(mContext, EzvizWebViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EzvizWebViewActivity.EXTRA_WEBVIEW_ACTION, EzvizWebViewActivity.WEBVIEW_ACTION_FORGETPASSWORD);
            mContext.startActivity(intent);
        } else if (view == mSwitchLogin) {
            switchAccount();
        }
    }
}
