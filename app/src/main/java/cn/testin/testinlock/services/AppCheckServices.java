package cn.testin.testinlock.services;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import cn.testin.testinlock.R;
import cn.testin.testinlock.widget.PwdLockingDialog2;

/**
 * Created by amitshekhar on 28/04/15.
 */
public class AppCheckServices extends Service {
    public static final String TAG = AppCheckServices.class.getSimpleName();
    private String mLastPkgName = "";
    private final int mLayoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
    private static long DELAY_TIME = 0l;
    private static long INTERVAL = 100l;

    private Context mContext;
    private Timer mTimer;
    private ImageView mIvBackgound;
    private WindowManager windowManager;
    private Dialog mDialog;
//    SharedPreference sharedPreference;
    List<String> pakageName;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        sharedPreference = new SharedPreference();
//        if (sharedPreference != null) {
//            pakageName = sharedPreference.getLocked(mContext);
//        }
        mTimer = new Timer("AppCheckServices");
        mTimer.schedule(updateTask, DELAY_TIME, INTERVAL);

        windowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        this.mIvBackgound = new ImageView(this.mContext);
        this.mIvBackgound.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                this.mLayoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
        params.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
        windowManager.addView(this.mIvBackgound, params);
    }

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
//            if (sharedPreference != null) {
//                pakageName = sharedPreference.getLocked(mContext);
//            }
            final String[] topActivities = AppCheckServices.this.getTopActivities();
            if (topActivities == null || topActivities.length == 0)
                return;
            if (getDefaultSettings().contains(topActivities[0])) {
                if (mIvBackgound != null && !mLastPkgName.equals(topActivities[0])) {
                    mIvBackgound.post(new Runnable() {
                        public void run() {
//                            if (!mLastPkgName.equals(topActivities[0])) {
                                showDialog();
                                mLastPkgName = topActivities[0];
//                            }
                        }
                    });
                }
            } else {
                if (mIvBackgound != null) {
                    mIvBackgound.post(new Runnable() {
                        public void run() {
                            hideDialog();
                        }
                    });
                }
            }
        }
    };

    private void hideDialog() {
        this.mLastPkgName = "";
        try {
            if (mDialog != null && this.mDialog.isShowing()) {
                this.mDialog.dismiss();
                this.mDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        if (mContext == null)
            mContext = getApplicationContext();
//        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//        View promptsView = layoutInflater.inflate(R.layout.best_numboard_land, null);
//        this.initPromptsView(promptsView);
        if (this.mDialog != null && this.mDialog.isShowing()) {
            return;
//            this.mDialog.dismiss();
//            this.mDialog = null;
        }

        this.mDialog = new PwdLockingDialog2(this.mContext);
        mDialog.show();
    }

//    private void initPromptsView(View promptsView) {
//        final EditText editText = (EditText) promptsView.findViewById(R.id.numboard_pwd_edittext);
//        Button button = (Button) promptsView.findViewById(R.id.btn_1);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("1");
//            }
//        });
//        promptsView.findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("2");
//            }
//        });
//        promptsView.findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("3");
//            }
//        });
//        promptsView.findViewById(R.id.btn_4).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("4");
//            }
//        });
//        promptsView.findViewById(R.id.btn_5).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("5");
//            }
//        });
//        promptsView.findViewById(R.id.btn_6).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("6");
//            }
//        });
//        promptsView.findViewById(R.id.btn_7).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("7");
//            }
//        });
//        promptsView.findViewById(R.id.btn_8).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("8");
//            }
//        });
//        promptsView.findViewById(R.id.btn_9).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("9");
//            }
//        });
//        promptsView.findViewById(R.id.btn_0).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.append("0");
//            }
//        });
//        promptsView.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int length = editText.getText().length();
//                if (length > 0) {
//                    editText.getText().delete(length - 1, length);
//                }
//            }
//        });
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (PASSWORD.equals(s.toString())) {//sharedPreference.getPassword(mContext)
//                    mDialog.dismiss();
//                    Log.i(TAG, "App成功解锁");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

        }
        /* We want this service to continue running until it is explicitly
        * stopped, so return sticky.
        */
        return START_STICKY;
    }

    @TargetApi(20)
    private String[] getTopActivityBeforeLollipop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e(TAG, "getTopActivityBeforeLollipop()手机版本号不适配, SDK_VER:" + Build.VERSION.SDK_INT);
        }
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
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
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
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

        UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;
        if (mIvBackgound != null) {
            windowManager.removeView(mIvBackgound);
        }
        /**** added to fix the bug of view not attached to window manager ****/
        try {
            if (mDialog != null) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}