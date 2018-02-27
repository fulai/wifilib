package com.wenba.wifi.connecter.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Dengmao on 18/1/18.
 */

public class WifiUtil {
    /**
     * 获取wifi是否连过
     *
     * @param scanResult
     * @param context
     * @return
     */
    public static WifiConfiguration hasSaveConfig(ScanResult scanResult, Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        String security = Wifi.ConfigSec.getScanResultSecurity(scanResult);
        WifiConfiguration config = Wifi.getWifiConfiguration(wifiManager, scanResult, security);
        return config;
    }

    /**
     * 获取连接wifi
     *
     * @param context
     * @return
     */
    public static String getConnWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        String ssid = connectionInfo.getSSID();
        String substring = ssid.substring(1, ssid.length() - 1);
        return substring;
    }

    /**
     * 是否连接上wifi
     *
     * @return
     */
    public static boolean isWifiConn(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 判断网络情况
     *
     * @param context 上下文
     * @return false 表示没有网络 true 表示有网络
     */
    public static boolean isNetworkAvalible(Context context) {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();
            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 是否需要输入密码
     *
     * @param scanResult
     * @return
     */
    public static boolean needPwd(ScanResult scanResult) {
        String scanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(scanResult);
        return !Wifi.ConfigSec.isOpenNetwork(scanResultSecurity);
    }

}
