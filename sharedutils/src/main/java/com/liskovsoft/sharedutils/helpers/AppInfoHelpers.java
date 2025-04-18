package com.liskovsoft.sharedutils.helpers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.res.Resources.NotFoundException;
import androidx.core.content.pm.PackageInfoCompat;
import com.liskovsoft.sharedutils.mylogger.Log;

public class AppInfoHelpers {
    private static final String TAG = AppInfoHelpers.class.getSimpleName();

    public static String getAppVersion(Context context) {
        return formatAppVersion(getAppVersionName(context), getActivityLabel(context));
    }

    public static String getAppVersionRobust(Context context, String launchActivityName) {
        return formatAppVersion(getAppVersionName(context), getActivityLabelRobust(context, launchActivityName));
    }

    private static String formatAppVersion(String version, String label) {
        return String.format("%s (%s)", version, label);
    }

    public static String getActivityLabelRobust(Context context, String launchActivityName) {
        return getActivityLabel(context, launchActivityName);
    }

    @SuppressWarnings("deprecation")
    public static int getAppVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionCode;
            // modern version
            //return (int) PackageInfoCompat.getLongVersionCode(packageInfo);
        }

        return -1;
    }

    public static String getAppVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }

        return null;
    }

    public static String getAppLabel(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);

        if (packageInfo != null) {
            return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        }

        return null;
    }

    private static PackageInfo getPackageInfo(Context context) {
        try {
            return context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.d(TAG, e);
            e.printStackTrace();
        }

        return null;
    }

    public static String getActivityLabel(Context context) {
        return getActivityLabel(context, (String) null);
    }

    public static String getActivityLabel(Context context, String cls) {
        if (cls != null) {
            return getActivityLabel(context, new ComponentName(context, cls));
        } else if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return getActivityLabel(context, activity.getComponentName());
        }

        return null;
    }

    private static String getActivityLabel(Context context, ComponentName name) {
        PackageManager pm = context.getPackageManager();

        ActivityInfo info = null;
        String label = "No Label";

        try {
            info = pm.getActivityInfo(name, PackageManager.GET_META_DATA);
            label = context.getResources().getString(info.labelRes);
        } catch (NameNotFoundException | NotFoundException e) {
            if (info != null) {
                if (info.metaData != null && info.metaData.containsKey("AppLabel")) {
                    label = info.metaData.getString("AppLabel");
                } else {
                    label = Helpers.getSimpleClassName(info.name); // label not found, return simple class name
                }
            }
        }

        return label;
    }

    public static ActivityInfo getActivityInfo(Context ctx, ComponentName componentName) {
        ActivityInfo ai = null;
        try {
            ai = ctx.getPackageManager().getActivityInfo(componentName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ai;
    }

    public static ProviderInfo getProviderInfo(Context ctx, ComponentName componentName) {
        ProviderInfo ai = null;
        try {
            ai = ctx.getPackageManager().getProviderInfo(componentName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ai;
    }

    public static ActivityInfo[] getActivityList(Context context) {
        PackageManager pm = context.getPackageManager();

        ActivityInfo[] list = null;

        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            list = info.activities;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean isActivityExists(Context context, String actName) {
        ActivityInfo[] list = getActivityList(context);

        if (list != null) {
            for (ActivityInfo activityInfo : list) {
                if (activityInfo.name.contains(actName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getMainActivityName(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

        return intent != null && intent.getComponent() != null ? intent.getComponent().getClassName() : null;
    }
}
