package org.goodev.material;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.squareup.okhttp.ResponseBody;

import org.goodev.material.api.Api;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateActivity extends AppCompatActivity {

    @Bind(R.id.urlInputLayout)
    TextInputLayout mUrlInputLayout;
    @Bind(R.id.urlEditText)
    EditText mUrlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClick(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fabClick(View view) {
        String url = mUrlEditText.getText().toString();
        Api.extractUrl(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> handleRes(r));
    }

    private void handleRes(ResponseBody r) {
        try {
            System.out.println(r.string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
