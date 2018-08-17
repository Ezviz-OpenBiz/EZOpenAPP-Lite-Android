package com.ezviz.open.view.avctivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ezviz.open.R;
import com.ezviz.open.view.widget.Topbar;


public class WiFiSelecteActivity extends RootActivity {

    private EditText mPasswordEditText;

    private TextView mSSIDTextView;

    private Topbar mTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_selecte);
        mTopBar = (Topbar) findViewById(R.id.topbar);
        mTopBar.setOnTopbarClickListener(new Topbar.OnTopbarClickListener() {
            @Override
            public void onLeftButtonClicked() {
                finish();
            }

            @Override
            public void onRightButtonClicked() {

            }
        });
        mSSIDTextView = (TextView) findViewById(R.id.tvSSID);
        mPasswordEditText = (EditText) findViewById(R.id.edtPassword);
        findViewById(R.id.search_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ssid = mSSIDTextView.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(ssid)) {

                } else {
                    Intent intent = new Intent(WiFiSelecteActivity.this, SmartConfigActivity.class);
                    intent.putExtra("SSID", ssid);
                    intent.putExtra("password", password);
                    intent.putExtras(getIntent());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isWifi(this)) {
            mSSIDTextView.setText("");
            Toast.makeText(this, "Please connect the wifi", Toast.LENGTH_LONG).show();
        } else {
            mSSIDTextView.setText(getWifiSSID(this));
        }
    }

    /**
     * make true current connect service is wifi
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager =
            (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static String getWifiSSID(Context ctx) {
        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifi_service.getConnectionInfo();
        String ssid = connectionInfo.getSSID();
        if (Build.VERSION.SDK_INT >= 17 && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }
}
