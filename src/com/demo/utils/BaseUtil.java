package com.demo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2014/4/29.
 */
public class BaseUtil {
    private static Context context;
    private static BaseUtil instance = null;
    private static  float batteryLevel = -1;

    private static final String LOG_TAG = "base.utils";


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

    /**
     * 获取应用包名
     *
     * @return
     */
    private String getPackageName()
    {
        String packageName = "";

        packageName = context.getPackageName();

        if(TextUtils.isEmpty(packageName))
        {
            Log.e(LOG_TAG, "getPackageName error !!!");
        }

        return packageName;
    }

    /**
     * 获取应用版本信息
     *
     * @return 应用程序版本号
     */
    private String getVersionName()
    {
        String versionName = "";

        String packageName = getPackageName();

        PackageManager pm = context.getPackageManager();

        try
        {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

            versionName = packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.e(LOG_TAG, "getVersionName error !!!");
        }

        return versionName;
    }

    /**
     * 获取应用Version Code
     *
     * @return 应用程序版本信息
     *
     * 该属性主要用于在应用市场升级使用（对消费者不可见）
     */
    private String getVersionCode()
    {
        String versionCode = "";

        String packageName = getPackageName();

        PackageManager pm = context.getPackageManager();

        try
        {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

            versionCode = String.valueOf(packageInfo.versionCode);

        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.e(LOG_TAG, "getVersionName error !!!");
        }

        return versionCode;
    }

    /**
     * 获取应用程序名称
     * @return 应用程序名称
     */
    public String getApplicationName()
    {
        String applicationName = "";
        ApplicationInfo applicationInfo = null;
        PackageManager pm = context.getPackageManager();
        try
        {
            applicationInfo = pm.getApplicationInfo(getPackageName(), 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        applicationName = (String) pm.getApplicationLabel(applicationInfo);

        return applicationName;
    }

    /**
     * 获取网络状态
     *
     * @return
     */
    private static boolean networkReachable()
    {
        boolean bRet = false;

        try
        {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conn.getActiveNetworkInfo();
            bRet = (null == netInfo) ? false : netInfo.isAvailable();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "get network state error", e);
        }

        return bRet;
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
