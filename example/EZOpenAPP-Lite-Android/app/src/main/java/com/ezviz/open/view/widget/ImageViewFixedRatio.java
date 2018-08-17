package com.ezviz.open.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import com.ezviz.open.R;


public class ImageViewFixedRatio extends android.support.v7.widget.AppCompatImageView {

    private float mRatio = 1.0f;
    private Paint mPaint = new Paint();
    protected RectF mRect = new RectF();

    public ImageViewFixedRatio(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ImageViewFixedRatio(Context context,float ratio ) {
        super(context);
        this.mRatio = ratio;
    }

    public ImageViewFixedRatio(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageViewFixedRatio);
        mRatio = typedArray.getFloat(R.styleable.ImageViewFixedRatio_ratio, 1.0f);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * mRatio);
        setMeasuredDimension(width, height);
        mRect.set(0, 0, width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub;
        super.onDraw(canvas);
        //Paint paint=new Paint();
        mPaint.setColor(0x14000000);
        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(2);
        canvas.drawRect(mRect, mPaint);
    }

    public float getRatio() {
        return mRatio;
    }
}
