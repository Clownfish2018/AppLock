package cn.testin.testinlock.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
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

import cn.testin.testinlock.R;

/**
 * Created by qipengfei on 2/14/2018.
 */
public class PwdLockingDialog extends Dialog {
    private static final String TAG = PwdLockingDialog.class.getSimpleName();
    private static final String PASSWORD = "0000";

    private ImageView mIvBackgound;
    private View mProptsView;
    private final int mLayoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;

    public PwdLockingDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.init();
        this.initListener();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        this.mProptsView = layoutInflater.inflate(R.layout.best_numboard_land, null);

        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setType(this.mLayoutType);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.setContentView(this.mProptsView);
        this.getWindow().setGravity(Gravity.CENTER);

        WindowManager windowManager = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        this.mIvBackgound = new ImageView(this.getContext());
        this.mIvBackgound.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                this.mLayoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = ((this.getContext().getResources().getDisplayMetrics().widthPixels) / 2);
        params.y = ((this.getContext().getResources().getDisplayMetrics().heightPixels) / 2);
        windowManager.addView(this.mIvBackgound, params);
    }

    private void initListener() {
        this.setOnKeyListener(new Dialog.OnKeyListener() {
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
        this.getOwnerActivity().startActivity(startMain);
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
                    dismiss();
                    Toast.makeText(getContext(), "App成功解锁", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "App成功解锁");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void show() {
        if (this.mIvBackgound != null && !this.isShowing())
            this.mIvBackgound.post(new Runnable() {
                @Override
                public void run() {
                    show();
                }
            });
//      super.show();
    }

    @Override
    public void dismiss() {
        if (this.mIvBackgound != null && this.isShowing())
            this.mIvBackgound.post(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
    }

    public void destroy() {
        if (this.mIvBackgound != null) {
            WindowManager windowManager = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(this.mIvBackgound);
            this.mIvBackgound = null;
        } else {
            Log.e(TAG, "PwdLockingDialog had been destoried.");
        }
    }
}