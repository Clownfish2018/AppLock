package cn.testin.testinlock.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import cn.testin.testinlock.AppLockApplication;

/**
 * Created by qipengfei on 2/13/2018.
 */

public class NumBoardButton extends ImageButton implements ValueAnimator.AnimatorUpdateListener {
    private final float a = 0.3F;
    private BitmapDrawable b;
    private boolean c = false;
    private boolean d = false;
    private float e = 1.0F;
    private boolean f = false;
    private ValueAnimator g;
    private AppLockApplication appLockApplication;

    public NumBoardButton(Context paramContext) {
        super(paramContext);
//        a(paramContext);
    }

    public NumBoardButton(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
//        a(paramContext);
    }

    public NumBoardButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
//        a(paramContext);
    }

    public NumBoardButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
//        a(paramContext);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }

//    public static BitmapDrawable a(Context paramContext, String paramString) {
//        Bitmap localBitmap = (AppLockApplication) paramContext.getApplicationContext().f();
//        try {
//            int i = Integer.parseInt(paramString);
//            int j = localBitmap.getWidth() / 12;
//            paramContext = new BitmapDrawable(paramContext.getResources(), Bitmap.createBitmap(localBitmap, i * j, 0, j, localBitmap.getHeight()));
//            return paramContext;
//        } catch (Exception paramContext) {
//        }
//        return null;
//    }
//
//    private void a(Context paramContext) {
//        if (this.appLockApplication == null) {
//            this.appLockApplication = (AppLockApplication) paramContext.getApplicationContext();
//        }
//    }
//
//    private void a(boolean paramBoolean) {
//        try {
//            if (this.g != null) {
//                this.g.cancel();
//            }
//            this.f = paramBoolean;
//            this.g = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});
//            this.g.setDuration(100L);
//            this.g.setTarget(this);
//            this.g.addUpdateListener(this);
//            this.g.start();
//            return;
//        } catch (Exception localException) {
//            for (; ; ) {
//            }
//        }
//    }
//
//    public void a() {
//        if (this.b != null) {
//        }
//        try {
//            this.b.getBitmap().recycle();
//            return;
//        } catch (Exception localException) {
//        }
//    }
//
//    public void a(Object paramObject, BitmapDrawable paramBitmapDrawable) {
//        if ((paramBitmapDrawable == null) || (paramBitmapDrawable.getBitmap().isRecycled())) {
//            setTag(paramObject);
//            return;
//        }
//        super.setTag(paramObject);
//        this.b = paramBitmapDrawable;
//        invalidate();
//    }
//
//    @Override
//    public void onAnimationUpdate(ValueAnimator paramValueAnimator) {
//        if (this.f) {
//            setKeyScaleSize(1.3F - paramValueAnimator.getAnimatedFraction() * 0.3F);
//            return;
//        }
//        setKeyScaleSize(1.0F + paramValueAnimator.getAnimatedFraction() * 0.3F);
//    }
//
//    protected void onDraw(Canvas paramCanvas) {
//        float f1 = 1.0F;
//        super.onDraw(paramCanvas);
//        if (!this.c) {
//        }
//        BitmapDrawable localBitmapDrawable;
//        do {
//            return;
//            if ((this.b == null) && (getTag() != null)) {
//                this.b = a(getContext(), getTag().toString());
//            }
//            localBitmapDrawable = this.b;
//        } while (localBitmapDrawable == null);
//        float f2 = getHeight();
//        if (getDrawable() != null) {
//            f1 = 1.0F * f2 / getDrawable().getIntrinsicHeight();
//        }
//        int i = (int) (f1 * (int) (localBitmapDrawable.getIntrinsicWidth() * this.e));
//        int j = (getWidth() - i) / 2;
//        int k = (int) ((f2 - i) / 2.0F);
//        localBitmapDrawable.setBounds(j, k, j + i, i + k);
//        if (this.d) {
//        }
//        for (i = 0; ; i = this.appLockApplication.n) {
//            localBitmapDrawable.setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
//            localBitmapDrawable.draw(paramCanvas);
//            return;
//        }
//    }
//
//    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
//        switch (paramMotionEvent.getAction()) {
//        }
//        for (; ; ) {
//            return super.onTouchEvent(paramMotionEvent);
//            a(false);
//            continue;
//            a(true);
//        }
//    }
//
//    public void setIgnoreCustomedColor(boolean paramBoolean) {
//        this.d = paramBoolean;
//    }
//
//    public void setKeyScaleSize(float paramFloat) {
//        this.e = paramFloat;
//        invalidate();
//    }
//
//    public void setNeedDrawNumber(boolean paramBoolean) {
//        this.c = paramBoolean;
//        if (this.c) {
//            LollipopDrawablesCompat.setBackground(this, 2130837929, null);
//        }
//    }
//
//    public void setTag(Object paramObject) {
//        super.setTag(paramObject);
//        a();
//        this.b = null;
//        invalidate();
//    }
}