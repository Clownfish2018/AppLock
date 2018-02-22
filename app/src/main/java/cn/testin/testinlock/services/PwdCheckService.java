package cn.testin.testinlock.services;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
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
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.testin.testinlock.R;
import cn.testin.testinlock.widget.PwdLockingDialog;
import cn.testin.testinlock.widget.PwdLockingDialog2;

/**
 * Created by qipengfei on 2/14/2018.
 */
public class PwdCheckService extends AbstractService {
//    private PwdLockingDialog mPwdLockingDialog;
    private static volatile PwdCheckService sInstance;
    private String mLastPkgName = "";
    private static final int SHOW_DIALOG = 0x100;
    private static final int HIDE_DIALOG = 0x200;

    private Handler mHandler;

//////////////////////start//////////////////////
    private static final String TAG = PwdLockingDialog.class.getSimpleName();
    private static final String PASSWORD = "0000";

    private ImageView mIvBackgound;
    private View mProptsView;
    private final int mLayoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
    private Dialog mDialog;
//////////////////end//////////////////

    private PwdCheckService(Context context) {
        super(context);
//        this.init();
//        this.initListener();
//        this.mPwdLockingDialog = new PwdLockingDialog(context);
        this.initBackground();
        this.mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_DIALOG:
                        showDialog();
                        return true;
                    case HIDE_DIALOG:
                        hideDialog();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public static PwdCheckService getInstance(Context context) {
        if (sInstance == null) {
            synchronized (context) {
                if (sInstance == null)
                    sInstance = new PwdCheckService(context);
            }
        }
        return sInstance;
    }

///////////////////start////////////////////
    private void hideUnlockDialog() {
        this.mLastPkgName = "";
        try {
            if (this.mDialog != null) {
                if (this.mDialog.isShowing()) {
                    this.mDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        Log.d(TAG, "showDialog()-calling");
        this.init();
        this.initListener();
        this.mDialog.show();
    }

    private void initBackground() {
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
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
        windowManager.addView(this.mIvBackgound, params);
    }

private void init() {
    LayoutInflater layoutInflater = LayoutInflater.from(this.mContext);
    this.mProptsView = layoutInflater.inflate(R.layout.best_numboard_land, null);

    this.mDialog = new PwdLockingDialog2(this.mContext);
//    this.mDialog = new Dialog(this.mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//    this.mDialog.setCanceledOnTouchOutside(false);
//    this.mDialog.setCancelable(false);
//    this.mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//    this.mDialog.getWindow().setType(this.mLayoutType);
//    this.mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//    this.mDialog.setContentView(this.mProptsView);
//    this.mDialog.getWindow().setGravity(Gravity.CENTER);
}

    private void initListener() {
        this.mDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    pressHome();
                }
                return true;
            }
        });
        this.initPromptsViewListener(this.mProptsView);
    }

    private void pressHome() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(startMain);
    }

    private void initPromptsViewListener(View promptsView) {
        final EditText editText = (EditText) promptsView.findViewById(R.id.numboard_pwd_edittext);
        Button button = (Button) promptsView.findViewById(R.id.btn_1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(editText.getText().toString() + "1");
            }
        });
        promptsView.findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("2");
            }
        });
        promptsView.findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("3");
            }
        });
        promptsView.findViewById(R.id.btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("4");
            }
        });
        promptsView.findViewById(R.id.btn_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("5");
            }
        });
        promptsView.findViewById(R.id.btn_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("6");
            }
        });
        promptsView.findViewById(R.id.btn_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("7");
            }
        });
        promptsView.findViewById(R.id.btn_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("8");
            }
        });
        promptsView.findViewById(R.id.btn_9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("9");
            }
        });
        promptsView.findViewById(R.id.btn_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("0");
            }
        });
        promptsView.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = editText.getText().length();
                if (length > 0) {
                    editText.getText().delete(length - 1, length);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (PASSWORD.equals(s.toString())) {//sharedPreference.getPassword(context)
                    mDialog.dismiss();
                    Toast.makeText(mContext, "App成功解锁", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "App成功解锁");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
///////////////////////////////////////////

    @Override
    public void startWork(String packageName) {}

    @Override
    public void doWork() {
        final String[] topActivities = this.getTopActivities();
        if (topActivities == null || topActivities.length == 0)
            return;
        String topActivity = topActivities[0];
        if (this.getDefaultSettings().contains(topActivities[0]) && !this.mLastPkgName.equals(topActivities[0])) {
            Log.d(TAG, "发现需要锁定的应用：" + topActivities[0]);
            Message msg = this.mHandler.obtainMessage(SHOW_DIALOG);
            this.mHandler.sendMessage(msg);
            this.mLastPkgName = topActivities[0];
//            if (imageView != null) {
//                imageView.post(new Runnable() {
//                    public void run() {
//                        if (!last_pkg_name.equals(topActivities[0])) {
//                            showUnlockDialog();
//                            last_pkg_name = topActivities[0];
//                        }
//                    }
//                });
//            }
        } else {
            Message msg = this.mHandler.obtainMessage(HIDE_DIALOG);
            this.mHandler.sendMessage(msg);
//            if (imageView != null) {
//                imageView.post(new Runnable() {
//                    public void run() {
//                        hideUnlockDialog();
//                    }
//                });
//            }
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

    }

//    private void showDialog() {
//        this.mPwdLockingDialog.show();
//    }

    private void hideDialog() {
        this.mLastPkgName = "";
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
//        this.mPwdLockingDialog.dismiss();
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
            ResolveInfo res = this.mContext.getPackageManager().resolveActivity(
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
            Log.d(TAG, "getTopActivityLollipopOnwards(): " + topPackageName.toString());
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
}