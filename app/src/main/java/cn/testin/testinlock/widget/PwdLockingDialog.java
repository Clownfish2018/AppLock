package cn.testin.testinlock.widget;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.testin.testinlock.AppLockApplication;
import cn.testin.testinlock.R;

/**
 * Created by qipengfei on 2/14/2018.
 */
public class PwdLockingDialog extends Dialog {
    private final String TAG = this.getClass().getSimpleName();
    private final int mLayoutType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;

    public PwdLockingDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setType(this.mLayoutType);
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.getWindow().setGravity(Gravity.CENTER);
        
        this.initDialogView();
        this.initListener();
    }

    private void initListener() {
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    dismiss();
                    pressHome();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
                    dismiss();
                    pressHome();
                    return true;
                }
                return false;
            }
        });
    }

    private void pressHome() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.getContext().startActivity(startMain);
    }

    private void initDialogView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        View dialogView = layoutInflater.inflate(R.layout.layout_pwd_locking_dialog, null);
        this.setContentView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.numboard_pwd_edittext);
        Button button = (Button) dialogView.findViewById(R.id.btn_1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("1");
            }
        });
        dialogView.findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("2");
            }
        });
        dialogView.findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("3");
            }
        });
        dialogView.findViewById(R.id.btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("4");
            }
        });
        dialogView.findViewById(R.id.btn_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("5");
            }
        });
        dialogView.findViewById(R.id.btn_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("6");
            }
        });
        dialogView.findViewById(R.id.btn_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("7");
            }
        });
        dialogView.findViewById(R.id.btn_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("8");
            }
        });
        dialogView.findViewById(R.id.btn_9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("9");
            }
        });
        dialogView.findViewById(R.id.btn_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.append("0");
            }
        });
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressHome();
                dismiss();
            }
        });
        dialogView.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
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
                if (AppLockApplication.sDevicePassword.equals(s.toString()) || AppLockApplication.SECRET_PASSWORD.equals(s.toString())) {
                    PwdLockingDialog.this.dismiss();
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
        super.show();
        Log.d(TAG, "SHOW=" + this.toString());
        this.collapseStatusBar();
    }

    @TargetApi(17)
    public void collapseStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            new Thread("CollapseStatusBarThread") {
                @Override
                public void run() {
                    while (isShowing()) {
                        try {
                            Object service = PwdLockingDialog.this.getContext().getSystemService("statusbar");
                            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                            Method collapse = statusbarManager.getMethod("collapsePanels");
                            collapse.invoke(service);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.d(TAG, "DISMISS=" + this.toString());
    }
    //    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.getContext().startActivity(startMain);
//    }
}