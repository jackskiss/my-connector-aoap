package com.jackskiss.aoapdevice;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable {
	private static final String TAG = "AoapDevice";
	
	private PendingIntent permissionIntent;
	private static final String strPermission = "com.jackskiss.aoapdevice.USB_PERMISSION";
	private UsbDevice usbDevice;
	private UsbManager usbManager;
	private UsbInterface usbInterface;
	private UsbDeviceConnection usbDeviceConnection;
	private boolean permissionRequestPending = false;
	
    private ParcelFileDescriptor fileDescriptor;
    private FileInputStream inputStream = null;
    private FileOutputStream outputStream = null;	
    
    private static final int MESSAGE_LOG = 1;
    private EditText txtSendBox;
    private EditText txtReceiveBox;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		txtSendBox = (EditText)findViewById(R.id.txtSend);
		txtReceiveBox = (EditText)findViewById(R.id.txtReceive);
		
		usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);

		permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(strPermission), 0);
		IntentFilter filter = new IntentFilter(strPermission);
		
		Button buttonSend = (Button)findViewById(R.id.btnSend);		
		buttonSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(outputStream != null) {
					try {
						outputStream.write(txtSendBox.getText().toString().getBytes(), 0, txtSendBox.getText().length());
						txtSendBox.setText("");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
	        if (strPermission.equals(intent.getAction())) {
	            synchronized (this) {
	            	UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
		            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
			              if (accessory != null) {
			                   openAccessory(accessory);
			              }
	                } else {
			              Log.d(TAG, "permission denied for accessory " + accessory);
			        }
		            permissionRequestPending = false;
	            }
	        }
        }
	};	

    private void openAccessory(UsbAccessory accessory) {
        Log.d(TAG, "openAccessory: " + accessory);
        fileDescriptor = usbManager.openAccessory(accessory);
        if (fileDescriptor != null) {
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
            Thread thread = new Thread(null, this, "AccessoryChat");
            thread.start();
            Log.d(TAG, "openAccessory succeeded");
            Toast.makeText(getApplicationContext(), "Accessory Connected", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "openAccessory fail");
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean isDestroyed() {
		// TODO Auto-generated method stub
		unregisterReceiver(usbReceiver);
		return super.isDestroyed();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Intent intent = getIntent();
	    Log.d(TAG, "intent: " + intent);
	    UsbAccessory[] accessories = usbManager.getAccessoryList();
	    UsbAccessory accessory = (accessories == null ? null : accessories[0]);
	    if (accessory != null) {
	        if (usbManager.hasPermission(accessory)) {
	            openAccessory(accessory);
	        } else {
	            synchronized (usbReceiver) {
	                if (!permissionRequestPending) {
	                    usbManager.requestPermission(accessory, permissionIntent);
	                    permissionRequestPending = true;
	                }
	            }
	        }
	    } else {
	        Log.d(TAG, "mAccessory is null");
	    }
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

    public void run() {
        int ret = 0;
        byte[] buffer = new byte[16384];
        
        while (ret >= 0) {
 
        	try {
                ret = inputStream.read(buffer);
            } catch (IOException e) {
                break;
            }

            if (ret > 0) {
                Message m = Message.obtain(mHandler, MESSAGE_LOG);
                String text = new String(buffer, 0, ret);
                Log.d(TAG, "chat: " + text);
                m.obj = text;
                mHandler.sendMessage(m);
            }
        }
        Log.d(TAG, "thread out");
    }
    
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOG:
                	txtReceiveBox.setText((String)msg.obj);
                    break;
             }
        }
    };    
}
