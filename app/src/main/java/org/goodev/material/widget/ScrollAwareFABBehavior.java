package org.goodev.material.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * ScrollAwareFABBehavior for API 14+
 */
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {
    private static final int TRANSLATE_DURATION_MILLIS = 200;
    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private boolean mIsAnimatingOut = false;

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            toggle(child, false, false);
        } else if (dyConsumed < 0 && child.getVisibility() == View.INVISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            toggle(child, true, false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void toggle(final FloatingActionButton button, final boolean visible, boolean force) {
        boolean show = button.getVisibility() == View.VISIBLE;
        if (show != visible || force) {
            int height = button.getHeight();
            if (height == 0 && !force) {
                ViewTreeObserver vto = button.getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ViewTreeObserver currentVto = button.getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            toggle(button, visible, true);
                            return true;
                        }
                    });
                    return;
                }
            }
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) button.getLayoutParams();
            int translationY = visible ? 0 : height + params.bottomMargin;
            Animator.AnimatorListener listener = visible ? null : new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                    button.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                }
            };
            button.setVisibility(View.VISIBLE);
            button.animate().setInterpolator(INTERPOLATOR)
                    .setDuration(TRANSLATE_DURATION_MILLIS)
                    .translationY(translationY)
                    .setListener(listener);
        }
    }
}