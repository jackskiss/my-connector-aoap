package com.obigo.baidumusic.standard.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.music.model.Artist;
import com.baidu.music.model.Music;
import com.obigo.baidumusic.standard.BaseActivity;
import com.obigo.baidumusic.standard.MusicApplication;
import com.obigo.baidumusic.standard.MusicApplication.RegulationListener;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.player.ListDownloader;
import com.obigo.baidumusic.standard.player.ListDownloader.BaiduSearchListener;
import com.obigo.baidumusic.standard.playlist.PlayListActivity;
import com.obigo.baidumusic.standard.search.AutoSearchBar.OnSearchListener;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.view.ObigoDialog;

public class SearchActivity extends BaseActivity implements OnClickListener,
        OnSearchListener, OnItemClickListener, BaiduSearchListener {
    private static String TAG = "SearchActivity";

    private Button mBtnBack, mBtnAll;

    private List<Music> mItems;

    private AutoSearchBar mAutoSearchBar;

    private ListView mListView;
    private SearchListAdapter mAdapter;

    private ArrayList<TextView> mArtists;

    private String mQuery;

    private String[] orgStr = {"(", ")", "{", "}", "^", "[", "]", "*", "+",
            "$", "|" };
    private String[] convStr = {"\\(", "\\)", "\\{", "\\}", "\\^", "\\[",
            "\\]", "[*]", "[+]", "[$]", "[|]" };

    private static final int MODE_RECENT = 0;
    private static final int MODE_SEARCH_RESULT = 1;
    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        try {
            mBtnBack = findButton(R.id.back);
            mBtnBack.setOnClickListener(this);

            mBtnAll = findButton(R.id.all);
            mBtnAll.setOnClickListener(this);
            mBtnAll.setEnabled(false);

            mAutoSearchBar = findAutoSearchBar(R.id.search_bar);
            mAutoSearchBar.setSearchListener(this);

            mListView = findListView(R.id.search_list);
            mListView.setOnItemClickListener(this);

            mArtists = new ArrayList<TextView>();

            mArtists.add(findTextView(R.id.artist1));
            mArtists.add(findTextView(R.id.artist2));
            mArtists.add(findTextView(R.id.artist3));
            mArtists.add(findTextView(R.id.artist4));
            mArtists.add(findTextView(R.id.artist5));

            for (TextView artist : mArtists) {
                artist.setClickable(false);
                artist.setOnClickListener(this);
            }
        } catch (Exception e) {
            ObiLog.e(TAG, "findView error");
        }

        showLoading(getResources().getString(R.string.loading));

        ListDownloader.getInstance(this).requestHotArtistList();

        loadRecentQuery();
    }
    
    private AutoSearchBar findAutoSearchBar(int id) throws Exception {
        View v = findViewById(id);
        
        if(v instanceof AutoSearchBar) {
            return (AutoSearchBar)v;
        }
        
        throw new Exception();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        ListDownloader.getInstance(this).setOnBaiduSearchListener(this);

        if (MusicApplication.isRegulation()) {
            regulationFinish();
        }

        MusicApplication.setOnRegulationListener(new RegulationListener() {

            @Override
            public void onChangedRegulationMode(boolean isRegulation) {
                if (isRegulation) {
                    regulationFinish();
                }
            }
        });

        try {
            MusicApplication.getPlayer().requestAudioFocus();
        } catch (Exception e) {
            ObiLog.e(TAG, "onResume::getPlayer error : ", e);
        }
    }
    
    public void regulationFinish() {
        ObigoDialog.builder(this, new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button_left) {
                    finish();
                }
            }
        });
        ObigoDialog.setButton(R.string.btn_ok);
        ObigoDialog.setTitleContent(R.string.warning,
                R.string.warning_driving_search);
        ObigoDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        ListDownloader.getInstance(this).setOnBaiduSearchListener(null);
        MusicApplication.setOnRegulationListener(null);
    }

    private void loadRecentQuery() {
        mMode = MODE_RECENT;

        if (mAdapter != null) {
            mAdapter = null;
        }

        List<Music> list = RecentSearchList.getInstance(this).get();

        if (list != null) {
            mAdapter = new SearchListAdapter(this, R.layout.search_item, list);
            mListView.setAdapter(mAdapter);
            mListView.setSelection(0);
        }
    }

    @Override
    public void onClick(View view) {
        if (mArtists.contains(view)) {
            if(view instanceof TextView) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(findViewById(R.id.search_src_text).getWindowToken(), 0);
                String query = ((TextView) view).getText().toString();
                mAutoSearchBar.setData(query);
                onSearch(query);
            }
        } else {

            switch (view.getId()) {
            case R.id.all:
                if (mItems != null && mItems.size() > 0) {

                    Intent intent = new Intent(SearchActivity.this,
                            SearchListActivity.class);

                    intent.putExtra(PlayListActivity.LIST_TYPE,
                            ListDownloader.TYPE_SEARCH);
                    intent.putExtra("query", mQuery);
                    intent.putExtra("ListObject", (Serializable) mItems);
                    startActivity(intent);
                }
                break;
            case R.id.back:
                this.finish();
                break;
            default:
                break;
            }

        }
    }

    @Override
    public void onSearch(String filter) {

        if (filter == null || filter.trim().length() == 0) {
            ObigoDialog.builder(this, null);
            ObigoDialog.setButton(R.string.btn_ok);
            ObigoDialog.setTitleContent(R.string.warning,
                    R.string.input_keyword);
            ObigoDialog.show();

            mAutoSearchBar.setData("");
            loadRecentQuery();
            return;
        }

        mQuery = filter.trim();
        RecentSearchList.getInstance(this).set(mQuery);

        for (int i = 0; i < orgStr.length; i++) {
            mQuery = mQuery.replace(orgStr[i], convStr[i]);
        }

        showLoading(getResources().getString(R.string.searching));

        mBtnAll.setEnabled(false);

        ListDownloader.getInstance(this).searchMusic(filter);

        for (TextView artist : mArtists) {
            artist.setClickable(false);
        }
    }
    
    @Override
    public void onSearchMusic(List<Music> list) {
        mMode = MODE_SEARCH_RESULT;

        hideLoading();

        for (TextView artist : mArtists) {
            artist.setClickable(true);
        }

        if (list != null && list.size() > 0) {
            mItems = list;

            if (mAdapter != null) {
                mAdapter = null;
            }
            mAdapter = new SearchListAdapter(this, R.layout.search_item, mItems);
            mListView.setAdapter(mAdapter);
            mListView.setSelection(0);

            mBtnAll.setEnabled(true);

            mAutoSearchBar.setResult(mItems.size());
        } else {
            ObigoDialog.builder(this, null);
            ObigoDialog.setButton(R.string.btn_ok);
            ObigoDialog.setTitleContent(R.string.information,
                    R.string.no_result);
            ObigoDialog.show();

            mListView.setAdapter(null);
            mBtnAll.setEnabled(false);

            mAutoSearchBar.setResult(0);

            loadRecentQuery();
        }
    }

    private class SearchListAdapter extends ArrayAdapter<Music> {
        private class ViewHolder {
            private ImageView mThumb;
            private TextView mTitle;
        }

        private Context mContext;
        private List<Music> mItems;
        private int mLayoutID;
        private LayoutInflater mInflater;

        public SearchListAdapter(Context context, int textViewResourceId,
                List<Music> items) {
            super(context, textViewResourceId, items);

            mContext = context;
            mItems = items;
            mLayoutID = textViewResourceId;
            mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            View rowView = convertView;
            if (rowView == null) {
                rowView = mInflater.inflate(mLayoutID, null);
                holder = new ViewHolder();
                
                View thumb = rowView.findViewById(R.id.thumbnail);
                if(thumb instanceof ImageView) {
                    holder.mThumb = (ImageView)thumb;
                }
                
                View title = rowView.findViewById(R.id.title);
                if(title instanceof TextView) {
                    holder.mTitle = (TextView)title;
                    holder.mTitle.setSelected(true);
                }
                
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            Music item = mItems.get(position);

            String str;

            if (mMode == MODE_RECENT) {
                holder.mThumb.setVisibility(View.GONE);
                str = item.mDescription;
            } else {
                holder.mThumb.setVisibility(View.VISIBLE);
                str = item.mTitle + " - " + item.mArtist;

                // query highlighting
                if (mMode == MODE_SEARCH_RESULT && mQuery != null) {
                    str = str.replaceAll("(?i)" + mQuery, "<font color=#007eff><b>"
                            + mQuery + "</b></font>");
                }
            }

            holder.mTitle.setText(Html.fromHtml(str));

            return rowView;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parentView, View clickedView,
            int position, long id) {
        Music music = mAdapter.getItem(position);

        if (mMode == MODE_SEARCH_RESULT) {
            Intent intent = new Intent(SearchActivity.this,
                    SearchListActivity.class);
            intent.putExtra(PlayListActivity.LIST_TYPE,
                    ListDownloader.TYPE_SEARCH);
            intent.putExtra("query", mQuery);

            List<Music> items = Arrays.asList(music);
            intent.putExtra("ListObject", (Serializable) items);

            startActivity(intent);
        } else if (mMode == MODE_RECENT) {
            String q = music.mDescription;

            mAutoSearchBar.setData(q);
            onSearch(q);
        }

    }

    @Override
    public void onDownloadHotArtist(List<Artist> list) {
        for (int i = 0; i < ListDownloader.ARTIST_NUM; i++) {
            if (list.size() > i) {
                mArtists.get(i).setText(list.get(i).mName);
                mArtists.get(i).setEnabled(true);
            } else {
                mArtists.get(i).setVisibility(View.INVISIBLE);
            }
        }
        
        hideLoading();
    }

    private void showLoading(String msg) {
        View dialog = findViewById(R.id.loading);
        dialog.setVisibility(View.VISIBLE);
        
        View image = dialog.findViewById(R.id.loadingImageView);
        if (image instanceof ImageView) {
            final ImageView loadingImage = (ImageView) image;

            loadingImage.post(new Runnable() {
                public void run() {
                    AnimationDrawable animationDrawable = (AnimationDrawable)loadingImage.getBackground();
                    animationDrawable.start();
                }
            });
        }
        
        View text = dialog.findViewById(R.id.loadingStr);
        if(text instanceof TextView) {
            TextView tv = (TextView)text;
            tv.setText(msg);
        }
    }

    private void hideLoading() {
        View dialog = findViewById(R.id.loading);
        dialog.setVisibility(View.GONE);
    }
}
