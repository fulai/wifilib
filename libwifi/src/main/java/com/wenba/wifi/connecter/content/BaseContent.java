package com.wenba.wifi.connecter.content;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.wenba.wifi.connecter.R;
import com.wenba.wifi.connecter.util.Wifi;
import com.wenba.wifi.connecter.activity.Floating;

public abstract class BaseContent implements Floating.Content, OnCheckedChangeListener {

    protected final WifiManager mWifiManager;
    protected final Floating mFloating;
    protected final ScanResult mScanResult;
    protected final String mScanResultSecurity;
    protected final boolean mIsOpenNetwork;

    protected int mNumOpenNetworksKept;

    protected View mView;
    protected Button saveButton;

    protected OnClickListener mCancelOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mFloating.finish();
        }
    };

    protected String getCancelString() {
        return mFloating.getString(android.R.string.cancel);
    }

    private static final int[] SIGNAL_LEVEL = {R.string.wifi_signal_0, R.string.wifi_signal_1,
            R.string.wifi_signal_2, R.string.wifi_signal_3};

    public BaseContent(final Floating floating, final WifiManager wifiManager, final ScanResult scanResult) {
        super();
        mWifiManager = wifiManager;
        mFloating = floating;
        mScanResult = scanResult;
        mScanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(mScanResult);
        mIsOpenNetwork = Wifi.ConfigSec.isOpenNetwork(mScanResultSecurity);
        saveButton = floating.getSaveButton();
        mView = View.inflate(mFloating, R.layout.base_content, null);
        ((TextView) mView.findViewById(R.id.SignalStrength_TextView)).setText(SIGNAL_LEVEL[WifiManager
                .calculateSignalLevel(mScanResult.level, SIGNAL_LEVEL.length)]);
        final String rawSecurity = Wifi.ConfigSec.getDisplaySecirityString(mScanResult);
        final String readableSecurity = Wifi.ConfigSec.isOpenNetwork(rawSecurity) ? mFloating.getString(R.string
                .wifi_security_open) : rawSecurity;
        ((TextView) mView.findViewById(R.id.Security_TextView)).setText(readableSecurity);
        ((CheckBox) mView.findViewById(R.id.ShowPassword_CheckBox)).setOnCheckedChangeListener(this);

        mNumOpenNetworksKept = Settings.Secure.getInt(floating.getContentResolver(),
                Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
    }


    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        EditText etPwd = mView.findViewById(R.id.Password_EditText);
        etPwd.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        (isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                : InputType.TYPE_TEXT_VARIATION_PASSWORD));
        etPwd.setSelection(etPwd.getText().length());
    }


    public OnClickListener mChangePasswordOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            changePassword();
        }

    };

    public void changePassword() {
        mFloating.setContent(new ChangePasswordContent(mFloating, mWifiManager, mScanResult));
    }

}
