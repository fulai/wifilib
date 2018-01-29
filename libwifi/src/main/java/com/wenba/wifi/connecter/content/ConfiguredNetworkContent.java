package com.wenba.wifi.connecter.content;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.wenba.wifi.connecter.util.Constants;
import com.wenba.wifi.connecter.R;
import com.wenba.wifi.connecter.util.Wifi;
import com.wenba.wifi.connecter.activity.Floating;

/**
 * 已保存的，需要密码的wifi
 */
public class ConfiguredNetworkContent extends BaseContent {

    public ConfiguredNetworkContent(Floating floating, WifiManager wifiManager,
                                    ScanResult scanResult) {
        super(floating, wifiManager, scanResult);

        mView.findViewById(R.id.Status).setVisibility(View.GONE);
        mView.findViewById(R.id.Speed).setVisibility(View.GONE);
        mView.findViewById(R.id.IPAddress).setVisibility(View.GONE);
        mView.findViewById(R.id.Password).setVisibility(View.GONE);
    }

    @Override
    public int getButtonCount() {
        return 3;
    }

    @Override
    public OnClickListener getButtonOnClickListener(int index) {
        switch (index) {
            case 0:
                return mCancelOnClick;
            case 1:
                if (mIsOpenNetwork) {
                    return mForgetOnClick;
                } else {
                    return mOpOnClick;
                }
            case 2:

                return mConnectOnClick;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getButtonText(int index) {
        switch (index) {
            case 0:
                return getCancelString();
            case 1:
                if (mIsOpenNetwork) {
                    return mFloating.getString(R.string.forget_network);
                } else {
                    return mFloating.getString(R.string.buttonOp);
                }
            case 2:

                return mFloating.getString(R.string.connect);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getTitle() {
        return mFloating.getString(R.string.wifi_connect_to, mScanResult.SSID);
    }

    private OnClickListener mConnectOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            final WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, mScanResult, mScanResultSecurity);
            boolean connResult = false;
            if (config != null && !mIsOpenNetwork) {
                connResult = Wifi.connectToConfiguredNetwork(mFloating, mWifiManager, config, false);
            }
            if (!connResult) {
                Toast.makeText(mFloating, "暂连接不上", Toast.LENGTH_LONG).show();
            }
            mFloating.finish();
        }
    };

    private OnClickListener mOpOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mFloating.registerForContextMenu(v);
            mFloating.openContextMenu(v);
            mFloating.unregisterForContextMenu(v);
        }
    };

    private OnClickListener mForgetOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            forget();
        }
    };

    private void forget() {
        final WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, mScanResult, mScanResultSecurity);
        boolean result = false;
        if (config != null) {
            result = mWifiManager.removeNetwork(config.networkId)
                    && mWifiManager.saveConfiguration();
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && !result) {
            Toast.makeText(mFloating, R.string.save_Failed_conection_admin, Toast.LENGTH_LONG).show();
        } else if (!result) {
            Toast.makeText(mFloating, R.string.toastFailed, Toast.LENGTH_LONG).show();
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Constants.WIFI_ITEM_FLUSH_ACTION);
            mView.getContext().sendBroadcast(intent);
        }

        mFloating.finish();
    }

    private static final int MENU_FORGET = 0;
    private static final int MENU_CHANGE_PASSWORD = 1;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_FORGET:
                forget();
                break;
            case MENU_CHANGE_PASSWORD:
                changePassword();
                break;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, MENU_FORGET, Menu.NONE, R.string.forget_network);
        menu.add(Menu.NONE, MENU_CHANGE_PASSWORD, Menu.NONE, R.string.wifi_change_password);
    }

}
