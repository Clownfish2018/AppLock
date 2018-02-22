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

    private PwdCheckService mPwdCheckService;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mPwdCheckService = new PwdCheckService(this.getApplicationContext());
        this.mPwdCheckService.start();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mPwdCheckService.stop();
    }
}