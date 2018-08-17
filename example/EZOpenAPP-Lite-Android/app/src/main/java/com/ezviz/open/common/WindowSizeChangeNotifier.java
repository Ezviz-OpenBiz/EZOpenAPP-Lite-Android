package com.ezviz.open.common;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class WindowSizeChangeNotifier { 
    
    private View mChildOfContent;
    private Point mPreviousPoint = null;
    private OnWindowSizeChangedListener mListener = null;
    public static interface OnWindowSizeChangedListener {
    	void onWindowSizeChanged(int w, int h, int oldW, int oldH);
    }
  
    public WindowSizeChangeNotifier(Activity activity, OnWindowSizeChangedListener onWindowSizeChangedListener) {
    	mListener = onWindowSizeChangedListener;
    	FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);  
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {  
                possiblyResizeChildOfContent();  
            }  
        });   
    }  
  
    private void possiblyResizeChildOfContent() {  
    	Point pointNow = computeUsablePoint();
    	if(mPreviousPoint == null) {
    		mPreviousPoint = pointNow;
    		return;
    	}
        if (pointNow.x != mPreviousPoint.x || pointNow.y != mPreviousPoint.y) { 
        	if(mListener != null) {
        		mListener.onWindowSizeChanged(pointNow.x, pointNow.y, mPreviousPoint.x, mPreviousPoint.y);
        	}  	
        	mPreviousPoint = pointNow; 
            
        }  
    }  
  
    private Point computeUsablePoint() {
    	Point p = new Point();
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);  
        p.x = r.width();
        p.y = r.height();
        return p;  
    }  
    
}
