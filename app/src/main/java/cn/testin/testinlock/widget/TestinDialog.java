package cn.testin.testinlock.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.testin.testinlock.R;

/**
 * Created by qipengfei on 2/22/2018.
 */

public class TestinDialog extends RelativeLayout {
    public TestinDialog(Context context) {
        super(context);
        this.initView();
    }

    public TestinDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public TestinDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    private void initView() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
//        this.setBackgroundResource(R.drawable.num_background);
        this.setBackgroundColor(this.getResources().getColor(android.R.color.white));

        EditText etPwd = new EditText(this.getContext());
        LayoutParams etlayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        etlayoutParams.setMargins(this.dpToPx(20), 0, this.dpToPx(20), 0);
        etlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        etPwd.setLayoutParams(etlayoutParams);

        TextView tvTitle = new TextView(this.getContext());
        tvTitle.setText(R.string.app_name);
        tvTitle.setText(R.string.app_name);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
        LayoutParams tvlayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tvlayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        tvlayoutParams.addRule(RelativeLayout.ABOVE, etPwd.getId());

        this.addView(etPwd, etlayoutParams);
        this.addView(tvTitle, tvlayoutParams);
    }

    private int dpToPx(int sizeInDP) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources().getDisplayMetrics());
    }
}