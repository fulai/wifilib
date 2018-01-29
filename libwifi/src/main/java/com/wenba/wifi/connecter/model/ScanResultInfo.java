package com.wenba.wifi.connecter.model;

import android.net.wifi.ScanResult;

/**
 * Created by Dengmao on 18/1/18.
 */

public class ScanResultInfo {
    private ScanResult scanResult;
    private String status;

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
