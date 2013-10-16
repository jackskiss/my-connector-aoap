/*
 * Added ObigoDialog.java
 * Description : Obigo Style Dialog
 * Modify date : 2013-05-22
 * Author : Roger Wang / Randy Bu 
 */
package com.obigo.baidumusic.standard.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.obigo.baidumusic.standard.R;

public final class ObigoDialog {
    private static AlertDialog mObigoDialog = null;
    private static ObigoDialog instance = null;
    
    static {
        instance = new ObigoDialog();
    }
    
    private static View.OnClickListener mListener = null;

    private static Context mContext;

    private static String mTitleString;
    private static String mContentString;
    private static int mLeftBtnId;
    private static int mRightBtnId;

    private ObigoDialog() {
        // not called
    }

    public static synchronized ObigoDialog builder(Context context,
            final View.OnClickListener listener) {

        mContext = context;

//        if (instance == null) {
//            instance = new ObigoDialog();
//        }

        if (mObigoDialog == null) {
            mObigoDialog = new AlertDialog.Builder(context).create();
            mObigoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mObigoDialog = null;
                }
            });
            
            mObigoDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    mObigoDialog = null;
                }

            });
        }

        mListener = listener;
        return instance;
    }

    public static ObigoDialog setButton(int leftBtnId) {
        mLeftBtnId = leftBtnId;
        mRightBtnId = -1;
        return instance;
    }

    public static ObigoDialog setButtons(int leftBtnId, int rightBtnId) {
        mLeftBtnId = leftBtnId;
        mRightBtnId = rightBtnId;
        return instance;
    }

    public static ObigoDialog setTitleContent(int titleId, int contentId) {
        mTitleString = mContext.getResources().getString(titleId);
        mContentString = mContext.getResources().getString(contentId);
        return instance;
    }

    public static ObigoDialog setTitleContent(String title, String content) {
        mTitleString = title;
        mContentString = content;
        return instance;
    }

    public static void show() {
        if (mObigoDialog != null && mObigoDialog.isShowing()) {
            return;
        }

        mObigoDialog.show();

        Window window = mObigoDialog.getWindow();
        window.setContentView(R.layout.dialog);

//        View v1 = window.findViewById(R.id.textView1);
//        if(v1 instanceof TextView) {
//            TextView title = (TextView)v1;
//            title.setText(mTitleString);
//        }
        
        View v2 = window.findViewById(R.id.textView2);
        if(v2 instanceof TextView) {
            TextView content = (TextView)v2;
            content.setText(mContentString);
        }
        
        Button btnLeft = null;
        Button btnRight = null;
        View b1 = window.findViewById(R.id.button_left);
        
        if(b1 instanceof Button) {
        
            btnLeft = (Button)b1;
            btnLeft.setText(mContext.getResources().getString(mLeftBtnId));
    
            btnLeft.setOnClickListener(new View.OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(v);
                    }
                    mObigoDialog.cancel();
                }
            });
        }
        
        View b2 = window.findViewById(R.id.button_right);
        if(b2 instanceof Button) {
            btnRight = (Button)b2;
            
            if (mRightBtnId == -1) {
                btnRight.setVisibility(View.GONE);
                
                if(btnLeft != null) {
                    btnLeft.setBackgroundResource(R.drawable.selector_btn_pop_long);
                }
            } else {
                btnRight.setText(mContext.getResources().getString(mRightBtnId));

                btnRight.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onClick(v);
                        }
                        mObigoDialog.cancel();
                    }
                });
            }
        }
    }
}