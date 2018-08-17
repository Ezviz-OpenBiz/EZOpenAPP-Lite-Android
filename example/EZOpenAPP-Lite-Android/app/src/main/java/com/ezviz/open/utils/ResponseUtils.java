package com.ezviz.open.utils;

import android.content.Context;

import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2017/1/12
 */
public class ResponseUtils {
    public static void pareException(Context context,int code){
        switch (code){
            case 10002:
                //accessToken异常或过期
                break;
        }
    }

    public static void pareException(Context context,BaseException baseException){
        switch (baseException.getErrorCode()){
            case ErrorCode.ERROR_WEB_SESSION_ERROR:
                //accessToken异常或过期
                break;
        }
    }
}


