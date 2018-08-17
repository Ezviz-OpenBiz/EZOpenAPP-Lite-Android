package com.ezviz.open.http;

import com.ezviz.open.main.EZOpenApplication;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/6
 */
public class EZOpenAPI {

    private static final String TAG = "EZOpenAPI";

    public static  EZOpenAPIService mEZOpenAPIService;
    static {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(EZOpenApplication.API_URL).
                addConverterFactory(GsonConverterFactory.create())
                .client(EZOpenHttpClient.getInstance().mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        mEZOpenAPIService = retrofit.create(EZOpenAPIService.class);
    }


}


