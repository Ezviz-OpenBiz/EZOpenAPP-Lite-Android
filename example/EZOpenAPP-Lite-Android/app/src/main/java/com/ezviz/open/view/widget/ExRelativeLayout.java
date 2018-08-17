package com.ezviz.open.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ExRelativeLayout extends RelativeLayout {

    private int mHeight;
    private int mLastMargin = 0;

    public ExRelativeLayout(Context context) {
        this(context, null);
    }

    public ExRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newHeight = MeasureSpec.getSize(heightMeasureSpec);
//        mHeight = newHeight;
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
}

    public void setBottomMargin(float x){
        if(mHeight == 0) {
            mHeight = getHeight();
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.bottomMargin = -(int)(x * mHeight);
        mLastMargin = params.bottomMargin;
        setLayoutParams(params);
    }

    public void setTopMargin(float x){
        if(mHeight == 0) {
            mHeight = getHeight();
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.topMargin = -(int)(x * mHeight);
        mLastMargin = params.topMargin;
        setLayoutParams(params);
    }

    public int geTopMargin(){
        return mLastMargin;
    }

    public int getBottomMargin(){return mLastMargin;};
}