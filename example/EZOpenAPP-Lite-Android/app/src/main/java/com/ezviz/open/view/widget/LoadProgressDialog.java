package com.ezviz.open.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.ezviz.open.R;



/**
 * 圆形加载圈 加载框
 *
 * @author dingwei3
 * @data 2015-10-13
 */
public class LoadProgressDialog extends Dialog {
    private Context mContext;
    private TextView mMessageTextView;

    public interface ProgressListener {
        public void progress(int progress);
    }

    public LoadProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public LoadProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        intView();
    }

    public LoadProgressDialog(Context context) {
        super(context, R.style.LoaderProgress);
        mContext = context;
        intView();

    }

    /**
     * Title: onWindowFocusChanged Description:可见时启动加载的动画
     *
     * @param hasFocus
     * @see android.app.Dialog#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public void setMessage(String message) {
        if (mMessageTextView != null) {
            if (!TextUtils.isEmpty(message)) {
                if (mMessageTextView.getVisibility() != View.VISIBLE) {
                    mMessageTextView.setVisibility(View.VISIBLE);
                }
                mMessageTextView.setText(message);
            } else {
                if (mMessageTextView.getVisibility() != View.GONE) {
                    mMessageTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setMessage(int messageRes) {
        setMessage(mContext.getResources().getString(messageRes));
    }

    private void intView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loaderprogress, null);
        mMessageTextView = (TextView) view.findViewById(R.id.message);
        setContentView(view);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.dimAmount = 0.2f;
        getWindow().setAttributes(lp);
    }

    public LoadProgressDialog(Context context, Object message, boolean cancelable, boolean cancelableOutsite) {
        super(context, R.style.LoaderProgress);
        mContext = context;
        initView(context, message, cancelable, cancelableOutsite);
    }

    public LoadProgressDialog(Context context, boolean cancelable, boolean cancelableOutsite) {
        super(context, R.style.LoaderProgress);
        mContext = context;
        initView(context, null, cancelable, cancelableOutsite);
    }

    private void initView(Context context, Object message, boolean cancelable, boolean cancelableOutsite) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_loaderprogress, null);
        mMessageTextView = (TextView) view.findViewById(R.id.message);
        if (message != null) {
            if (message instanceof Integer) {
                mMessageTextView.setVisibility(View.VISIBLE);
                mMessageTextView.setText((int) message);
            } else if (message instanceof String || message instanceof CharSequence) {
                if (!TextUtils.isEmpty((String) message)) {
                    mMessageTextView.setVisibility(View.VISIBLE);
                    mMessageTextView.setText((String) message);
                } else {
                    mMessageTextView.setVisibility(View.GONE);
                }
            }
        } else {
            mMessageTextView.setVisibility(View.GONE);
        }
        setCancelable(cancelable);
        setCanceledOnTouchOutside(cancelableOutsite);
        setContentView(view);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.dimAmount = 0.2f;
        getWindow().setAttributes(lp);
    }
}
