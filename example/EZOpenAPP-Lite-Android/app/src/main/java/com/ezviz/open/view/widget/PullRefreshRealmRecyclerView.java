package com.ezviz.open.view.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;


import com.ezviz.open.view.adapter.BaseRealmRecyclerViewAdapter;
import com.ezviz.open.R;
/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/15
 */
public class PullRefreshRealmRecyclerView<T> extends RelativeLayout {

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private BaseRealmRecyclerViewAdapter mBaseRealmRecyclerViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private OnAutoRefreshingListner mOnAutoRefreshingListner;
    /**
     * 是否支持下拉刷新
     */
    private boolean mRefreshEnable = true;

    public interface OnAutoRefreshingListner {
        public void onAutoRefreshing();
    }

    public PullRefreshRealmRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        intitView();
    }

    public PullRefreshRealmRecyclerView(Context context) {
        super(context);
        mContext = context;
        intitView();
    }

    public PullRefreshRealmRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        intitView();
    }

    public void setAdapter(BaseRealmRecyclerViewAdapter adapter) {
        mBaseRealmRecyclerViewAdapter = adapter;
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }


    public void setOnAutoRefreshingListner(OnAutoRefreshingListner onAutoRefreshingListner) {
        mOnAutoRefreshingListner = onAutoRefreshingListner;
    }

    public void onRefreshing() {
        if (mOnAutoRefreshingListner != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            mOnAutoRefreshingListner.onAutoRefreshing();
        }
    }


    /**
     * 设置是否支持下拉刷新
     *
     * @param enable
     */
    public void setRefreshEnable(boolean enable) {
        mRefreshEnable = enable;
        mSwipeRefreshLayout.setEnabled(mRefreshEnable);
    }

    public BaseRealmRecyclerViewAdapter getAdapter() {
        return mBaseRealmRecyclerViewAdapter;
    }


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void intitView() {
        PullRefreshRealmRecyclerView root = (PullRefreshRealmRecyclerView) LayoutInflater.from(mContext).inflate(
                R.layout.pullrefresh_layout, this, true);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        // 设置卷内的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_item_text_select,
                R.color.main_item_text_select, R.color.main_item_text_select,
                R.color.main_item_text_select);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mOnAutoRefreshingListner != null) {
                    mOnAutoRefreshingListner.onAutoRefreshing();
                }
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (mBaseRealmRecyclerViewAdapter != null) {
            mRecyclerView.setAdapter(mBaseRealmRecyclerViewAdapter);
        }
    }

    public void onRefreshComplete() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}


