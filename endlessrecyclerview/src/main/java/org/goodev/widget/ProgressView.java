package org.goodev.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by ADMIN on 2014/12/24.
 */
public class ProgressView extends View {

    private CircularProgressDrawable mDrawable;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressView, defStyleAttr, 0);
        int color = Color.parseColor("#C2185B");
        TypedArray aa = context.obtainStyledAttributes(new int[]{R.attr.colorAccent});
        color = a.getColor(0, color);
        aa.recycle();
        color = a.getColor(R.styleable.ProgressView_progressColor, color);
        a.recycle();
        mDrawable = new CircularProgressDrawable(color, 8);
        mDrawable.setCallback(this);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mDrawable == null) {
            return;
        }
        if (visibility == VISIBLE) {
            mDrawable.start();
        } else {
            mDrawable.stop();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mDrawable.draw(canvas);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    public void start() {
        if (mDrawable != null) {
            mDrawable.start();
        }
    }
}