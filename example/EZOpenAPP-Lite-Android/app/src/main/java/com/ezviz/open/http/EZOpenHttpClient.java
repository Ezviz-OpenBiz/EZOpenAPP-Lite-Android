package com.ezviz.open.http;

import android.content.Context;

import com.ezviz.open.main.EZOpenApplication;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2017/1/12
 */
public class EZOpenHttpClient {
    private final static String TAG = "EZOpenHttpClient";
    public OkHttpClient mOkHttpClient;
    private static EZOpenHttpClient mEZOpenHttpClient;

    /**
     * 根据类型生成并获取实例
     */
    public static EZOpenHttpClient getInstance() {
        if (mEZOpenHttpClient == null) {
            synchronized (EZOpenHttpClient.class) {
                if (mEZOpenHttpClient == null) {
                    mEZOpenHttpClient =  new EZOpenHttpClient(EZOpenApplication.mEZOpenApplication.getApplicationContext());
                }
            }
        }
        return mEZOpenHttpClient;
    }

    public EZOpenHttpClient(Context context) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        if (EZOpenApplication.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
        }
        mOkHttpClient = okHttpClientBuilder.build();
    }

}


