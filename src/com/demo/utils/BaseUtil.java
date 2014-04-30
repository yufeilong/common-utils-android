package com.demo.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by Administrator on 2014/4/29.
 */
public class BaseUtil {
    private static Context context;
    private static BaseUtil instance = null;

    private BaseUtil(Context cx) {
        this.context = cx;
    }

    /**
     * 获取设备名称（型号）
     *
     * @return 设备名称（型号）
     */
    private String getDeviceName() {
        String deviceName;
        deviceName = Build.MODEL;
        if (deviceName == null || "".equals(deviceName)) {
            deviceName = "Unknow Name";
        }
        return deviceName;
    }

    /**
     * 获取设备imei
     *
     * @return
     */
    private String getDeviceImei() {
        String deviceImei;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        deviceImei = tm.getDeviceId();

        if (deviceImei == null) {
            deviceImei = "";
        }

        return deviceImei;
    }

    /**
     * 获取设备imsi
     *
     * @return
     */
    public String getDeviceImsi() {
        String imsi;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        imsi = tm.getSubscriberId();

        if (imsi == null) {
            imsi = "";
        }

        return imsi;
    }

    /**
     * 获取sim卡状态
     *
     * @return sim卡是否可用
     */
    public boolean getSimState() {
        boolean simAvailable = false;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = tm.getSimState();

        switch (simState) {
            case TelephonyManager.SIM_STATE_READY:
                simAvailable = true;
                break;
            default:
                simAvailable = false;
                break;
        }

        return simAvailable;
    }

    public static void setContext(Context context) {
        BaseUtil.context = context;
    }

    public static BaseUtil getInstance() {
        if (instance == null) {
            return new BaseUtil(context);
        } else {
            return instance;
        }
    }
}
