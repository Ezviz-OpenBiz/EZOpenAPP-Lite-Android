/* 
 * @ProjectName VideoGo
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName RingView.java
 * @Description 这里对文件进行描述
 * 
 * @author chenxingyf1
 * @data 2014-6-19
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.ezviz.open.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ezviz.open.utils.EZOpenUtils;

//import com.videogo.util.LogUtil;

public class RingView extends View implements Runnable {

    private final Paint mPaint;
    private final Context mContext;
    private float mMinRadius;
    private float mDistance;
    private float mCurrentRadius;
    
    public RingView(Context context) {
        this(context, null);
    }

    public RingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true); //消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
        mPaint.setStrokeWidth(EZOpenUtils.dip2px(mContext, 1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制圆环
        if(mCurrentRadius > 0) {
            canvas.drawCircle(this.getLeft() + this.getWidth()/2, this.getTop() + this.getHeight()/2, 
                    mCurrentRadius, mPaint);
        }
        
        super.onDraw(canvas);
    }
    
    public void setMinRadiusAndDistance(float minRadius, int distance) {   
        mMinRadius = minRadius;  
        mCurrentRadius = mMinRadius + mDistance;
        mDistance = distance;
        //LogUtil.infoLog("RingView", "minRadius:" + minRadius);
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        while(mMinRadius > 0 && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(300);
            }
            catch(Exception e) {
                Thread.currentThread().interrupt();
            }
            int maxRadius = getHeight()/2;
            if(mCurrentRadius + mDistance < maxRadius) {
                mCurrentRadius += mDistance;
            } else {
                mCurrentRadius = mMinRadius + mDistance;
            }
            int alpha = (int)(255*(maxRadius - mCurrentRadius + mDistance)/maxRadius);
            //LogUtil.infoLog("RingView", "mCurrentRadius:" + mCurrentRadius + ", alpha:" + alpha + ", maxRadius:" + maxRadius);
            mPaint.setARGB(alpha, 209 ,216, 219);
            postInvalidate();
        }
    }
}

