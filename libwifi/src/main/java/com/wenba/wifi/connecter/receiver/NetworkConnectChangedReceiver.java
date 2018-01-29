package com.wenba.wifi.connecter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.wenba.wifi.connecter.util.Constants;

/**
 * Created by Dengmao on 18/1/17.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkReceiver";
    private WifiManager wifiManager;

    public NetworkConnectChangedReceiver(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context
                .WIFI_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String wifiSSID = "";
        Intent intentStatus = new Intent(Constants.CONNECT_ACTION);
        String status = "";
        boolean isSendBroadcast = false;
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
            wifiSSID = wifiInfo.getSSID();
            Log.i(TAG, "DetailedState:" + detailedState);
//            if ("0x".equals(wifiSSID) || wifiSSID.contains("unknown") || detailedState == NetworkInfo.DetailedState
//                    .SCANNING || detailedState == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK) {
//                return;
//            }
//            if (detailedState == NetworkInfo.DetailedState.CONNECTED
//                    || (detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR && wifiInfo.getIpAddress
//                    () != 0)) {
//                status = "已连接";
//                Log.i(TAG, "detailed:" + wifiSSID + "连接成功！！！");
//            } else if (detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
//                Log.i(TAG, "detailed:" + wifiSSID + "正在获取IP地址－－－");
//                isSendBroadcast = true;
//                status = "正在获取IP地址..";
//            } else if (detailedState == NetworkInfo.DetailedState.DISCONNECTED) {
//                status = "连接失败";
//                Log.i(TAG, "detailed:" + wifiSSID + "连接失败");
//            } else if (detailedState == NetworkInfo.DetailedState.AUTHENTICATING) {
//                Log.i(TAG, "detailed:" + wifiSSID + "身份验证中");
//                status = "身份验证中..";
//            } else if (detailedState == NetworkInfo.DetailedState.CONNECTING) {
//                Log.i(TAG, "detailed:" + wifiSSID + "连接中中－－－");
//                //isSendBroadcast = true;
//                //status = "正在连接..";
//            }
            if (detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                Log.i(TAG, "DetailedState:" + wifiSSID + ":正在获取IP地址..");
                isSendBroadcast = true;
                status = "正在获取IP地址..";
            } else if (detailedState == NetworkInfo.DetailedState.CONNECTING || detailedState == NetworkInfo
                    .DetailedState.SCANNING) {
                Log.i(TAG, "DetailedState;" + wifiSSID + ":正在连接..");
                isSendBroadcast = true;
                status = "正在连接..";
            } else {
                isSendBroadcast = false;
            }
            Log.i(TAG, "DetailedState:" + detailedState + "---SSID:" + wifiSSID +
                    "--status:" + status);

        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            wifiSSID = wifiInfo.getSSID();
            SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
            if (error == WifiManager.ERROR_AUTHENTICATING) {
                Log.i(TAG, "supplicantState:" + wifiSSID + ":身份验证出现问题");
                isSendBroadcast = true;
                status = "身份验证出现问题";
            } else if (supplicantState == SupplicantState.FOUR_WAY_HANDSHAKE) {
                Log.i(TAG, "supplicantState:" + wifiSSID + ":身份验证中.. ");
                isSendBroadcast = true;
                status = "身份验证中..";
            } else if (supplicantState == SupplicantState.COMPLETED) {
                Log.i(TAG, "supplicantState:" + wifiSSID + ":已连接 ");
                isSendBroadcast = true;
                status = "已连接";
            } else {
                isSendBroadcast = false;
            }
            Log.i(TAG, "supplicantState:" + supplicantState + "-------error:" + error + "---SSID:" + wifiSSID +
                    "--status:" + status);
        }
        if (TextUtils.isEmpty(status) || ("0x".equals(wifiSSID) || wifiSSID.contains("unknown"))) {
            Log.i(TAG, "last:empty 0x 0x unknown unknown");
            return;
        }

        if (isSendBroadcast) {
            intentStatus.putExtra(Constants.CONNECT_STATUS, status);
            intentStatus.putExtra(Constants.CONNECT_SSID, wifiSSID);
            context.sendBroadcast(intentStatus);
        }
    }
}
