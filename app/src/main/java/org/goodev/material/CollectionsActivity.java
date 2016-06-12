package org.goodev.material;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.goodev.material.ui.CollectionPostsFragment;
import org.goodev.material.util.Launcher;

public class CollectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String path = getIntent().getStringExtra(Launcher.EXTRA_ID);
        String title = getIntent().getStringExtra(Launcher.EXTRA_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        if (savedInstanceState == null) {
            CollectionPostsFragment fragment = CollectionPostsFragment.newIns(path);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        }
    }

}
