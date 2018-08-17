package com.ezviz.open.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.ezviz.open.utils.DateUtil;
import com.videogo.openapi.bean.EZAlarmInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/22
 */
public class EZMessageItemDecoration  extends RecyclerView.ItemDecoration {
    private Context mContext;
    private List<EZAlarmInfo> mDatas;
    private Paint mPaint;
    private Rect mBounds;//用于存放测量文字Rect

    private LayoutInflater mInflater;

    private int mTitleHeight;//title的高
    private static int COLOR_TITLE_BG = Color.parseColor("#FFF5F5F5");
    private static int COLOR_TITLE_FONT = Color.parseColor("#FF323232");
    private static int mTitleFontSize;//title字体大小
    private int mTitleLeftPadding;

    private int mHeaderViewCount = 0;


    public EZMessageItemDecoration(Context context) {
        super();
        mContext = context;
        mPaint = new Paint();
        mBounds = new Rect();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics());
        mTitleLeftPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
        mInflater = LayoutInflater.from(context);
    }


    public EZMessageItemDecoration setmTitleHeight(int mTitleHeight) {
        this.mTitleHeight = mTitleHeight;
        return this;
    }


    public EZMessageItemDecoration setColorTitleBg(int colorTitleBg) {
        COLOR_TITLE_BG = colorTitleBg;
        return this;
    }

    public EZMessageItemDecoration setColorTitleFont(int colorTitleFont) {
        COLOR_TITLE_FONT = colorTitleFont;
        return this;
    }

    public EZMessageItemDecoration setTitleFontSize(int mTitleFontSize) {
        mPaint.setTextSize(mTitleFontSize);
        return this;
    }

    public EZMessageItemDecoration setData(List<EZAlarmInfo> list) {

        if (mDatas != null){
            mDatas.clear();
        }else{
            mDatas = new ArrayList<EZAlarmInfo>();
        }
        if (list != null){
            mDatas.addAll(list);
        }
        return this;
    }

    public EZMessageItemDecoration appendData(List<EZAlarmInfo> list) {
       if (mDatas != null){
           mDatas.addAll(list);
       }else{
           mDatas = new ArrayList<EZAlarmInfo>();
           if (list != null){
               mDatas.addAll(list);
           }
       }
        return this;
    }

    public int getHeaderViewCount() {
        return mHeaderViewCount;
    }

    public EZMessageItemDecoration setHeaderViewCount(int headerViewCount) {
        mHeaderViewCount = headerViewCount;
        return this;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            position -= getHeaderViewCount();
            //pos为1，size为1，1>0? true
            if (mDatas == null || mDatas.isEmpty() || position > mDatas.size() - 1 || position < 0) {
                continue;//越界
            }
            //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
            if (position > -1) {
                if (position == 0) {//等于0肯定要有title的
                    drawTitleArea(c, left, right, child, params, position);

                } else {//其他的通过判断
                    if (null != DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position).getAlarmStartTime())
                            && !DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position).getAlarmStartTime()).equals(DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position-1).getAlarmStartTime()))) {
                        //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                        drawTitleArea(c, left, right, child, params, position);
                    } else {
                        //none
                    }
                }
            }
        }
    }

    /**
     * 绘制Title区域背景和文字的方法
     *
     * @param c
     * @param left
     * @param right
     * @param child
     * @param params
     * @param position
     */
    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        /*
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;*/

        mPaint.getTextBounds(DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position).getAlarmStartTime()), 0, DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position).getAlarmStartTime()).length(), mBounds);
        c.drawText(DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position).getAlarmStartTime()), child.getPaddingLeft()+mTitleLeftPadding, child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, final RecyclerView parent, RecyclerView.State state) {//最后调用 绘制在最上层
        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        position -= getHeaderViewCount();
        //pos为1，size为1，1>0? true
        if (mDatas == null || mDatas.isEmpty() || position > mDatas.size() - 1 || position < 0) {
            return;//越界
        }

        String tag = DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position).getAlarmStartTime());
        //View child = parent.getChildAt(pos);
        View child = parent.findViewHolderForLayoutPosition(position + getHeaderViewCount()).itemView;//出现一个奇怪的bug，有时候child为空，所以将 child = parent.getChildAt(i)。-》 parent.findViewHolderForLayoutPosition(pos).itemView

        boolean flag = false;//定义一个flag，Canvas是否位移过的标志
        if ((position + 1) < mDatas.size()) {//防止数组越界（一般情况不会出现）
            if (null != tag && !tag.equals(DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position+1).getAlarmStartTime()))) {//当前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了
                Log.d("zxt", "onDrawOver() called with: c = [" + child.getTop());//当getTop开始变负，它的绝对值，是第一个可见的Item移出屏幕的距离，
                if (child.getHeight() + child.getTop() < mTitleHeight) {//当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，我们也该开始做悬浮Title的“交换动画”
                    c.save();//每次绘制前 保存当前Canvas状态，
                    flag = true;

                    //一种头部折叠起来的视效，个人觉得也还不错~
                    //可与123行 c.drawRect 比较，只有bottom参数不一样，由于 child.getHeight() + child.getTop() < mTitleHeight，所以绘制区域是在不断的减小，有种折叠起来的感觉
                    //c.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + child.getHeight() + child.getTop());

                    //类似饿了么点餐时,商品列表的悬停头部切换“动画效果”
                    //上滑时，将canvas上移 （y为负数） ,所以后面canvas 画出来的Rect和Text都上移了，有种切换的“动画”感觉
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }
            }
        }
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
        c.drawText(tag, child.getPaddingLeft()+mTitleLeftPadding,
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);
        if (flag)
            c.restore();//恢复画布到之前保存的状态
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //super里会先设置0 0 0 0
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        position -= getHeaderViewCount();
        if (mDatas == null || mDatas.isEmpty() || position > mDatas.size() - 1) {//pos为1，size为1，1>0? true
            return;//越界
        }
        //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
        if (position > -1) {
            EZAlarmInfo alarmInfo = mDatas.get(position);
            //等于0肯定要有title的,
            // 2016 11 07 add 考虑到headerView 等于0 也不应该有title
            // 2016 11 10 add 通过接口里的isShowSuspension() 方法，先过滤掉不想显示悬停的item
                if (position == 0) {
                    outRect.set(0, mTitleHeight, 0, 0);
                } else {//其他的通过判断
                    if (null != DateUtil.getMessageItemDataDisplay(mContext,alarmInfo.getAlarmStartTime()) && !DateUtil.getMessageItemDataDisplay(mContext,alarmInfo.getAlarmStartTime()).equals(DateUtil.getMessageItemDataDisplay(mContext,mDatas.get(position-1).getAlarmStartTime()))) {
                        //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                        outRect.set(0, mTitleHeight, 0, 0);
                    }
                }
        }
    }

}

