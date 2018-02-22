package cn.testin.testinlock;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by qipengfei on 2/13/2018.
 */

public class AppLockApplication extends Application {
    private static AppLockApplication sAppInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppInstance = this;
    }

    /**
     * Get an instance of application
     *
     * @return
     */
    public static synchronized AppLockApplication getInstance() {
        return sAppInstance;
    }
}