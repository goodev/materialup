package org.goodev.material;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.goodev.material.ui.SelectCollectionFragment;
import org.goodev.material.util.Launcher;

public class SelectCollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_collection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = getIntent().getLongExtra(Launcher.EXTRA_ID, -1);
        if (savedInstanceState == null) {
            SelectCollectionFragment fragment = SelectCollectionFragment.newIns(id);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
        }
    }

}
