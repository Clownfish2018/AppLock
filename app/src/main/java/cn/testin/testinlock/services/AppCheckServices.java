package cn.testin.testinlock.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import cn.testin.testinlock.MainActivity;
import cn.testin.testinlock.R;

/**
 * Created by qipengfei on 2/14/2018.
 */
public class AppCheckServices extends Service {
    public static final String TAG = AppCheckServices.class.getSimpleName();

    private PwdCheckService mPwdCheckService;
    private UsbConnectionService mUsbConnectionService;

    @Override
    public void onCreate() {
        super.onCreate();
        this.showNotification();
        this.mPwdCheckService = new PwdCheckService(this.getApplicationContext());
        this.mPwdCheckService.start();
        this.mUsbConnectionService = new UsbConnectionService((this.getApplicationContext()));
        this.mUsbConnectionService.start();
    }

    private void showNotification() {
        try {
//			if (android.os.Build.VERSION.SDK_INT >= 5) {
            // 创建一个NotificationManager的引用
            // NotificationManager notificationManager =
            // (NotificationManager)
            //

            final String CHANNEL_ID = this.getClass().getName();
            final String CHANNEL_NAME = this.getClass().getSimpleName();
            NotificationManager nm = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentItent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            NotificationChannel nc = null;
            Notification notification = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nc = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                nc.enableLights(true);
                nc.setLightColor(Color.RED);
                nc.setShowBadge(true);
                nc.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(nc);
                Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);
                notification = builder.setContentIntent(contentItent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("TestinLock")
                        .setContentText("Keep service").build();
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                notification = builder.setContentIntent(contentItent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("TestinLock")
                        .setContentText("Keep service").build();
            }
            notification.flags = Notification.FLAG_ONGOING_EVENT
                    | Notification.FLAG_NO_CLEAR;
            // notificationManager.notify(0, notification);
            startForeground(Notification.FLAG_INSISTENT, notification);
//			}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        this.mUsbConnectionService.stop();
    }
}