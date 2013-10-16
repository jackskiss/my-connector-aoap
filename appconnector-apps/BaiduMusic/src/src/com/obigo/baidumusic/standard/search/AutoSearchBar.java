package com.obigo.baidumusic.standard.search;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.music.model.SearchSuggestion;
import com.baidu.music.onlinedata.OnlineManagerEngine;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.util.ObiLog;

//test check out
public class AutoSearchBar extends LinearLayout {// implements View.OnClickListener {
    private static final String TAG = "AutoSearchBar";

    public static final int MAX_COUNT = 5;
    private static final int KEYBOARD_DISAPPER_DELAY = 250;

    private boolean mAuto;

    private Context mContext;
    private LayoutInflater mInflater;
    private boolean mToSearch;
    private boolean mIsInSearchResult;

    public void setIsInSearchResult(boolean search) {
        this.mIsInSearchResult = search;
    }

    private AutoCompleteTextView mSearchAutoComplete;
//    private Button mSearchBtn;  //not use

    private OnSearchListener mSearchListener;

    private ArrayList<String> mHistorys = new ArrayList<String>();
    private String mFilter = "";

    private ArrayAdapter<String> mAdapter;

    public interface OnSearchListener {
        void onSearch(String filter);

    }

    public AutoSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.search_bar, this, true);

        mAuto = false;

        initViews();
    }

    private void initViews() {

        mFilter = "";
        if (mHistorys != null) {
            ObiLog.d("AutoSearchBar",
                    "+++init:" + Arrays.toString(mHistorys.toArray()));
        }
        
        View v = findViewById(R.id.search_src_text);
        if(!(v instanceof AutoCompleteTextView)) {
            return;
        }
        
      //mSearchAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_src_text);
        mSearchAutoComplete = (AutoCompleteTextView)v;
        mSearchAutoComplete.setThreshold(1);

        // mSearchAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
        // getResources().getDrawable(R.drawable.ic_search), null, null,
        // null);
        // mSearchBtn = (Button) findViewById(R.id.search_go_btn);
        // mSearchBtn.setOnClickListener(this);

        // if(mHistorys==null)
        // mAdapter= new ArrayAdapter<String>(mContext,
        // android.R.layout.simple_dropdown_item_1line, mHistorys);
        // mSearchAutoComplete.setAdapter(mAdapter);
        mSearchAutoComplete.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    // if (mSearchBtn != null)
                    // mSearchBtn.performClick();
                    hideKeyboard();
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            performSearch();
                        }
                    }, KEYBOARD_DISAPPER_DELAY);
                    return true;
                }
                return false;
            }
        });

        // add by Randy to change background of textinput
        mSearchAutoComplete
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        // TODO Auto-generated method stub
                        if (v.getId() == R.id.search_src_text) {
                            if (hasFocus) {
                                mSearchAutoComplete
                                        .setBackgroundDrawable(getResources()
                                                .getDrawable(
                                                        R.drawable.bg_search_press));
                            } else {
                                mSearchAutoComplete
                                        .setBackgroundDrawable(getResources()
                                                .getDrawable(
                                                        R.drawable.bg_search));
                            }
                        }

                    }
                });

        mSearchAutoComplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                
                if ( (s.toString()).equalsIgnoreCase("") ) {
                    View v = findViewById(R.id.search_result);
                    if(v instanceof TextView && v.isShown()) {
                        v.setVisibility(View.GONE);
                    }
                }
                if (!mAuto) {
                    return;
                }

                if (mToSearch) {
                    mToSearch = false;
                    return;
                }
                if (mIsInSearchResult) {
                    return;
                }
                String query = s.toString();
                if (TextUtils.isEmpty(s)) {
                    return;
                }

                if (mDownloadThread != null) {
                    mDownloadThread.cancel(true);
                    mDownloadThread = null;
                }

                mDownloadThread = new GetSuggestionThread();
                mDownloadThread.execute(query);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        mSearchAutoComplete.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                int pos = arg2;
                if (pos < 0) {
                    pos = 0;
                }
                try {
                    mSearchAutoComplete.dismissDropDown();
                    String item = mAdapter.getItem(pos);
                    if ((item != null) && (item.length() != 0)) {
                        mToSearch = true;
                        if (mSearchListener != null) {
                            hideKeyboard();
                            mSearchListener.onSearch(item);
                        }
                        mSearchAutoComplete.setText("");
                    }
                } catch (Exception e) {
                    ObiLog.e(TAG, "onItemClick error : ", e);
                }

            }
        });

    }

    public void cancelDownloadThread() {
        if (mDownloadThread != null) {
            mDownloadThread.cancel(true);
            mDownloadThread = null;
        }
    }

    public void clearDropDownList() {
        if (mAdapter != null) {
            mAdapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_dropdown_item_1line,
                    new String[] {});
            mSearchAutoComplete.setAdapter(mAdapter);
        }
        mSearchAutoComplete.dismissDropDown();
    }

    private GetSuggestionThread mDownloadThread;

    class GetSuggestionThread extends
            AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            SearchSuggestion array = OnlineManagerEngine.getInstance(mContext)
                    .getSearchManager(mContext)
                    .getSearchSuggestionSync(params[0]);
            if (array == null || array.getItems().size() <= 0) {
                return null;
            }
            return array.getItems();
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (!isCancelled()) {
                if (result == null || result.size() <= 0) {
                    return;
                }
                if (mSearchAutoComplete != null
                        && TextUtils.isEmpty(mSearchAutoComplete.getText())) {
                    return;
                }
                mAdapter = new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_dropdown_item_1line, result);
                if (mSearchAutoComplete != null) {
                    mSearchAutoComplete.setAdapter(mAdapter);
                    mSearchAutoComplete.showDropDown();
                }
            }
        }

    }

    public void setSearchListener(OnSearchListener searchListener) {

        mSearchListener = searchListener;

    }

    public void clearData() {
        mSearchAutoComplete.setText("");
    }

    public void setData(String data) {
        mSearchAutoComplete.setText(data);

    }

