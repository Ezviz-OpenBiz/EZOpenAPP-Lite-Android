package com.ezviz.open.main;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;


import com.ezviz.open.utils.DataManager;
import com.ezviz.open.view.avctivity.MainActivity;
import com.ezviz.opensdk.auth.OnAuthCallBack;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZAccessToken;

import io.realm.Realm;
import com.ezviz.open.R;
/**
 * Description:
 * Created by dingwei3
 *
 * @date : 2016/12/3
 */
public class EZOpenApplication extends MultiDexApplication {
    public static final boolean DEBUG = false;
    public static final String TAG = "EZOpenApplication";
    public static String APP_KEY = "";
    public static String API_URL = "https://open.ys7.com";
    public static EZOpenApplication mEZOpenApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mEZOpenApplication = this;
        init();
        Realm.init(this);
    }

    public void init() {
        Log.i(TAG, "init debug = " + getResources().getBoolean(R.bool.debug));
        EZOpenSDK.showSDKLog(getResources().getBoolean(R.bool.debug));
        EZOpenSDK.initSDK(this, APP_KEY);
        DataManager.init(this);
        EZOpenSDK.setAuthCallBack(new OnAuthCallBack() {
            @Override
            public void onAuthSuccessCallBack(EZAccessToken ezAccessToken) {
                Intent toIntent = new Intent(mEZOpenApplication, MainActivity.class);
                toIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toIntent);
            }

            @Override
            public void onNeedAuthAccessToken() {

            }
        });
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}


