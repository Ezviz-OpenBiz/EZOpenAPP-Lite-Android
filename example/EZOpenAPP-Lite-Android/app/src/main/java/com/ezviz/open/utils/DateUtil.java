package com.ezviz.open.utils;

import android.content.Context;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import com.ezviz.open.R;
/**
 * Description: 时间工具类
 * Created by dingwei3
 *
 * @date : 2016/12/20
 */
public class DateUtil {
    public static final SimpleDateFormat simpleDateFormat_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat simpleDateFormat_HHmmss = new SimpleDateFormat("HH:mm:ss");
    public static String getTimeDesplay(){
        return "";
    }


    /**
     * @param context
     * @param time 毫秒
     * @return
     */
    public static String getMessageDataTimeDisplay(Context context,Object time){
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int threeHour = 3*60 * tMin;
        int tDay = 24 * tHour;
        long currentTime = System.currentTimeMillis();
        long mTime = 0l;
        Date date = new Date();
        Date currentDate = new Date();
        Date yesterday = new Date();
        Date beforeYes = new Date();
        SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
        if (time instanceof Long) {
            mTime = (Long) time;
            date.setTime(mTime);
        } else if (time instanceof String) {
            // 注意format的格式要与日期String的格式相匹配
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse((String) time);
                mTime = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String str = "";
        try {
            yesterday.setTime(todayDf.parse(todayDf.format(currentDate)).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        beforeYes.setTime(yesterday.getTime() - tDay);
        long timeGap = currentTime - mTime;
        long beforeMin = timeGap / 1000 / 60;
        long beforeHour = beforeMin / 60;
        if (timeGap < tMin) {
            str = context.getResources().getString(R.string.just_now);
        } else if (timeGap < tHour) {
            str = String.format(context.getResources().getString(R.string.before_minute), beforeMin);
        } else if (timeGap >= tHour && timeGap < threeHour) {
            str = String.format(context.getResources().getString(R.string.before_hours), beforeHour);
        }else {
            // SimpleDateFormat sdf= new SimpleDateFormat("MM-dd  HH:mm");
            // str = sdf.format(new Date(time));
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(currentTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mTime);
            if (now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                str = String.format(context.getResources().getString(R.string.custum_date_no_year),
                        (calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH));
            } else {
                str = String.format(context.getResources().getString(R.string.custum_date),
                        calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1),
                        calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
        return str;
    }


    /**
     * 根据时间得到消息显示时间
     * @param context
     * @param time "2015-11-11 12:23:20"
     * @return
     */
    public static String getMessageItemDataDisplay(Context context,String time) {
        long currentTime = System.currentTimeMillis();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timeStr = todayDf.format(date);
        String todayStr = todayDf.format(new Date(currentTime));
        if (timeStr.equals(todayStr)){
            return context.getResources().getString(R.string.today);
        }
        return timeStr;
    }

    /**
     *
     * @param time 毫秒
     * @return
     */
    public static String getDataDisplay(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String todayStr = sdf.format(new Date(time));
        return todayStr;
    }

    /**
     *
     * @param time 毫秒
     * @return
     */
    public static String getRecordTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String string = sdf.format(new Date(time));
        return string;
    }

    /**
     * 播放OSD时间获取
     * @param OSDTime
     * @return
     */
    public static String OSD2Time(Calendar OSDTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        return sdf.format(Long.valueOf(OSDTime.getTimeInMillis()));
    }


    /**
     *
     * @param time 毫秒
     * @return
     */
    public static String getDataTime(long time,SimpleDateFormat simpleDateFormat) {
        String todayStr = simpleDateFormat.format(new Date(time));
        return todayStr;
    }

    public static Calendar parseTimeToCalendar(String strTime) {
        if(strTime == null) {
            return null;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;

            try {
                date = sdf.parse(strTime);
            } catch (ParseException var4) {
                var4.printStackTrace();
            }

            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(date);
            return timeCalendar;
        }
    }

    /**
     * @param time 毫秒
     * @return
     */
    public static String parseTimeToString(long time) {
        EZLog.d("parseTimeToString","parseTimeToString = "+time);
        if (time <= 0){
            time = 0;
        }
        SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss",Locale.getDefault());
        mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date  mDate = new Date();
        mDate.setTime(time);
        return mDateFormat.format(mDate);
    }
}


