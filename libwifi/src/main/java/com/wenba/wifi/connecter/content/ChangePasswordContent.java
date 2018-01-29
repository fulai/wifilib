package com.wenba.wifi.connecter.content;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wenba.wifi.connecter.R;
import com.wenba.wifi.connecter.activity.Floating;
import com.wenba.wifi.connecter.util.Wifi;
import com.wenba.wifi.connecter.view.ChangingAwareEditText;

public class ChangePasswordContent extends BaseContent {

    private ChangingAwareEditText mPasswordEditText;

    public ChangePasswordContent(Floating floating, WifiManager wifiManager,
                                 ScanResult scanResult) {
        super(floating, wifiManager, scanResult);

        mView.findViewById(R.id.Status).setVisibility(View.GONE);
        mView.findViewById(R.id.Speed).setVisibility(View.GONE);
        mView.findViewById(R.id.IPAddress).setVisibility(View.GONE);
        saveButton.setEnabled(false);
        mPasswordEditText = ((ChangingAwareEditText) mView.findViewById(R.id.Password_EditText));

        ((TextView) mView.findViewById(R.id.Password_TextView)).setText(R.string.please_type_passphrase);

        ((EditText) mView.findViewById(R.id.Password_EditText)).setHint(R.string.wifi_password_unchanged);
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (string.length() < 8) {
                    saveButton.setEnabled(false);
                } else {
                    saveButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    public int getButtonCount() {
        return 2;
    }

    @Override
    public OnClickListener getButtonOnClickListener(int index) {
        return mOnClickListeners[index];
    }

    @Override
    public CharSequence getButtonText(int index) {
        switch (index) {
            case 0:
                return getCancelString();
            case 1:

                return mFloating.getString(R.string.wifi_save_config);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getTitle() {
        return mScanResult.SSID;
    }

    private OnClickListener mSaveOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mPasswordEditText.getChanged()) {
                final WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, mScanResult,
                        mScanResultSecurity);
                String strPwd = mPasswordEditText.getText().toString().trim();
                boolean saveResult = false;
                if (config != null) {
                    saveResult = Wifi.changePasswordAndConnect(mFloating, mWifiManager, config
                            , strPwd
                            , mNumOpenNetworksKept);
                }
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && !saveResult) {
                    Toast.makeText(mFloating, R.string.save_Failed_conection_admin, Toast.LENGTH_LONG).show();
                } else if (!saveResult) {
                    Toast.makeText(mFloating, R.string.toastFailed, Toast.LENGTH_LONG).show();
                }
            }

            mFloating.finish();
        }
    };

    OnClickListener mOnClickListeners[] = {mCancelOnClick, mSaveOnClick};

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
    }

}
