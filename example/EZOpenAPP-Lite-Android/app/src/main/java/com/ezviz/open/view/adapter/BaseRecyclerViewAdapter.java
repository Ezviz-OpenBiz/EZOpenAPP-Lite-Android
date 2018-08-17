package com.ezviz.open.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/15
 */
public abstract class BaseRecyclerViewAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public List<T> mList;

    public Context mContext;

    public BaseRecyclerViewAdapter(Context context){
        mContext = context;
    }

    public void setList(List<T> list) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        this.mList = list;
        notifyDataSetChanged();
    }

    public void appendData(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void add(T object) {
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

    public void add(int position, T object) {
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
        if (mList != null) {
          return mList.size();
        }
        return 0;
    }

    public int getRealItemCount(){
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }
}


