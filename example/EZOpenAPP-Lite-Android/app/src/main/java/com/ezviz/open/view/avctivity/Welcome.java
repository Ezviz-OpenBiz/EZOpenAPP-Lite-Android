package com.ezviz.open.view.avctivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;


import com.ezviz.open.utils.EZLog;
import com.ezviz.opensdk.auth.EZAccessTokenInternal;
import com.ezviz.opensdk.base.EZBaseCore;
import com.ezviz.opensdk.base.JsonUtils;
import com.ezviz.opensdk.base.LogUtil;
import com.ezviz.opensdk.base.SharedPreferencesUtils;
import com.videogo.openapi.EZOpenSDK;

import java.util.ArrayList;
import com.ezviz.open.R;
public class Welcome extends RootActivity {

    private static final String TAG = "Welcome";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        EZLog.i(TAG,"Welcome");
        //EZOpenApplication.mEZOpenApplication.init();
//        if (Build.VERSION.SDK_INT >= 23) {
//            checkPermission();
//        }else{

            gotoLogin();
//        }
    }

    private void gotoLogin(){
        if (EZOpenSDK.isLogin()) {
            Intent toIntent = new Intent(this, MainActivity.class);
            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toIntent);
            finish();
        } else {
            EZOpenSDK.openLoginPage();
            finish();
        }
    }

    public void checkPermission() {
        ArrayList<String> list = new ArrayList<String>();
        String phone_permission = Manifest.permission.READ_PHONE_STATE;
        String storage_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, phone_permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, phone_permission)) {
                showToast("请在设置中打开权限");
                return;
            }
            list.add(phone_permission);
        } else {

        }
        if (ContextCompat.checkSelfPermission(this, storage_permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, storage_permission)) {
                showToast("请在设置中打开权限");
                return;
            }
            list.add(storage_permission);
        } else {

        }
        if (list.size() <= 0) {
            //EZOpenApplication.mEZOpenApplication.init();
            gotoLogin();
        } else {
            String[] strings = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                strings[i] = list.get(i);
            }
            ActivityCompat.requestPermissions(this, strings, 700);
        }
    }

    //@Override
    //public void onRequestPermissionsResult(int requestCode,
    //                                       String permissions[], int[] grantResults) {
    //    switch (requestCode) {
    //        case 700: {
    //            if (grantResults.length > 0
    //                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //                EZOpenApplication.mEZOpenApplication.init();
    //                gotoLogin();
    //            } else {
    //                finish();
    //            }
    //            return;
    //        }
    //
    //        // other 'case' lines to check for other
    //        // permissions this app might request
    //    }
    //}
}
