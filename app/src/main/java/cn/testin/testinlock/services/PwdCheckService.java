package cn.testin.testinlock.services;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.testin.testinlock.AppLockApplication;
import cn.testin.testinlock.widget.PwdLockingDialog;

/**
 * Created by qipengfei on 2/14/2018.
 */
public class PwdCheckService extends AbstractService {
    private static final long DELAY_TIME = 0l;
    private static final long INTERVAL = 100l;
    private final int mLayoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;

    private WindowManager windowManager;
    private ImageView mIvBackgound;
    private String mLastPkgName = "";
    private Dialog mDialog;

    public PwdCheckService(@NonNull Context context) {
        super(context);

        this.windowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        this.mIvBackgound = new ImageView(this.mContext);
        this.mIvBackgound.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                this.mLayoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = ((this.mContext.getResources().getDisplayMetrics().widthPixels) / 2);
        params.y = ((this.mContext.getResources().getDisplayMetrics().heightPixels) / 2);
        this.windowManager.addView(this.mIvBackgound, params);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        String devicePassword = sp.getString("devPwd", "UNKNOWN");
        AppLockApplication.sDevicePassword = devicePassword;
    }

    @Override
    public void startWork(String packageName) {}

    @Override
    public void doWork() {
        //Not connected
        if (!AppLockApplication.usbConnected || !AppLockApplication.usbConnected) {
            if (this.mDialog != null && this.mDialog.isShowing() && this.mIvBackgound != null)
                this.mIvBackgound.post(new Runnable() {
                    public void run() {
                        hideDialog();
                    }
                });
            return;
        }
        final String[] topActivities = PwdCheckService.this.getTopActivities();
        if (topActivities == null || topActivities.length == 0)
            return;

        if (getDefaultSettings().contains(topActivities[0])) {
            if (this.mIvBackgound != null && !this.mLastPkgName.equals(topActivities[0])) {
                this.mIvBackgound.post(new Runnable() {
                    public void run() {
//                            if (!mLastPkgName.equals(topActivities[0])) {
                        showDialog();
                        PwdCheckService.this.mLastPkgName = topActivities[0];
//                            }
                    }
                });
            }
        } else {
            if (this.mIvBackgound != null) {
                this.mIvBackgound.post(new Runnable() {
                    public void run() {
                        hideDialog();
                    }
                });
            }
        }
    }

    @Override
    public void stopWork() {}

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (this.mIvBackgound != null) {
            this.windowManager.removeView(this.mIvBackgound);
        }
        /**** added to fix the bug of view not attached to window manager ****/
        try {
            if (this.mDialog != null) {
                if (this.mDialog.isShowing())
                    this.mDialog.dismiss();
                this.mDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideDialog() {
        this.mLastPkgName = "";
        try {
            if (this.mDialog != null) {
                if (this.mDialog.isShowing())
                    this.mDialog.dismiss();
                this.mDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
//        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//        View promptsView = layoutInflater.inflate(R.layout.layout_pwd_locking_dialog, null);
//        this.initPromptsView(promptsView);
        if (this.mDialog != null && this.mDialog.isShowing()) {
            return;
//            this.mDialog.dismiss();
//            this.mDialog = null;
        }

        this.mDialog = new PwdLockingDialog(this.mContext);
        this.mDialog.show();
    }

    @TargetApi(20)
    private String[] getTopActivityBeforeLollipop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e(TAG, "getTopActivityBeforeLollipop()手机版本号不适配, SDK_VER:" + Build.VERSION.SDK_INT);
        }
        ActivityManager am = (ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return null;
        final List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (taskInfo == null || taskInfo.isEmpty())
            return null;
        final ComponentName componentName = taskInfo.get(0).topActivity;
        return new String[]{componentName.getPackageName()};
    }

    @TargetApi(21)
    private String[] getTopActivityForLollipop() {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP) {
            Log.e(TAG, "getTopActivityForLollipop()手机版本号不适配, SDK_VER:" + Build.VERSION.SDK_INT);
            return new String[]{};
        }
        final int PROCESS_STATE_TOP = 2;
        try {
            Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            ActivityManager am = (ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return null;
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                if (
                    // Filters out most non-activity processes
                        process.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                                &&
                                // Filters out processes that are just being
                                // _used_ by the process with the activity
                                process.importanceReasonCode == 0
                        ) {
                    int state = processStateField.getInt(process);

                    if (state == PROCESS_STATE_TOP)
                        /*
                         If multiple candidate processes can get here,
                         it's most likely that apps are being switched.
                         The first one provided by the OS seems to be
                         the one being switched to, so we stop here.
                         */
                        return process.pkgList;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "getTopActivityLollipopOnwards()执行异常", e);
        }
        return new String[]{};
    }

    @TargetApi(22)
    private String[] getTopActivityLollipopOnwards() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Log.e(TAG, "getTopActivityLollipopOnwards()手机版本号不适配, SDK_VER:" + Build.VERSION.SDK_INT);
            return null;
        }

        UsageStatsManager usageStatsManager = (UsageStatsManager) this.mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) return null;
        long time = System.currentTimeMillis();
        // We get usage stats for the last 10 seconds
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 100, time);
        if (stats == null || stats.isEmpty())
            return null;
        String[] topPackageName = null;
        // Sort the stats by the last time used
        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
        for (UsageStats usageStats : stats) {
            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
        }
        if (!mySortedMap.isEmpty()) {
            topPackageName = new String[] {mySortedMap.get(mySortedMap.lastKey()).getPackageName()};
            Log.d(TAG, "getTopActivityLollipopOnwards(): " + topPackageName[0]);
        }
        return topPackageName;
    }

    private String[] getTopActivities() {
        String[] topActivities = null;
        switch (Long.signum(Build.VERSION.SDK_INT - Build.VERSION_CODES.LOLLIPOP)) {
            case -1:
                topActivities = this.getTopActivityBeforeLollipop();
                break;
            case 0:
                topActivities = this.getTopActivityForLollipop();
                break;
            case 1:
                topActivities = this.getTopActivityLollipopOnwards();
                break;
            default:
                break;
        }
        return topActivities;
    }

    private List<String> getDefaultSettings() {
        ArrayList<String> acts = new ArrayList<String>();
        acts.add("com.android.settings");
        acts.add("android");
        acts.add("com.android.settings.SubSettings");//三星手机“开发者选项“

        String[] settings = {Settings.ACTION_SETTINGS,
                Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS,
                Settings.ACTION_LOCATION_SOURCE_SETTINGS,
        };

        for (String str : settings) {
            Intent intent = new Intent(str);
            ResolveInfo res = mContext.getPackageManager().resolveActivity(
                    intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (res != null) {
                ActivityInfo activityInfo = res.activityInfo;
                if (activityInfo != null) {
                    acts.add(activityInfo.name);
                }
            }
        }
        return acts;
    }
}