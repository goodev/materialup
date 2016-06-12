package org.goodev.ui;

import android.support.v4.app.Fragment;

import org.goodev.material.App;

public class StatFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        App app = (App) getContext().getApplicationContext();
        Stats stats = app.getStats();
        stats.track(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
