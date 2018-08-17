package com.ezviz.open.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ezviz.open.view.avctivity.RootActivity;
import com.ezviz.open.view.widget.LoadProgressDialog;
import java.util.concurrent.atomic.AtomicBoolean;
import com.ezviz.open.R;
/**
 * Description:懒加载fragment
 * Created by dingwei3
 *
 * @date : 2016/12/12
 */
public abstract class BaseLazyFragment extends BaseFragment {

    protected boolean isVisible;
    protected LayoutInflater mInflater;
    protected boolean isPrepared;
    protected AtomicBoolean isInit = new AtomicBoolean(false);
    protected RootActivity mActivity;

    public Context mContext;
    protected View mRootView;
    private LoadProgressDialog mLoadProgressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (RootActivity) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        if (mRootView == null) {
            mRootView = initView(inflater, container, savedInstanceState);
        }
        // 缓存的viewiew需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        isPrepared = true;
        if (isPrepared){
            if (!isInit.getAndSet(true)){
                lazyLoad();
            }
        }
        return mRootView;
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initVisibleView();
        } else {
            initInVisibleView();
        }
    }

    /**
     * 切换底部bottom，不经过生命周期，监听hide，可见时进行处理
     *
     * @see
     * @since V1.8.2
     */
    public void initVisibleView() {
    }

    ;

    /**
     * 切换底部bottom，不经过生命周期，监听hide，不可见时进行处理
     *
     * @see
     * @since V1.8.2
     */
    public void initInVisibleView() {
    }

    ;

    protected void onVisible() {
        if (isPrepared){
            if (!isInit.getAndSet(true)){
                lazyLoad();
            }
        }
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

}
