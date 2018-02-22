package cn.testin.testinlock.services;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import cn.testin.testinlock.R;
import cn.testin.testinlock.widget.TestinDialog;

/**
 * Created by amitshekhar on 28/04/15.
 */
public class AppCheckServices3 extends Service {

    public static final String TAG = "AppCheckServices3";
    private final int mLayoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
    private Context context = null;
    private Timer timer;
    ImageView imageView;
    private WindowManager windowManager;
    private Dialog dialog;
    public static String currentApp = "";
    public static String previousApp = "";
    List<String> pakageName;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        this.pakageName = this.getDefaultSettings();
        timer = new Timer("AppCheckServices3");
        timer.schedule(updateTask, 1000L, 1000L);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageView = new ImageView(this);
        imageView.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,//WRAP_CONTENT
                WindowManager.LayoutParams.WRAP_CONTENT,//WRAP_CONTENT
                this.mLayoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
        params.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
        windowManager.addView(imageView, params);

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
            ResolveInfo res = context.getPackageManager().resolveActivity(
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

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            if (isConcernedAppIsInForeground()) {
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            if (!currentApp.matches(previousApp)) {
                                showUnlockDialog();
                                previousApp = currentApp;
                            }
                        }
                    });
                }
            } else {
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            hideUnlockDialog();
                        }
                    });
                }
            }
        }
    };

    void showUnlockDialog() {
        showDialog();
    }

    void hideUnlockDialog() {
        previousApp = "";
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(11)
    void showDialog() {
        if (context == null)
            context = getApplicationContext();
        Log.d("AppCheckServices3", "showDialog()");
        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        View promptsView = layoutInflater.inflate(R.layout.popup_unlock, null);
//        Lock9View lock9View = (Lock9View) promptsView.findViewById(R.id.lock_9_view);
//        FlatButton forgetPassword = (FlatButton) promptsView.findViewById(R.id.forgetPassword);
//        lock9View.setCallBack(new Lock9View.CallBack() {
//            @Override
//            public void onFinish(String password) {
//                if (password.matches(sharedPreference.getPassword(context))) {
//                    dialog.dismiss();
//                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");
//                } else {
//                    Toast.makeText(getApplicationContext(), "Wrong Pattern Try Again", Toast.LENGTH_SHORT).show();
//                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Wrong Password", "wrong_password", "");
//                }
//            }
//        });
        View promptsView = layoutInflater.inflate(R.layout.layout_dialog, null);
        final EditText editText = (EditText) promptsView.findViewById(R.id.et_pwd);

        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        dialog = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
//                .setView(new EditText(context))
//                .setTitle("title").setPositiveButton("ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                System.out.println("ok");
//            }
//        }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setType(this.mLayoutType);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(new TestinDialog(context));
        dialog.getWindow().setGravity(Gravity.CENTER);
//
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
                return true;
            }
        });

        dialog.show();

    }

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

    public boolean isConcernedAppIsInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
        if (Build.VERSION.SDK_INT <= 20) {
            if (task.size() > 0) {
                ComponentName componentInfo = task.get(0).topActivity;
                for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                    if (componentInfo.getPackageName().equals(pakageName.get(i))) {
                        currentApp = pakageName.get(i);
                        return true;
                    }
                }
            }
        } else {
            String mpackageName = manager.getRunningAppProcesses().get(0).processName;
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) {
                    Log.d(TAG,"isEmpty Yes");
                    mpackageName = "";
                }else {
                    Long a = runningTask.lastKey();
                    mpackageName = runningTask.get(a).getPackageName();
                    Log.d(TAG,"isEmpty No : "+mpackageName);
                }
            }

            for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                if (mpackageName.equals(pakageName.get(i))) {
                    currentApp = pakageName.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        if (imageView != null) {
            windowManager.removeView(imageView);
        }
        /**** added to fix the bug of view not attached to window manager ****/
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}