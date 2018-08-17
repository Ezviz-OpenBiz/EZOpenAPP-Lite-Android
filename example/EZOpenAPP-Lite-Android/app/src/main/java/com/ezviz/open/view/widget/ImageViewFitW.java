package com.ezviz.open.view.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * 根据快读自适应高度的imagview
 */
public class ImageViewFitW extends android.support.v7.widget.AppCompatImageView{

	public ImageViewFitW(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public ImageViewFitW(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable d = getDrawable();
		if (d != null) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = (int) Math.ceil((float) width
					* (float) d.getIntrinsicHeight()
					/ (float) d.getIntrinsicWidth());
			setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
