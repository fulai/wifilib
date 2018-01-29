package com.wenba.wifi.connecter.content;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
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

/**
 * 没有保存wifi信息Content
 */
public class NewNetworkContent extends BaseContent {

    private boolean mIsOpenNetwork = false;
    private EditText pwdEt;

    public NewNetworkContent(final Floating floating, final WifiManager wifiManager, ScanResult scanResult) {
        super(floating, wifiManager, scanResult);

        mView.findViewById(R.id.Status).setVisibility(View.GONE);
        mView.findViewById(R.id.Speed).setVisibility(View.GONE);
        mView.findViewById(R.id.IPAddress).setVisibility(View.GONE);
        pwdEt = mView.findViewById(R.id.Password_EditText);
        if (Wifi.ConfigSec.isOpenNetwork(mScanResultSecurity)) {
            mIsOpenNetwork = true;
            mView.findViewById(R.id.Password).setVisibility(View.GONE);
        } else {
            ((TextView) mView.findViewById(R.id.Password_TextView)).setText(R.string.please_type_passphrase);
        }
        if (!mIsOpenNetwork) {
            saveButton.setEnabled(false);
        }
        pwdEt.addTextChangedListener(new TextWatcher() {
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

    private OnClickListener mConnectOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            boolean connResult = false;
            if (!mIsOpenNetwork) {
                connResult = Wifi.connectToNewNetwork(mFloating, mWifiManager, mScanResult
                        , ((EditText) mView.findViewById(R.id.Password_EditText)).getText().toString()
                        , mNumOpenNetworksKept);
            }
            if (!connResult) {
                Toast.makeText(mFloating, "暂连接不上", Toast.LENGTH_LONG).show();
            }
            mFloating.finish();
        }
    };

    private OnClickListener mOnClickListeners[] = {mCancelOnClick, mConnectOnClick};

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
                return mFloating.getText(R.string.connect);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getTitle() {
        return mFloating.getString(R.string.wifi_connect_to, mScanResult.SSID);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {

    }

}
