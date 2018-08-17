package com.ezviz.open.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.ezviz.opensdk.base.data.CameraManager;
import com.ezviz.opensdk.base.data.DeviceManager;
import com.ezviz.opensdk.http.bean.CameraInfoEx;
import com.ezviz.opensdk.http.bean.DeviceInfoEx;

/**
 * TODO description
 *
 * @author dingwei3
 * @date 2017/11/21
 */

public class SharedPreferencesUtils {

    private static final String TAG = "SharedPreferencesUtils";

    /** 变量/常量说明 */
    public static final String VIDEOGO_PREFERENCE_NAME = "videoGo";
    private static final String DEVICE_SERIAL_CACHE = "deviceSerial cache";

    private static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences sharedPreference = null;
        sharedPreference = context.getSharedPreferences(VIDEOGO_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreference;
    }
}
