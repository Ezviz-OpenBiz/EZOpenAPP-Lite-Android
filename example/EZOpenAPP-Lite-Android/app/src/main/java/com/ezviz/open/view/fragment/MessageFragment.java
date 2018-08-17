package com.ezviz.open.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.ezviz.open.glide.EncryptUrlInfo;
import com.ezviz.open.presenter.MessagePresenter;
import com.ezviz.open.utils.EZLog;
import com.ezviz.open.utils.EZOpenUtils;
import com.ezviz.open.utils.JsonUtils;
import com.ezviz.open.view.MessageView;
import com.ezviz.open.view.adapter.MessageAdapter;
import com.ezviz.open.view.avctivity.PlayBackActivity;
import com.ezviz.open.view.widget.EZMessageItemDecoration;
import com.ezviz.open.view.widget.ImageViewFixedRatio;
import com.ezviz.open.view.widget.PullRefreshRecyclerView;
import com.ezviz.open.view.widget.Topbar;
import com.videogo.openapi.bean.EZAlarmInfo;
import java.util.List;
import com.ezviz.open.R;
/**
 * Description:消息界面
 * Created by dingwei3
 *
 * @date : 2016/12/12
 */
public class MessageFragment extends BaseLazyFragment implements MessageView, PullRefreshRecyclerView.OnAutoRefreshingListner, MessageAdapter.onItemClickListener {
    private static final String TAG = "MessageFragment";
    private MessagePresenter mMessagePresenter;
    private MessageAdapter mMessageAdapter;
    private EZMessageItemDecoration mEZMessageItemDecoration;
    private Topbar mTopbar;
    private  PullRefreshRecyclerView mPullRefreshRecyclerView;
    public MessageFragment() {
        mMessagePresenter = new MessagePresenter(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,null);
//        mTopbar = (Topbar) view.findViewById(R.id.message_topbar);
//        mTopbar.setTitle(R.string.my_message);
        mMessageAdapter = new MessageAdapter(mContext);
        mEZMessageItemDecoration = new EZMessageItemDecoration(mContext);
        mPullRefreshRecyclerView = (PullRefreshRecyclerView) view.findViewById(R.id.message_list_layout);
        mPullRefreshRecyclerView.setAdapter(mMessageAdapter);
        mPullRefreshRecyclerView.setOnAutoRefreshingListner(this);
        mMessageAdapter.setOnItemClickListener(this);
        mPullRefreshRecyclerView.getRecyclerView().addItemDecoration(mEZMessageItemDecoration);
        return view;
    }

    @Override
    protected void lazyLoad() {
        mPullRefreshRecyclerView.onRefreshing();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void loadFinish(List<EZAlarmInfo> list,boolean isEnd) {
        for (int i = 0;i<list.size();i++){
            EZLog.d(TAG,"EZAlarmInfo = "+ JsonUtils.toJson(list.get(i)));
        }
        EZLog.d(TAG,"EZAlarmInfo loadFinish isEnd = " + isEnd);
        mEZMessageItemDecoration.appendData(list);
        mPullRefreshRecyclerView.appendData(list,isEnd);
    }

    @Override
    public void refreshFinish(List<EZAlarmInfo> list,boolean isEnd) {
        mEZMessageItemDecoration.setData(list);
        mPullRefreshRecyclerView.setList(list,isEnd);
    }

    @Override
    public void onError() {
        mPullRefreshRecyclerView.onRefreshComplete();
    }


    @Override
    public void onAutoRefreshing() {
        mMessagePresenter.onRefresh();
    }

    @Override
    public void onLoadMore() {
        mMessagePresenter.onLoadMore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(EZAlarmInfo alarmInfo) {
        if (alarmInfo.getRecState() != 0){
            Intent intent = new Intent(mActivity, PlayBackActivity.class);
            intent.putExtra(EZOpenUtils.EXTRA_ALARM_INFO, alarmInfo);
            mActivity.startActivity(intent);
        }else{
            showCover(alarmInfo);
        }
    }

    private void showCover(EZAlarmInfo alarmInfo) {
        final Dialog mDialog = new Dialog(mActivity, R.style.CommonDialog);
        ImageViewFixedRatio imageViewFixedRatio = new ImageViewFixedRatio(mContext,0.562f);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageViewFixedRatio.setLayoutParams(layoutParams);
        boolean isEncrypt = EZOpenUtils.isEncrypt(alarmInfo.getAlarmPicUrl());
        EncryptUrlInfo encryptURLInfo = new EncryptUrlInfo(alarmInfo.getDeviceSerial(),alarmInfo.getAlarmPicUrl(),isEncrypt);
        Glide.with(mContext)
                .from(EncryptUrlInfo.class)
                .centerCrop().crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(isEncrypt?R.drawable.alarm_encrypt_image_mid:R.drawable.default_cover_big)
                .load(encryptURLInfo)
                .into(imageViewFixedRatio);
        imageViewFixedRatio.setClickable(true);
        imageViewFixedRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mDialog.isShowing()) {
                    mDialog.cancel();
                }
            }
        });
        mDialog.setContentView(imageViewFixedRatio);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        wlp.width = (int) (dm.widthPixels);
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        mDialog.show();
    }
}
