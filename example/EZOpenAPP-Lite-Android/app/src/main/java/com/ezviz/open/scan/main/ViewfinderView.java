/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ezviz.open.scan.main;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.ezviz.open.scan.camera.CameraManager;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import com.ezviz.open.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {
        0, 64, 128, 192, 255, 255, 192, 128, 64, 0};

    private static final long ANIMATION_DELAY = 20L;

    private static final int CURRENT_POINT_OPACITY = 0xA0;

    private static final int MAX_RESULT_POINTS = 20;

    private static final int POINT_SIZE = 6;

    private static final int OPAQUE = 0xFF;

    boolean isFirst;

    /** 中间滑动线的最顶端位置 */
    private int slideTop;

    /** 中间那条线每次刷新移动的距 */
    private static final int SPEEN_DISTANCE = 1;

    /** 四个绿色边角对应的长度 */
    private int screenRate;

    /** 四个绿色边角对应的宽度 */
    private static final int CORNER_WIDTH = 5;

    private CameraManager cameraManager;

    private final Paint paint;

    private Bitmap resultBitmap;

    private final int maskColor;

    private final int resultColor;

    private final int laserColor;

    private final int resultPointColor;

    // private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;

    private Collection<ResultPoint> lastPossibleResultPoints;

    private Paint mTagbarPaint = new Paint();

    private Context mContext;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        // frameColor = resources.getColor(R.color.viewfinder_frame);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        // scannerAlpha = 0;
        possibleResultPoints = new ArrayList<ResultPoint>(5);
        lastPossibleResultPoints = null;

        float density = context.getResources().getDisplayMetrics().density;
        // 将像素转换成dp
        screenRate = (int) (20 * density);
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null)
            return;
        Rect frame = cameraManager.getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // 初始化中间线滑动的最上边和最下边
        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top;
        }

        // Draw the exterior (i.e. outside the framing rect) darkened
        // 画四周的蒙版共四个
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        paint.setAlpha(154);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            // 如果显示的是扫描的结果，则显示的是拍摄到的图片
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            // Draw a one pixel solid black border inside the framing rect
            // 绘制一个像素的黑色边框
            // paint.setColor(frameColor);
            // canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 1, paint);
            // canvas.drawRect(frame.left, frame.top + 2, frame.left + 1, frame.bottom - 1, paint);
            // canvas.drawRect(frame.right - 1, frame.top, frame.right, frame.bottom - 1, paint);
            // canvas.drawRect(frame.left, frame.bottom, frame.right + 1, frame.bottom + 1, paint);

            // 画扫描框边上的角，总共8个部分
            paint.setColor(laserColor);
            canvas.drawRect(frame.left, frame.top, frame.left + screenRate, frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top + screenRate, paint);
            canvas.drawRect(frame.right - screenRate, frame.top, frame.right, frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top + screenRate, paint);
            canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left + screenRate, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom - screenRate, frame.left + CORNER_WIDTH, frame.bottom, paint);
            canvas.drawRect(frame.right - screenRate, frame.bottom - CORNER_WIDTH, frame.right, frame.bottom, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - screenRate, frame.right, frame.bottom, paint);

            // 画四周的方块共三个
            paint.setColor(Color.WHITE);
            paint.setStyle(Style.FILL);// 实心矩形框
            canvas.drawRect(frame.left + 25, frame.top + 25, frame.left + 45, frame.top + 45, paint);
            canvas.drawRect(frame.left + 25, frame.bottom - 25, frame.left + 45, frame.bottom - 45, paint);
            canvas.drawRect(frame.right - 25, frame.top + 25, frame.right - 45, frame.top + 45, paint);

            // 画四周的方块共三个
            paint.setColor(Color.WHITE);
            paint.setAlpha(255);
            paint.setStyle(Style.STROKE);// 空心矩形框
            paint.setStrokeWidth((float) 2);
            canvas.drawRect(frame.left + 20, frame.top + 20, frame.left + 50, frame.top + 50, paint);
            canvas.drawRect(frame.left + 20, frame.bottom - 20, frame.left + 50, frame.bottom - 50, paint);
            canvas.drawRect(frame.right - 20, frame.top + 20, frame.right - 50, frame.top + 50, paint);

            // Draw a red "laser scanner" line through the middle to show decoding is active
            // 绘制一个红色的激光线
            // 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
            slideTop += SPEEN_DISTANCE;
            if (slideTop >= frame.bottom) {
                slideTop = frame.top;
            }
            paint.setColor(laserColor);
            paint.setStyle(Style.FILL);
            paint.setStrokeWidth((float) 1);
            // paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            //
            // scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            canvas.drawRect(frame.left + 2, slideTop - 1, frame.right - 1, slideTop + 1, paint);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                }
            }
            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     * 
     * @param barcode
     *            An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        // List<ResultPoint> points = possibleResultPoints;
        // synchronized (points) {
        possibleResultPoints.add(point);
        // int size = points.size();
        // if (size > MAX_RESULT_POINTS) {
        // // trim it
        // points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
        // }
        // }
    }

}