//    public void onClick(View v) {
//        if (v == mSearchBtn) {
//            cancelDownloadThread();
//            String text = mSearchAutoComplete.getText().toString();
//            if ((text == null) || (text.length() == 0)) {
//                if (mSearchListener != null) {
//                    hideKeyboard();
//                    mSearchListener.onSearch(null);
//                }
//                return;
//            }
//            ArrayList<String> newList = new ArrayList<String>();
//            if (mHistorys == null) {
//                mHistorys = new ArrayList<String>();
//                mHistorys.add(text);
//            } else if (!mHistorys.contains(text)) {
//                ObiLog.d("AutoSearchBar",
//                        "+++before:" + Arrays.toString(mHistorys.toArray()));
//                if (mHistorys.size() < MAX_COUNT) {
//                    mHistorys.add(text);
//                } else {
//                    for (int i = 0; i < mHistorys.size(); i++) {
//                        if (i == 0) {
//                            continue;
//                        }
//                        newList.add(mHistorys.get(i));
//                    }
//                    newList.add(text);
//                    mHistorys = newList;
//                }
//                ObiLog.d("AutoSearchBar",
//                        "+++after:" + Arrays.toString(mHistorys.toArray()));
//                // mAdapter= new ArrayAdapter<String>(mContext,
//                // android.R.layout.simple_dropdown_item_1line, mHistorys);
//            }
//            mFilter = text;
//            if (mSearchListener != null) {
//                hideKeyboard();
//                mSearchListener.onSearch(mFilter);
//            }
//        }
//
//    }

    private void hideKeyboard() {
        InputMethodManager mgr = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT); //
        // show
        mgr.hideSoftInputFromWindow(mSearchAutoComplete.getWindowToken(), 0); // hide
    }

    private void performSearch() {
        cancelDownloadThread();
        String text = mSearchAutoComplete.getText().toString();
        if (text.length() == 0) {
            if (mSearchListener != null) {
                hideKeyboard();
                mSearchListener.onSearch(null);
            }
            return;
        }
        ArrayList<String> newList = new ArrayList<String>();
        if (mHistorys == null) {
            mHistorys = new ArrayList<String>();
            mHistorys.add(text);
        } else if (!mHistorys.contains(text)) {
            ObiLog.d("AutoSearchBar",
                    "+++before:" + Arrays.toString(mHistorys.toArray()));
            if (mHistorys.size() < MAX_COUNT) {
                mHistorys.add(text);
            } else {
                for (int i = 0; i < mHistorys.size(); i++) {
                    if (i == 0) {
                        continue;
                    }
                    newList.add(mHistorys.get(i));
                }
                newList.add(text);
                mHistorys = newList;
            }
            ObiLog.d("AutoSearchBar",
                    "+++after:" + Arrays.toString(mHistorys.toArray()));
            // mAdapter= new ArrayAdapter<String>(mContext,
            // android.R.layout.simple_dropdown_item_1line, mHistorys);
        }
        mFilter = text;
        if (mSearchListener != null) {
            hideKeyboard();
            mSearchListener.onSearch(mFilter);
        }
    }

    public void setResult(int size) {
        //TextView view = (TextView) findViewById(R.id.search_result);
        View v = findViewById(R.id.search_result);
        if (!v.isShown())
            v.setVisibility(View.VISIBLE);

        if(v instanceof TextView) {
            TextView view = (TextView)v;
            
            if (size > 0) {
                view.setText("(" + size + ")");
            } else {
                view.setText("");
            }
        }
    }
}
