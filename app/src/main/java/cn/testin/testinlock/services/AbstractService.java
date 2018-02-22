package cn.testin.testinlock.services;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qipengfei on 2/14/2018.
 */
public abstract class AbstractService implements IService {
    protected final String TAG = this.getClass().getSimpleName();
    private static long DELAY_TIME = 0l;
    private static long INTERVAL = 100l;

    protected Context mContext;

    private Timer mTimer;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            doWork();
        }
    };

    public AbstractService(@NonNull Context context) {
        this.mContext = context;
    }

    @Override
    public void start() {
        if (this.mTimer == null) {
            this.mTimer = new Timer(this.getClass().getSimpleName() + "Timer");
            this.mTimer.scheduleAtFixedRate(this.mTimerTask, DELAY_TIME, INTERVAL);
        }
    }

    @Override
    public void stop() {
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
    }
}