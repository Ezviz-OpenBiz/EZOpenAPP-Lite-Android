package com.ezviz.open.view.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;


import com.ezviz.open.view.adapter.BaseLoadMoreRecyclerViewAdapter;

import java.util.List;
import com.ezviz.open.R;
/**
 * Description: 支持下拉刷新，上拉加载更多组件，RecyclerView和SwipeRefreshLayout组合
 * Created by dingwei3
 *
 * @date : 2016/12/15
 */
public class PullRefreshRecyclerView extends RelativeLayout {

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private BaseLoadMoreRecyclerViewAdapter mBaseRecyclerViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    /**
     * 是否正在上拉加载中
     */
    private boolean isLoading = false;
    /**
     * 刷新监听
     */
    private OnAutoRefreshingListner mOnAutoRefreshingListner;
    /**
     * 是否支持下拉刷新
     */
    private boolean mRefreshEnable = true;
    /**
     * 是否支持上拉加载更多
     */
    private boolean mLoadMoreEnable = false;

    public interface OnAutoRefreshingListner {
        /**
         * 下拉刷新触发
         */
        public void onAutoRefreshing();

        /**
         * 上拉加载更多触发
         */
        public void onLoadMore();
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        intitView();
    }

    public PullRefreshRecyclerView(Context context) {
        super(context);
        mContext = context;
        intitView();
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        intitView();
    }

    public void setAdapter(BaseLoadMoreRecyclerViewAdapter adapter) {
        mBaseRecyclerViewAdapter = adapter;
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }


    public void setOnAutoRefreshingListner(OnAutoRefreshingListner onAutoRefreshingListner) {
        mOnAutoRefreshingListner = onAutoRefreshingListner;
    }


    /**
     * 自动刷新
     */
    public void onRefreshing() {
        if (mOnAutoRefreshingListner != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            mBaseRecyclerViewAdapter.isNeedLoadMore = false;
            mOnAutoRefreshingListner.onAutoRefreshing();
        }
    }

    /**
     * 设置是否支持上拉加载更多
     *
     * @param enable
     */
    public void setLoadMoreEnable(boolean enable) {
        mLoadMoreEnable = enable;
        mBaseRecyclerViewAdapter.setNeedLoadMore(enable);
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

    public BaseLoadMoreRecyclerViewAdapter getAdapter() {
        return mBaseRecyclerViewAdapter;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void intitView() {
        PullRefreshRecyclerView root = (PullRefreshRecyclerView) LayoutInflater.from(mContext).inflate(
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
                    mBaseRecyclerViewAdapter.isNeedLoadMore = false;
                    mOnAutoRefreshingListner.onAutoRefreshing();
                }
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (mBaseRecyclerViewAdapter != null) {
            mRecyclerView.setAdapter(mBaseRecyclerViewAdapter);
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");
                if (mLoadMoreEnable) {
                    int lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition + 1 == mBaseRecyclerViewAdapter.getItemCount()) {
                        Log.d("test", "loading executed");
                        boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                        Log.d("test", "loading executed isRefreshing = "+isRefreshing);
                        if (isRefreshing) {
                            if (mBaseRecyclerViewAdapter.getItemViewType(mBaseRecyclerViewAdapter.getItemCount() - 1) == BaseLoadMoreRecyclerViewAdapter.TYPE_FOOTER) {
                                Log.d("test", "loading executed  getItemCount ="+mBaseRecyclerViewAdapter.getItemCount());
                                mBaseRecyclerViewAdapter.isNeedLoadMore = false;
                            }
                            return;
                        }
                        if (!isLoading) {
                            isLoading = true;
                            if (mOnAutoRefreshingListner != null) {
                                mSwipeRefreshLayout.setEnabled(false);
                                mOnAutoRefreshingListner.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    public void onRefreshComplete() {
        if (mSwipeRefreshLayout != null & mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        isLoading = false;
        mSwipeRefreshLayout.setEnabled(true);
        if (mLoadMoreEnable && mBaseRecyclerViewAdapter != null) {
            mBaseRecyclerViewAdapter.isNeedLoadMore = true;
            if (mBaseRecyclerViewAdapter.getItemViewType(mBaseRecyclerViewAdapter.getItemCount() - 1) == BaseLoadMoreRecyclerViewAdapter.TYPE_FOOTER) {
                mBaseRecyclerViewAdapter.notifyItemRemoved(mBaseRecyclerViewAdapter.getItemCount() - 1);
            }
        }
    }

    public void onRefreshComplete(boolean isEnd) {
        if (mSwipeRefreshLayout != null & mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        isLoading = false;
        mSwipeRefreshLayout.setEnabled(true);
        if (mLoadMoreEnable && mBaseRecyclerViewAdapter != null) {
            mBaseRecyclerViewAdapter.isNeedLoadMore = !isEnd;
            if (mBaseRecyclerViewAdapter.getItemViewType(mBaseRecyclerViewAdapter.getItemCount() - 1) == BaseLoadMoreRecyclerViewAdapter.TYPE_FOOTER) {
                mBaseRecyclerViewAdapter.notifyItemRemoved(mBaseRecyclerViewAdapter.getItemCount() - 1);
            }
        }
    }

    public synchronized void setList(List list,boolean isEnd) {
        setLoadMoreEnable(!isEnd);
        mBaseRecyclerViewAdapter.setList(list);
        onRefreshComplete(isEnd);
    }

    public synchronized void appendData(List list,boolean isEnd) {
        setLoadMoreEnable(!isEnd);
        mBaseRecyclerViewAdapter.appendData(list);
        onRefreshComplete(isEnd);
    }
}


