package cn.testin.testinlock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.testin.testinlock.services.AppCheckServices;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE_USAGE_ACCESS = 0x100;
    private static final int REQUEST_CODE_CAN_DRAW_OVERLAYS = 0x200;
    private TextView mTvMsg;
    private boolean isFirstCheckUsageAccess = true;
    private boolean isFirstCheckCanDrawOverlays = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initView();
        this.initService();
    }

    private void checkUsbConnection() {
        new Thread("checkUsbConnectionThread") {
            @Override
            public void run() {
                while (true) {
                    mTvMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = getBaseContext().registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
                            boolean connected = intent.getExtras().getBoolean("connected");
                            Toast.makeText(getBaseContext(), "Connected=" + connected, Toast.LENGTH_SHORT).show();
                        }
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void initService() {
        if (this.requestPermissions())
            this.startService(new Intent(MainActivity.this, AppCheckServices.class));
        else {
            boolean canDrawOverlays = (VERSION.SDK_INT < Build.VERSION_CODES.M) ? true : this.checkCanDrawOverlays();
            boolean usageAccess = (VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) ? true : this.checkUsageAccess();
            this.mTvMsg.setText("canDrawOverlays=" + canDrawOverlays + ", usageAccess=" + usageAccess);
        }
    }

    @TargetApi(23)
    private boolean checkCanDrawOverlays() {
        if (VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;
        return Settings.canDrawOverlays(this);
    }

    @TargetApi(21)
    private boolean checkUsageAccess() {
        if (VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return false;

        PackageManager pm = this.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(this.getPackageName(), 0);
            AppOpsManager aom = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
            int mode = aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initView() {
        this.mTvMsg = this.findViewById(R.id.tv_msg);
    }

    private boolean requestPermissions() {
        if (this.isFirstCheckCanDrawOverlays && VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.isFirstCheckCanDrawOverlays = false;
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                this.startActivityForResult(intent, REQUEST_CODE_CAN_DRAW_OVERLAYS);
                return false;
            }
        }
        if (this.isFirstCheckUsageAccess && VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.isFirstCheckUsageAccess = false;
            if (!this.checkUsageAccess()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_USAGE_ACCESS);
                return false;
            }
        }
//        if (!this.isFirstCheckUsageAccess && !this.isFirstCheckCanDrawOverlays)
//            return false;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_USAGE_ACCESS || requestCode == REQUEST_CODE_CAN_DRAW_OVERLAYS) {
            Toast.makeText(this, "requestCode:" + requestCode + ", resultCode: " + resultCode, Toast.LENGTH_SHORT).show();
            this.initService();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getTopActivtyFromLolipopOnwards() {
        String topPackageName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap< Long, UsageStats > mySortedMap = new TreeMap< Long, UsageStats >();
                for (UsageStats usageStats: stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    Log.e("TopPackage Name", topPackageName);
                }
            }
        }
        return topPackageName;
    }
}