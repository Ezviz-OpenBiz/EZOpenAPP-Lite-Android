package com.ezviz.open.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import com.ezviz.open.R;
/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/15
 */
public abstract class BaseLoadMoreRecyclerViewAdapter<T> extends   RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<T> mList;
    /**
     * 是否需要上拉加载更多
     */
    public boolean isNeedLoadMore;

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_FOOTER = 1;
    public Context mContext;

    public BaseLoadMoreRecyclerViewAdapter(Context context){
        mContext = context;
    }

    public void setNeedLoadMore(boolean needLoadMore) {
        isNeedLoadMore = needLoadMore;
        notifyDataSetChanged();
    }

    public synchronized void setList(List<T> list) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        this.mList = list;
        notifyDataSetChanged();
    }

    public synchronized void appendData(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public synchronized void add(T object) {
        if (object == null || mList == null) {
            return;
        } else {
            mList.add(object);
            notifyDataSetChanged();
        }
    }

    public T getItem(int position){
        if (mList == null){
            return null;
        }
        if (position > mList.size()){
            return null;
        }
        return mList.get(position);
    }

    public synchronized void add(int position, T object) {
        if (object == null || mList == null || position < 0 || position >= mList.size()) {
            return;
        } else {
            synchronized (mList) {
                mList.add(position, object);
            }
        }
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mList != null) {
            if (isNeedLoadMore){
                count =  mList.size() + 1;
            }else{
                count = mList.size();
            }
        }
        return count;
    }

    public int getRealItemCount(){
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getRealItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * 创建item viewholder
     * @param parent
     * @param viewType
     * @return
     */
    public abstract RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    /**
     * 创建footview viewholder
     * @param parent
     * @param viewType
     * @return
     */
    public RecyclerView.ViewHolder onCreateFootViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_footview_layout, null);
        return new FootViewHolder(view);
    }

    /**
     * 绑定item viewholder
     * @param holder
     * @param position
     */
    public abstract void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * 绑定footview viewholder
     * @param holder
     * @param position
     */
    public void onBindFootViewHolder(RecyclerView.ViewHolder holder, int position){

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return onCreateItemViewHolder(parent,viewType);
        }
        // type == TYPE_FOOTER 返回footerView
        else if (viewType == TYPE_FOOTER) {
            return onCreateFootViewHolder(parent,viewType);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER){
            onBindFootViewHolder(holder,position);
        }else if(getItemViewType(position) == TYPE_ITEM){
            onBindItemViewHolder(holder,position);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }
}


