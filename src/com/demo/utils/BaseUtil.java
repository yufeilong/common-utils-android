package com.demo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by Administrator on 2014/4/29.
 */
public class BaseUtil {
    private static Context context;
    private static BaseUtil instance = null;

    private BaseUtil(Context cx) {
        this.context = cx;
    }


    private static  float batteryLevel = -1;

    /**
     * 获取设备名称（型号）
     *
     * @return 设备名称（型号）
     */
    private String getDeviceName() {
        String deviceName;
        deviceName = Build.MODEL;
        if (TextUtils.isEmpty(deviceName)) {
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
    private String getDeviceImsi() {
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
    private boolean getSimState() {
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

    /**
     * 获取设备sim卡类型
     * 1代表移动，2代表联通，3代表电信
     * @return
     */
    private String getSimType() {
        String imsi = getDeviceImsi();

        String simType = "";

        if (imsi != null) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
                simType = "1";
            } else if (imsi.startsWith("46001")) {
                simType = "2";
            } else if (imsi.startsWith("46003")) {
                simType = "3";
            }
        }

        return simType;
    }

    private float getBatteryLevel() {
        if (batteryLevel < -0.5f) {
            //初始化电池监听
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            batteryLevel = 0;
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                        int level = intent.getIntExtra("level", 0);//电量
                        int scale = intent.getIntExtra("scale", 0);//最大电量
                        if (scale != 0) {
                            batteryLevel = ((float) level) / scale;
                            if (batteryLevel < 0) {
                                batteryLevel = 0;
                            } else if (batteryLevel > 1) {
                                batteryLevel = 1;
                            }
                        } else {
                            batteryLevel = 0;
                        }
                    }
                }
            }, intentFilter);
        }

        return batteryLevel;
    }

    public static void setContext(Context context) {
        BaseUtil.context = context;
    }

    public static BaseUtil getInstance() {
        if (instance == null) {
               synchronized (BaseUtil.class) {
                   return new BaseUtil(context);
               }
        }
        return instance;
    }
}
