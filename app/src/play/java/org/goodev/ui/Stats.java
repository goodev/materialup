package org.goodev.ui;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.goodev.material.App;
import org.goodev.material.util.L;

public class Stats {

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker(Context ctx) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(ctx.getApplicationContext());
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-68232106-2");
        }
        return mTracker;
    }

    public void track(String name) {
        if (mTracker == null) {
            getDefaultTracker(App.getIns());
        }
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void track(String cat, String name) {
        if (mTracker == null) {
            getDefaultTracker(App.getIns());
        }
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(cat)
                .setAction(name)
                .build());
    }

}
