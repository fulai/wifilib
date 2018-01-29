package com.wenba.wifi.connecter.util;

/**
 * Created by Dengmao on 18/1/18.
 */

public enum WifiStatus {
    none("", 1), connected("已连接", 2), saved("已保存", 3);
    // 成员变量
    private String name;
    private int index;

    // 构造方法
    private WifiStatus(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (WifiStatus c : WifiStatus.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
