package com.obigo.baidumusic.standard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.obigo.baidumusic.standard.R;

public class CategoryView extends LinearLayout {
    private final int[] mTypeString = {0, R.string.favorite, R.string.theme,
            R.string.radio, R.string.popular, R.string.billboard,
            R.string.newsong, R.string.search};

    private final int[] mTypeDrawable = {0, R.drawable.tab_icon_favorite,
            R.drawable.tab_icon_theme, R.drawable.tab_icon_radio,
            R.drawable.tab_icon_popular, R.drawable.tab_icon_billboard,
            R.drawable.tab_icon_new, R.drawable.tab_icon_search };

    private TextView mTitle;
    private Context mContext;
    private LinearLayout mLayout;

    public CategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CategoryView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mContext = context;
        
        View v = inflater.inflate(R.layout.category_view, this);
        if(v instanceof LinearLayout) {
            mLayout = (LinearLayout)v;

            View v1 = mLayout.findViewById(R.id.category_title);
            if(v1 instanceof TextView) {
                mTitle = (TextView)v1;
            }

            mLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void setCategory(int category) {
        mLayout.setVisibility(View.VISIBLE);

        mTitle.setText(mContext.getResources().getString(mTypeString[category]));
        mTitle.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources()
                .getDrawable(mTypeDrawable[category]), null, null, null);
    }
}
