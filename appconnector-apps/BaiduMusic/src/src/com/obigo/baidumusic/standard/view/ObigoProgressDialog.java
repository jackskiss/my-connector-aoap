/*
 * Added ObigoProgressDialog.java
 * Description : Obigo Style ProgressDialog
 * Modify date : 2013-05-27
 * Author : Mcdon Tang 
 */
package com.obigo.baidumusic.standard.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.obigo.baidumusic.standard.R;

public class ObigoProgressDialog extends Dialog {

    private static ObigoProgressDialog obigoProgressDialog = null;

    public ObigoProgressDialog(Context context) {
        super(context);
    }

    public ObigoProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static ObigoProgressDialog createDialog(Context context) {
        obigoProgressDialog = new ObigoProgressDialog(context,
                R.style.ObigoProgressDialog);
        obigoProgressDialog.setContentView(R.layout.obigoprogressdialog);
        obigoProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        return obigoProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        if (obigoProgressDialog == null) {
            return;
        }

        ImageView imageView = null;

        View v = obigoProgressDialog.findViewById(R.id.loadingImageView);
        if (v instanceof ImageView) {
            imageView = (ImageView) v;

            AnimationDrawable animationDrawable = (AnimationDrawable) imageView
                    .getBackground();
            animationDrawable.start();
        }
    }
    
    public Button getButton() {
        View v = obigoProgressDialog.findViewById(R.id.back);
        if (v instanceof Button) {
            return (Button)v;
        }
        
        return null;
    }

    public ObigoProgressDialog setMessage(String string) {
        TextView loadingStr = null;

        View v = obigoProgressDialog.findViewById(R.id.loadingStr);
        if (v instanceof TextView) {
            loadingStr = (TextView) v;
        }

        if (loadingStr != null) {
            loadingStr.setText(string);
        }

        return obigoProgressDialog;
    }
}
