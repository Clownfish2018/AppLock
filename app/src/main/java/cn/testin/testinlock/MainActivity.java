package cn.testin.testinlock;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.testin.testinlock.services.AppCheckServices;

public class MainActivity extends Activity {
    private TextView mTvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initView();
        this.initData();

        this.initService();
    }

    private void initService() {
        this.startService(new Intent(MainActivity.this, AppCheckServices.class));
    }

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

    private void initData() {
        boolean usageAccess = this.checkUsageAccess();
        this.mTvMsg.setText("" + usageAccess);
        Log.d("TESTIN_LOCK", "" + usageAccess + this.getTopActivtyFromLolipopOnwards());

        if (!usageAccess && VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//            intent.setData(Uri.parse(" package:" + this.getPackageName()));
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            this.mTvMsg.setText("" + this.checkUsageAccess() + this.getTopActivtyFromLolipopOnwards());
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