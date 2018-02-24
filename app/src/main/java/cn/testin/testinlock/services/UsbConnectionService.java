package cn.testin.testinlock.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import cn.testin.testinlock.AppLockApplication;
import cn.testin.testinlock.MainActivity;

/**
 * Created by qipengfei on 2/23/2018.
 */
public class UsbConnectionService extends AbstractService {
    private static final String TAG = UsbConnectionService.class.getSimpleName();
    private static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    private static final String USB_CONNECTED = "connected";
    private static final String USB_FUNCTION_ADB = "adb";

    private BroadcastReceiver mUsbStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_STATE.equals(action)) {
                if (intent.getExtras().containsKey(USB_CONNECTED)) {
                    AppLockApplication.usbConnected = intent.getExtras().getBoolean(USB_CONNECTED);
                    Log.d(TAG, "Usb connected=" + AppLockApplication.usbConnected);
                }
                if (intent.getExtras().containsKey(USB_FUNCTION_ADB)) {
                    AppLockApplication.adbEnabled = intent.getExtras().getBoolean(USB_FUNCTION_ADB);
                    Log.d(TAG, "Adb enabled=" + AppLockApplication.adbEnabled);
                }
            }
        }
    };

    public UsbConnectionService(@NonNull Context context) {
        super(context);
    }

    @Override
    public void startWork(String packageName) {
    }

    @Override
    public void doWork() {
    }

    @Override
    public void stopWork() {
    }

    @Override
    public void start() {
        this.mContext.registerReceiver(this.mUsbStateBroadcastReceiver, new IntentFilter(ACTION_USB_STATE));
    }

    @Override
    public void stop() {
        this.mContext.unregisterReceiver(this.mUsbStateBroadcastReceiver);
    }
}