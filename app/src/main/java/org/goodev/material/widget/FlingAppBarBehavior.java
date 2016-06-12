package org.goodev.material.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import org.goodev.material.util.L;

/**
 * Created by yfcheng on 2015/12/30.
 */
public class FlingAppBarBehavior extends AppBarLayout.Behavior {
    private boolean isPositive;

    public FlingAppBarBehavior() {
        super();
    }

    public FlingAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {


//        if (velocityY < 0) {
//            consumed = false;
//        }
//        if (velocityY > 0 && !isPositive || velocityY < 0 && isPositive) {
//            velocityY = velocityY * -1;
//        }
        boolean fling = super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        if (velocityY < 0) {
            return true;
        }

        return fling;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        isPositive = dy > 0;
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }
}
