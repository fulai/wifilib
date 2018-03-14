package com.wenba.wifi.connecter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wenba.wifi.connecter.R;
import com.wenba.wifi.connecter.adapter.ListBaseAdapter;
import com.wenba.wifi.connecter.model.ScanResultInfo;
import com.wenba.wifi.connecter.receiver.NetworkConnectChangedReceiver;
import com.wenba.wifi.connecter.util.Constants;
import com.wenba.wifi.connecter.util.Wifi;
import com.wenba.wifi.connecter.util.WifiStatus;
import com.wenba.wifi.connecter.util.WifiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WifiListActivity extends AppCompatActivity implements View.OnClickListener {

    private WifiManager mWifiManager;
    private List<ScanResultInfo> mScanResultInfos;
    private ListBaseAdapter mListBaseAdapter;
    private String firstSSID;
    private NetworkConnectChangedReceiver mNetworkConnectChangedReceiver;

    private TextView backTxt;
    private ListView listView;
    private HashMap<String, String> initWifiInfo = new HashMap<>();
    private boolean init = false;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_wifi_list);
        initWifiInfo.put("Wenba 2.4G", "wenba100wifi");
        initWifiInfo.put("Wenba 5G", "wenba100wifi");
        backTxt = findViewById(R.id.back_txt);
        backTxt.setOnClickListener(this);
        listView = findViewById(R.id.listview);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        listView.setOnItemClickListener(mItemOnClick);
        registerBroadcast();
        showWifi();
        connWifi();

    }

    private void connWifi() {
        boolean isConn = false;
        for (String key : initWifiInfo.keySet()) {
            for (ScanResultInfo info : mScanResultInfos) {
                if (key.equals(info.getScanResult().SSID) && !isConn) {
                    boolean connResult = Wifi.connectToNewNetwork(WifiListActivity.this, mWifiManager, info
                                    .getScanResult()
                            , initWifiInfo.get(key)
                            , 0);
                    if (connResult) {
                        isConn = true;
                        Toast.makeText(WifiListActivity.this, "已经连接", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.CONNECT_ACTION);
        filter.addAction(Constants.WIFI_ITEM_FLUSH_ACTION);
        registerReceiver(mReceiver, filter);

        IntentFilter filterWifiChanged = new IntentFilter();
        filterWifiChanged.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filterWifiChanged.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mNetworkConnectChangedReceiver = new NetworkConnectChangedReceiver(this);
        registerReceiver(mNetworkConnectChangedReceiver, filterWifiChanged);
    }


    private void showWifi() {
        String stringSSID = WifiUtil.getConnWifiSSID(this);
        String status = "";
        if (!TextUtils.isEmpty(stringSSID) && WifiUtil.isWifiConn(this)) {
            status = WifiStatus.connected.getName();
        } else {
            status = WifiStatus.saved.getName();
        }
        startScan(stringSSID, status);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mNetworkConnectChangedReceiver);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Constants.CONNECT_ACTION)) {
                String stringExtra = intent.getStringExtra(Constants.CONNECT_STATUS);
                String stringSSID = intent.getStringExtra(Constants.CONNECT_SSID);
                stringSSID = stringSSID.substring(1, stringSSID.length() - 1);
                if (stringSSID.contains("unknown")) {
                    return;
                }
                Log.i("networkinfo", "stringSSID：" + stringSSID);
                Log.i("networkinfo", "stringExtra：" + stringExtra);
                Log.i("networkinfo", "firstSSID：" + firstSSID);
                if (!TextUtils.isEmpty(firstSSID) && firstSSID.equals(stringSSID)) {//直接更新view数据
                    Log.i("networkinfo", "直接更新view数据:" + stringExtra);
                    mListBaseAdapter.updataView(0, listView, stringExtra);
                } else {//需要更新view位置
                    firstSSID = stringSSID;
                    Log.i("networkinfo", "更新布局;" + stringSSID + ":" + stringExtra);
                    startScan(stringSSID, stringExtra);
                }
            } else if (action.equals(Constants.WIFI_ITEM_FLUSH_ACTION)) {
                Log.i("networkinfo", "刷新wifi");
                showWifi();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!init && WifiUtil.isWifiConn(WifiListActivity.this) && WifiUtil.isNetworkAvalible
                            (WifiListActivity
                                    .this)) {
                        Log.i("kkkk", "have network");
                        init = true;
                        Intent intent1 = new Intent();
                        intent1.setAction("com.wenba.init.action.SETTINGACCOUNTACTIVITY");
                        intent1.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(intent1);
                    }
                }
            }, 3000);
        }
    };

    private void startScan(String stringSSID, String stringExtra) {
        Log.i("TAG", "startScan");
        mWifiManager.startScan();
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        mScanResultInfos = new ArrayList<>();
        filterScanResult(scanResults, stringSSID, stringExtra);
        mListBaseAdapter = new ListBaseAdapter(mScanResultInfos, this);
        listView.setAdapter(mListBaseAdapter);
//        mWifiManager.startScan();
    }

    private void filterScanResult(List<ScanResult> scanResults, String stringSSID, String stringExtra) {
        Set<String> set = new HashSet<>();
        for (ScanResult scanResult : scanResults) {
            if (!TextUtils.isEmpty(scanResult.SSID)) {
                set.add(scanResult.SSID);
            }
        }
        int size = scanResults.size();
        Log.i("TAG", "wifisize==" + set.size());
        int index = 0;
        boolean needInc = true;
        ScanResultInfo scanResultInfo;
        List<ScanResultInfo> tempScanResultInfo = new ArrayList<>();
        for (String string : set) {
            for (int i = 0; i < size; i++) {
                if (string.equals(scanResults.get(i).SSID)) {
                    scanResultInfo = new ScanResultInfo();
                    scanResultInfo.setScanResult(scanResults.get(i));
                    WifiStatus wifiStatus = getStatus(scanResults.get(i));
                    if (string.equals(stringSSID)) {
                        needInc = false;
                        firstSSID = stringSSID;
                        scanResultInfo.setStatus(stringExtra);
                    } else {
                        if (needInc) {
                            index++;
                        }
                        scanResultInfo.setStatus(wifiStatus.getName());
                    }
                    tempScanResultInfo.add(scanResultInfo);
                    break;
                }
            }
            Log.i("TAG", "ssid=" + string);
        }
        if (index < set.size()) {
            ScanResultInfo temp1 = tempScanResultInfo.get(0);
            ScanResultInfo temp2 = tempScanResultInfo.get(index);
            System.out.println(temp1);
            System.out.println(temp2);
            tempScanResultInfo.set(0, temp2);
            tempScanResultInfo.set(index, temp1);
        }
        mScanResultInfos = tempScanResultInfo;
        Log.i("TAG", "tempScanResultInfo==" + tempScanResultInfo.size());
    }

    private WifiStatus getStatus(ScanResult result) {
        if (WifiUtil.hasSaveConfig(result, this) == null) {
            return WifiStatus.none;
        } else {
            return WifiStatus.saved;
        }
    }

    private OnItemClickListener mItemOnClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            final ScanResult result = mScanResultInfos.get(position).getScanResult();
            launchWifiConnecter(result);
        }
    };

    /**
     * Try to launch Wifi Connecter with {@link }. Prompt user to download if Wifi Connecter is not installed.
     *
     * @param hotspot
     */
    private void launchWifiConnecter(final ScanResult hotspot) {
        final Intent intent = new Intent(this, WifiSettingActivity.class);
        intent.putExtra("com.wenba.wifi.connecter.extra.HOTSPOT", hotspot);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
