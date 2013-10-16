package com.obigo.baidumusic.standard.util;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.obigo.baidumusic.standard.R;

public class ViewCache {
    private View mListView;
    private TextView mTitle;
    private ImageView mAlbum;
    private Button mButton;

    public ViewCache(View baseView) {
        this.mListView = baseView;
    }

    public TextView getTextView() {
        if (mTitle == null) {
            View v = mListView.findViewById(R.id.title);
            if(v instanceof TextView) {
                mTitle = (TextView)v;
            }
        }
        return mTitle;
    }

    public ImageView getImageView() {
        if (mAlbum == null) {
            View v = mListView.findViewById(R.id.album);
            if(v instanceof ImageView) {
                mAlbum = (ImageView)v;
            }
        }
        return mAlbum;
    }

    public Button getButtonView() {
        if (mButton == null) {
            View v = mListView.findViewById(R.id.delete);
            if(v instanceof Button) {
                mButton = (Button)v;
            }
        }
        return mButton;
    }
}
