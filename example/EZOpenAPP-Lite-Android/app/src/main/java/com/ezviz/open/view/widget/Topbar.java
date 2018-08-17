package com.ezviz.open.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.ezviz.open.R;
public class Topbar extends FrameLayout implements OnClickListener {

    private TextView mTitle = null;
    private TextView mTextViewLeft;
    private TextView mTextViewRight;

    private ImageView mImgLeft;

    private ImageView mImgRight;
    private OnTopbarClickListener mListener = null;

    public static interface OnTopbarClickListener {
        void onLeftButtonClicked();

        void onRightButtonClicked();
    }

    public void setOnTopbarClickListener(OnTopbarClickListener listener) {
        mListener = listener;
    }

    public Topbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        Topbar root = (Topbar) LayoutInflater.from(context).inflate(R.layout.topbar, this, true);
        mTitle = (TextView) root.findViewById(R.id.text_title);
        mTextViewRight = (TextView) root.findViewById(R.id.text_right);
        mTextViewLeft = (TextView) root.findViewById(R.id.text_left);
        mImgLeft = (ImageView) root.findViewById(R.id.image_back);
        mImgRight = (ImageView) root.findViewById(R.id.topbar_right_img);
        mImgLeft.setOnClickListener(this);
        mImgRight.setOnClickListener(this);
        mTextViewRight.setOnClickListener(this);
        mTextViewLeft.setOnClickListener(this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Topbar);
        boolean hideLeft = typedArray.getBoolean(R.styleable.Topbar_hideLeft, true);
        boolean hideRight = typedArray.getBoolean(R.styleable.Topbar_hideRight, true);
        Drawable leftIcon = typedArray.getDrawable(R.styleable.Topbar_leftIcon);
        String title = typedArray.getString(R.styleable.Topbar_topBarTitle);
        String left = typedArray.getString(R.styleable.Topbar_topBarLeft);
        String right = typedArray.getString(R.styleable.Topbar_topBarRight);
        Drawable rightIcon = typedArray.getDrawable(R.styleable.Topbar_rightIcon);

        if (!TextUtils.isEmpty(left)) {
            mTextViewLeft.setText(left);
        }

        if (!TextUtils.isEmpty(right)) {
            mTextViewRight.setText(right);
        }
        if (hideLeft) {
            mTextViewLeft.setVisibility(View.GONE);
        }
        if (hideRight) {
            mTextViewRight.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }

        if (leftIcon != null) {
            mImgLeft.setVisibility(View.VISIBLE);
            mImgLeft.setImageDrawable(leftIcon);
        }

        if (rightIcon != null) {
            mImgRight.setVisibility(View.VISIBLE);
            mImgRight.setImageDrawable(rightIcon);
        }

        typedArray.recycle();
    }

    public void setTitle(int resId) {
        mTitle.setText(resId);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void setTitle(String res) {
        mTitle.setText(res);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void setTitle(SpannableString spannableString) {
        mTitle.setText(spannableString);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void setRightText(int resId) {
        mTextViewRight.setText(resId);
        mTextViewRight.setVisibility(View.VISIBLE);
    }

    public void showLeftText() {
        mTextViewLeft.setVisibility(View.VISIBLE);
    }

    public void hideLeftText() {
        mTextViewLeft.setVisibility(View.GONE);
    }

    public void showRightText() {
        mTextViewRight.setVisibility(View.VISIBLE);
    }

    public void hideRightText() {
        mTextViewRight.setVisibility(View.GONE);
    }

    public TextView getLeftText() {
        return mTextViewLeft;
    }

    public TextView getRightText() {
        return mTextViewRight;
    }

    public TextView getTitle() {
        return mTitle;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (mListener != null) {
            if (mTextViewLeft == v || mImgLeft == v) {
                mListener.onLeftButtonClicked();
            } else if (mTextViewRight == v) {
                mListener.onRightButtonClicked();
            }else if (mImgRight == v) {
                mListener.onRightButtonClicked();
            }
        }
    }
}
