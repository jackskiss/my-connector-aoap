package com.obigo.baidumusic.standard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.obigo.baidumusic.standard.view.CategoryView;
import com.obigo.baidumusic.standard.view.MenuButton;

public class BaseActivity extends Activity {
    
    private MusicApplication mApplication;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (MusicApplication)this.getApplicationContext();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        ConnReceiver.setActivity(this);
        
        if(mApplication != null) {
            mApplication.setCurrentActivity(this);
        }
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
        ConnReceiver.setActivity(null);
    }
    
    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }
    
    private void clearReferences(){
        Activity currActivity = null;
        
        if(mApplication != null) {
            currActivity = mApplication.getCurrentActivity();
        }
        
        if (currActivity != null && currActivity.equals(this)) {
            if(mApplication != null) {
                mApplication.setCurrentActivity(null);
            }
        }
    }
    
    protected Button findButton(int id) throws Exception {
        View v = findViewById(id);
        
        if(v instanceof Button) {
            return (Button)v;
        }
        
        throw new Exception();
    }
    
    protected MenuButton findMenuButton(int id) throws Exception {
        View v = findViewById(id);
        
        if(v instanceof MenuButton) {
            return (MenuButton)v;
        }
        
        throw new Exception();
    }
    
    protected TextView findTextView(int id) throws Exception {
        View v = findViewById(id);
        
        if(v instanceof TextView) {
            return (TextView)v;
        }
        
        throw new Exception();
    }
    
    protected CategoryView findCategoryView(int id) throws Exception {
        View v = findViewById(id);
        
        if(v instanceof CategoryView) {
            return (CategoryView)v;
        }
        
        throw new Exception();
    }
    
    protected ListView findListView(int id) throws Exception {
        View v = findViewById(id);
        
        if(v instanceof ListView) {
            return (ListView)v;
        }
        
        throw new Exception();
    }
    
    protected ImageView findImageView(int id) throws Exception {
        View v = findViewById(id);
        
        if(v instanceof ImageView) {
            return (ImageView)v;
        }
        
        throw new Exception();
    }
    
    protected ProgressBar findProgressBar(int id) throws Exception {
        View v = findViewById(id);
        
        if(v instanceof ProgressBar) {
            return (ProgressBar)v;
        }
        
        throw new Exception();
    }
}
