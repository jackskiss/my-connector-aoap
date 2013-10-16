package com.obigo.appconnector.ipodtest;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hkmc.ipod.IPodListener;
import com.hkmc.ipod.IPodManager;
import com.hkmc.ipod.IPodObserver;
import com.hkmc.ipod.IPodObserverListener;

public class MainActivity extends Activity implements IPodObserverListener {
	
	ArrayList<String> mReceivedList;
	ArrayAdapter<String> mReceivedListAdapter;
	
	IPodObserver mIPodObserver;
	IPodListener mIPodListener;
	IPodManager mIPodManager;
	
	int mAppId;
	int mSessionId;
	
	Button mObserverAddListenerButton;
	Button mObserverRemoveListenerButton;
	Button mObserverDevicesButton;
	Button mMgrAddListenerButton;
	Button mMgrRemoveListenerButton;
	Button mClearButton;
	ListView mReceivedListView;
	Button mSendButton;
	TextView mToSendText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppId = -1;
        mSessionId = -1;
        
        mIPodObserver = new IPodObserver(this);
        
        
        
        mIPodListener = new IPodListener() {
        	
        	@Override
            public void onAppCloseSession(int sessionId) {
        		
				mReceivedList.add(String.format("IPodListener.onAppCloseSession"));
				mReceivedList.add(String.format("	sessionId=%d", sessionId));
 				
 				mReceivedListAdapter.notifyDataSetChanged();
 				
        		if (mSessionId == sessionId) {
        			mSessionId = -1;
        		}
            }
/*
        	@Override
            public void onAddDataReceived(int sessionId, byte[] buffer) {
				mReceivedList.add(String.format("IPodListener.onAddDataReceived"));
				mReceivedList.add(String.format("	sessionId=%d, buffer=%s", sessionId, buffer.toString()));
 				
 				mReceivedListAdapter.notifyDataSetChanged();
            }
*/            
        	@Override
            public void onAppOpenSession(int appId, int sessionId) {
        		mAppId = appId;
        		mSessionId = sessionId;

				mReceivedList.add(String.format("IPodListener.onAppOpenSession"));
				mReceivedList.add(String.format("	appId=%d, sessionId=%d", appId, sessionId));
 				
 				mReceivedListAdapter.notifyDataSetChanged();
        	
        	}
            
        	@Override
            public void onConnectionChanged(int state, String iPodName, String iPodModel, 
            		String iPodSerial) {
				mReceivedList.add(String.format("IPodListener.onConnectionChanged"));
				mReceivedList.add(String.format("	state=%d", state));
				mReceivedList.add(String.format("	iPodName=%s", iPodName));
				mReceivedList.add(String.format("	iPodModel=%s", iPodModel));
				mReceivedList.add(String.format("	iPodSerial=%s", iPodSerial));
 				
 				mReceivedListAdapter.notifyDataSetChanged();
            	
            }
/*            
        	@Override
            public void onConnectionInfoReceived(int lingoMajor, int lingoMinor, 
            		int iPodMinor, int iPodRevision, String iPodName, String iPodModel, 
            		String iPodSerial) {
        		
				mReceivedList.add(String.format("IPodListener.onConnectionInfoReceived"));
				mReceivedList.add(String.format("	iPodRevision=%d", iPodRevision));
				mReceivedList.add(String.format("	iPodName=%s", iPodName));
				mReceivedList.add(String.format("	iPodModel=%s", iPodModel));
				mReceivedList.add(String.format("	iPodSerial=%s", iPodSerial));
 				
 				mReceivedListAdapter.notifyDataSetChanged();
            	
            }
*/            
        	@Override
            public void onPlayStatusChanged(int type, int value) {
            	
				mReceivedList.add(String.format("IPodListener.onPlayStatusChanged"));
				mReceivedList.add(String.format("	type=%d, value=%d", type, value));
 				
 				mReceivedListAdapter.notifyDataSetChanged();
            }        	
        };
        
        mIPodManager = new IPodManager(this);
       
        
        mObserverAddListenerButton = (Button)findViewById(R.id.ipodobserver_add_listener_button);
        mObserverAddListenerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIPodObserver.addListener(MainActivity.this);		
			}
		});
        
        mObserverRemoveListenerButton = (Button)findViewById(R.id.ipodobserver_remove_listener_button);
        mObserverRemoveListenerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIPodObserver.removeListener(MainActivity.this);			
			}
		});
        
        mObserverDevicesButton = (Button)findViewById(R.id.ipodobserver_devices_button);
        mObserverDevicesButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String devices[] = mIPodObserver.getConnectedDevices();
				
				mReceivedList.add(String.format("IPodObserverListener.getConnectedDevices"));
				for (String device : devices)
					mReceivedList.add(String.format("	%s", device));
 				
 				mReceivedListAdapter.notifyDataSetChanged();				
			}
		});
        
        mMgrAddListenerButton = (Button)findViewById(R.id.ipodmgr_add_listener_button);
        mMgrAddListenerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIPodManager.addListener(mIPodListener);	
			}
		});
        
        mMgrRemoveListenerButton = (Button)findViewById(R.id.ipodmgr_remove_listener_button);
        mMgrRemoveListenerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIPodManager.removeListener(mIPodListener);				
			}
		});
        
        mClearButton = (Button)findViewById(R.id.clear_button);
        mClearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mReceivedList.clear();
				mReceivedListAdapter.notifyDataSetChanged();
				
			}
		});

        mReceivedListView = (ListView)findViewById(R.id.received_list_view);
        mReceivedList = new ArrayList<String>();
        mReceivedListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mReceivedList);
        mReceivedListView.setAdapter(mReceivedListAdapter);
             
        mSendButton = (Button)findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String textToSend = mToSendText.getText().toString();
				
				mIPodManager.appSendData(mSessionId, textToSend.getBytes());
			}
		});
                
        mToSendText = (TextView)findViewById(R.id.to_send_text);           
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // IPodObserverListener
	@Override
	public void onConnectionChanged(int state, String iPodName,
			String iPodModel, String iPodSerial) {			
		mReceivedList.add(String.format("IPodObserverListener.onConnectionChanged"));
		mReceivedList.add(String.format("	state=%d", state));
		mReceivedList.add(String.format("	iPodName=%s", iPodName));
		mReceivedList.add(String.format("	iPodModel=%s", iPodModel));
		mReceivedList.add(String.format("	iPodSerial=%s", iPodSerial));				
		
		mReceivedListAdapter.notifyDataSetChanged();
	}        	
}
