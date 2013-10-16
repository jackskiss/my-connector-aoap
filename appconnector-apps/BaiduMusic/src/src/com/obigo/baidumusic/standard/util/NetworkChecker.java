package com.obigo.baidumusic.standard.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;

import com.hkmc.api.Const;
import com.hkmc.api.Info;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.view.ObigoDialog;

public final class NetworkChecker {
    private static String TAG = "NetworkChecker";
    public static final int NOT_CHOOSE = 0;
    public static final int ACCEPT = 1;
    public static final int DENY = 2;
    
    private static int m3gChoice = NOT_CHOOSE;
    private NetworkChecker() {
        // not called
    }

    public static boolean isConnected(final Context context) {
        ConnectivityManager cManager = (ConnectivityManager) (context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo ni = cManager.getActiveNetworkInfo();

        try {
            Info info = new Info(context);
            if (info != null) {
                Bundle bundle = info.getProvisioningInfo();
                if (bundle != null) {
                    int data_network = bundle.getInt(Const.MOF_GEN2_DOWNLOAD_TMS_DTP);
        
                    if (data_network > 0) { // in case of 3G network was enabled
                        switch(m3gChoice) {
                        case NOT_CHOOSE:
                            showChoosePopup(context);
                            break;
                        case ACCEPT:
                            break;
                        case DENY:
                            break;
                        }
                    }
                } else {
                    ObiLog.e(TAG, "bundle instance was not generated.");
                }
            }

        } catch (RemoteException e) {
          ObiLog.e(TAG, "isConnected() - error:" + e);
        }
        if (ni != null && ni.isConnected()) {
            return true;
        }

        return false;
    }

    public static void showError(final Context context) {
        ObigoDialog.builder(context, null);
        ObigoDialog.setButton(R.string.btn_ok);
        ObigoDialog.setTitleContent(R.string.warning,
                R.string.warning_network_connection1);
        ObigoDialog.show();
    }
    
    public static void showChoosePopup(final Context context) {
        View.OnClickListener listener = new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                case R.id.button_left:
                    m3gChoice = ACCEPT;
                    break;
                case R.id.button_right:
                    m3gChoice = DENY;
//                    ((Activity)context).finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
                }
            }
        };
        ObigoDialog.builder(context, listener);
        ObigoDialog.setButtons(R.string.btn_yes, R.string.btn_no);
        ObigoDialog.setTitleContent(R.string.warning,
                R.string.warning_3gnetwork_connection);
        ObigoDialog.show();
    }
}
