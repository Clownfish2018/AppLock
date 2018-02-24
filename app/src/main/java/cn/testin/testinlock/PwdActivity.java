package cn.testin.testinlock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 * am start -n cn.testin.testinlock/.PwdActivity<p/>
 * Created by qipengfei on 2/23/2018.
 */
public class PwdActivity extends Activity {
    private TextView mTvDevPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        this.initView();
    }

    private void initView() {
        this.mTvDevPwd = this.findViewById(R.id.tv_dev_pwd);
    }

    @Override
    protected void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        String devicePassword = sp.getString("devPwd", "UNKNOWN");
        this.mTvDevPwd.setText(devicePassword);
        super.onResume();
    }
}