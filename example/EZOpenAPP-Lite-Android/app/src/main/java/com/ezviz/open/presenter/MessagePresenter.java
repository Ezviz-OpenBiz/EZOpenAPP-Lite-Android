package com.ezviz.open.presenter;

import com.ezviz.open.view.MessageView;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAlarmInfo;

import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Description: 报警消息相关操作
 * Created by dingwei3
 *
 * @date : 2016/12/15
 */
public class MessagePresenter extends BasePresenter{

    private int pageSize = 20;
    /**
     * 时间区间为起始时间倒推24小时*7天
     */
    private long Interval_Time = 24*60*60*1000*7;
    private MessageView mView;
    private long mEndTime = 0;
    private Calendar mStartCalendar;
    private Calendar mEndCalendar;
    private int index;

    public MessagePresenter(MessageView baseView){
        mView = baseView;
    }


    public void onRefresh(){
        index = 0;
        mEndTime = System.currentTimeMillis();
        mEndCalendar = Calendar.getInstance();
        mEndCalendar.setTimeInMillis(mEndTime);
        mStartCalendar = Calendar.getInstance();
        mStartCalendar.setTimeInMillis(mEndTime-Interval_Time);
        Observable.create(new Observable.OnSubscribe<List<EZAlarmInfo>>() {
            @Override
            public void call(Subscriber<? super List<EZAlarmInfo>> subscriber) {
                try {
                    List<EZAlarmInfo> alarmInfoList = EZOpenSDK.getAlarmList("",index++,pageSize,mStartCalendar,mEndCalendar);
                    if (alarmInfoList != null && alarmInfoList.size() == 0){
                        // TODO: 2017/2/6 7天内均无报警消息
                    }
                    subscriber.onNext(alarmInfoList);
                } catch (BaseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Action1<List<EZAlarmInfo>>() {
            @Override
            public void call(List<EZAlarmInfo> list) {
                boolean isEnd = true;
                if (list != null && list.size() >= pageSize){
                    isEnd = false;
                }
                mView.refreshFinish(list,isEnd);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                onErrorBaseHandle(((BaseException) throwable).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
                mView.onError();
            }
        });
    }

    /**
     * 加载更多
     */
    public void onLoadMore(){
        Observable.create(new Observable.OnSubscribe<List<EZAlarmInfo>>() {
            @Override
            public void call(Subscriber<? super List<EZAlarmInfo>> subscriber) {
                try {
                    List<EZAlarmInfo> alarmInfoList = EZOpenSDK.getAlarmList("",index,pageSize,mStartCalendar,mEndCalendar);
                    subscriber.onNext(alarmInfoList);
                } catch (BaseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Action1<List<EZAlarmInfo>>() {
            @Override
            public void call(List<EZAlarmInfo> list) {
                boolean isEnd = true;
                if (list != null && list.size() > 0){
                    index++;
                }
                if (list != null && list.size() >= pageSize){
                    isEnd = false;
                }
                mView.loadFinish(list,isEnd);

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mView.onError();
                onErrorBaseHandle(((BaseException) throwable).getErrorCode());
                // TODO: 2017/2/15 除公共错误码之后的错误码处理
            }
        });
    }

}


