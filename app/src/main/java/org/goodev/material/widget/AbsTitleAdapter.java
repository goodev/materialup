package org.goodev.material.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.goodev.material.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by go on 2016/1/20.
 */
public class AbsTitleAdapter extends BaseAdapter {
    private List<String> mItems = new ArrayList<>();

    Context mContext;

    public AbsTitleAdapter(Context context) {
        mContext = context;
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(String String) {
        mItems.add(String);
    }

    public void addItems(List<String> StringList) {
        mItems.addAll(StringList);
    }

    public void addItems(String[] StringList) {
        mItems.addAll(Arrays.asList(StringList));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = getLayoutInflater().inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));

        return view;
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = getLayoutInflater().inflate(R.layout.
                    toolbar_spinner_item_actionbar, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position) : "";
    }
}