package com.ezviz.open.view.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ezviz.open.utils.EZOpenUtils;
import com.ezviz.open.R;
/**
 * Created by yudan on 16/6/22.
 */
public class CommomAlertDialog {

    /**
     * 验证码输入回调
     */
    public interface VerifyCodeInputListener {

        void onInputVerifyCode(final String verifyCode);
    }

    /**
     *
     * 验证码错误回调
     */
    public interface VerifyCodeErrorListener{

        void verifyCodeError();
    }

    private AlertDialog mPasswordDialog = null;

    public static AlertDialog VerifyCodeInputDialog(final Context context, final VerifyCodeInputListener l) {

        LinearLayout passwordErrorLayout = new LinearLayout(context);
        FrameLayout.LayoutParams layoutLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        passwordErrorLayout.setOrientation(LinearLayout.VERTICAL);
        passwordErrorLayout.setLayoutParams(layoutLp);

        TextView message1 = new TextView(context);
        LinearLayout.LayoutParams message1Lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        message1Lp.gravity = Gravity.CENTER_HORIZONTAL;
        message1Lp.leftMargin = EZOpenUtils.dip2px(context, 10);
        message1Lp.rightMargin = EZOpenUtils.dip2px(context, 10);
        message1.setGravity(Gravity.CENTER);
        message1.setTextColor(Color.rgb(0, 0, 0));
        message1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        passwordErrorLayout.addView(message1, message1Lp);

        final EditText newPassword = new EditText(context);
        newPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EZOpenUtils.PSW_MAX_LENGTH)});
        LinearLayout.LayoutParams newPasswordLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newPasswordLp.leftMargin = EZOpenUtils.dip2px(context, 10);
        newPasswordLp.rightMargin = EZOpenUtils.dip2px(context, 10);
        newPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        newPassword.setSingleLine(true);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordErrorLayout.addView(newPassword, newPasswordLp);

        TextView message2 = new TextView(context);
        LinearLayout.LayoutParams message2Lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        message2Lp.gravity = Gravity.CENTER_HORIZONTAL;
        message2Lp.leftMargin = EZOpenUtils.dip2px(context, 10);
        message2Lp.rightMargin = EZOpenUtils.dip2px(context, 10);
//        message2.setGravity(Gravity.CENTER);
        message2.setTextColor(Color.rgb(0, 0, 0));
        message2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        passwordErrorLayout.addView(message2, message2Lp);
        // 使用布局中的视图创建AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        message1.setText("");
        message2.setText(R.string.vrifycode_dialog_tip);
        builder.setTitle(R.string.vrifycode_dialog_title);
        builder.setView(passwordErrorLayout);
        builder.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        builder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String verifyCode = newPassword.getText().toString();
                if (TextUtils.isEmpty(verifyCode)) {
                    return;
                }
                l.onInputVerifyCode(verifyCode);
            }
        });
        AlertDialog mPasswordDialog = null;
        mPasswordDialog = builder.create();
        mPasswordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return mPasswordDialog;
    }

    public static AlertDialog DeviceEncryptDialog(final Context context, final VerifyCodeInputListener verifyCodeInputListener) {

        LinearLayout passwordErrorLayout = new LinearLayout(context);
        FrameLayout.LayoutParams layoutLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        passwordErrorLayout.setOrientation(LinearLayout.VERTICAL);
        passwordErrorLayout.setLayoutParams(layoutLp);

        TextView message1 = new TextView(context);
        LinearLayout.LayoutParams message1Lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        message1Lp.gravity = Gravity.CENTER_HORIZONTAL;
        message1Lp.leftMargin = EZOpenUtils.dip2px(context, 10);
        message1Lp.rightMargin = EZOpenUtils.dip2px(context, 10);
        message1.setGravity(Gravity.CENTER);
        message1.setTextColor(Color.rgb(0, 0, 0));
        message1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        passwordErrorLayout.addView(message1, message1Lp);

        final EditText newPassword = new EditText(context);
        newPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EZOpenUtils.PSW_MAX_LENGTH)});
        LinearLayout.LayoutParams newPasswordLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newPasswordLp.leftMargin = EZOpenUtils.dip2px(context, 10);
        newPasswordLp.rightMargin = EZOpenUtils.dip2px(context, 10);
        newPassword.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        newPassword.setSingleLine(true);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordErrorLayout.addView(newPassword, newPasswordLp);

        TextView message2 = new TextView(context);
        LinearLayout.LayoutParams message2Lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        message2Lp.gravity = Gravity.CENTER_HORIZONTAL;
        message2Lp.leftMargin = EZOpenUtils.dip2px(context, 10);
        message2Lp.rightMargin = EZOpenUtils.dip2px(context, 10);
        message2.setGravity(Gravity.CENTER);
        message2.setTextColor(Color.rgb(0, 0, 0));
        message2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        passwordErrorLayout.addView(message2, message2Lp);
        // 使用布局中的视图创建AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        message1.setText("");
        builder.setTitle(R.string.vrifycode_dialog_title);
        builder.setView(passwordErrorLayout);
        builder.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        builder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String verifyCode = newPassword.getText().toString();
                if (TextUtils.isEmpty(verifyCode)) {
                    return;
                }else{
                    if (verifyCodeInputListener != null){
                        verifyCodeInputListener.onInputVerifyCode(verifyCode);
                    }
                }
            }
        });
        AlertDialog mPasswordDialog = null;
        mPasswordDialog = builder.create();
        mPasswordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return mPasswordDialog;
    }
}
