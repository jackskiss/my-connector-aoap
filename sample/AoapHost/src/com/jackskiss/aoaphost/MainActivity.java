package com.jackskiss.aoaphost;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable {
	
	private static final String TAG = "AoapHost";

	private PendingIntent permissionIntent;
	private static final String strPermission = "com.jackskiss.aoaphost.USB_PERMISSION";
	private UsbDevice usbDevice;
	private UsbManager usbManager;
	private UsbInterface usbInterface;
	private UsbDeviceConnection usbDeviceConnection;
	private UsbEndpoint epRx;
	private UsbEndpoint epTx;
	private boolean isConnected = false;
	private boolean permissionRequestPending = false;
	
	private static final int AOAP_GET_PROTOCOL = 		51;
	private static final int AOAP_SEND_STRING = 		52;
	private static final int AOAP_START_ACCESSORY = 	53;
	private static final int AOAP_SUPPORT_AUDIO = 	58;
	
	private static final int AOAP_STRING_MANUFACTURER = 0;
	private static final int AOAP_STRING_MODEL = 1;
	
	/* AOA 1.0 */
	private static final int USB_PRODUCTID_ACCESSORY = 			0x2D00;
	private static final int USB_PRODUCTID_ACCESSORY_ADB = 		0x2D01;
	/* AOA 2.0 */
	private static final int USB_PRODUCTID_AUDIO = 				0x2D02;
	private static final int USB_PRODUCTID_AUDIO_ADB = 			0x2D03;
	private static final int USB_PRODUCTID_ACCESSORY_AUDIO = 		0x2D04;
	private static final int USB_PRODUCTID_ACCESSORY_AUDIO_ADB = 	0x2D05;
	
	private static final String manufacturer = "HKMC";
	private static final String model = "VIT";

	private static final int MESSAGE_LOG = 1;
	private static final int MESSAGE_USB_CONNECT_HANDLER = 2;
	
	private EditText txtSendBox;
	private EditText txtReceiveBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtSendBox = (EditText)findViewById(R.id.txtSend);
		txtReceiveBox = (EditText)findViewById(R.id.txtReceive);		

		usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
		
		usbDevice = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
		
		checkDeviceConnection checkDevice = new checkDeviceConnection();
		checkDevice.start();
		permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(strPermission), 0);
		
		IntentFilter filter = new IntentFilter(strPermission);
		
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		
		registerReceiver(usbReceiver, filter);			

		Button buttonSend = (Button)findViewById(R.id.btnSend);
		buttonSend.setOnClickListener(new View.OnClickListener() {
			
			int ret = 0;
			
			@Override
			public void onClick(View arg0) {
				if(epTx != null)
				{
					ret = usbDeviceConnection.bulkTransfer(epTx, txtSendBox.getText().toString().getBytes(), txtSendBox.getText().length(), 500);
					txtSendBox.setText("");
					Log.d(TAG, "Sent " + ret + "bytes");
				}
				
			}
		});
		
	}
	
	public static class UsbIds {
		final static int SAMSUNG_VID = 1256;
		final static int LG_VID = 4100;
		final static int[] SAMSUNG_PID = { 26715, 26716, 26717, 26718, 26720, 26725, 26726 };
		final static int[] LG_PID = {25036, 25073, 25084, 25086, 25344, 25372, 25374, 25430};
	}
	
	private class checkDeviceConnection extends Thread {
		private boolean isChecking  = true;
		
		public void run() {
			
			while(isChecking)
			{
				if(!permissionRequestPending)
				{
					for(UsbDevice dev : usbManager.getDeviceList().values())
					{
						if((dev.getVendorId() == UsbIds.LG_VID) || (dev.getVendorId() == UsbIds.SAMSUNG_VID)) {
							usbManager.requestPermission(dev, permissionIntent);
							usbDevice = dev;
							permissionRequestPending = true;
							break;
						}
					}						
				}
				
				try {
					sleep(1000);
				} catch (Exception e) {
					// Error
				}
			}
		}
	}
	
	BroadcastReceiver usbReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Received intent : " + intent.getAction());
			String action = intent.getAction();
			if (strPermission.equals(action)) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                synchronized (this) {	                    
	                	if (usbDevice != null)
	                    {
	                		if(usbVidPidChecker(usbDevice.getVendorId(), usbDevice.getProductId()))
	                		{
	                			// usbInterface = usbDevice.getInterface(0); // Should be removed
	                			
	                			usbDeviceConnection = usbManager.openDevice(usbDevice);		
	                			
	                			usbDeviceConnection.claimInterface(usbInterface, true); /* Use interface exclusive */
	                			
	                			isConnected = setEndpoint()?true:false;
	                		}
	                		else
	                		{
	                			openUsbAsAccessory();
	                			
	                		}
	                    }
	                	permissionRequestPending = false;
	                }        
	             }
				 else 
                     Log.d(TAG, "permission denied for Hostmode ");				
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            	usbAccessoryAttach();
            	
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            	 // Fix: Close USB connection 
				usbDeviceConnection.releaseInterface(usbInterface);
				usbDeviceConnection.close();
				isConnected = false;
            }
			
		}
		
	};

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(usbReceiver);
	}

	@Override
	protected void onResume() {		
		
//		Log.d(TAG, "onResume");
		
//		usbAccessoryAttach();
		
		super.onResume();
	}
	
	private boolean usbAccessoryAttach()
	{
		usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		getUsbDevice();
			
		if(usbDevice != null)
		{
			if(usbManager.hasPermission(usbDevice))
			{
				openUsbAsAccessory();
			}
			else
			{
				synchronized (usbReceiver) {
					if(!permissionRequestPending) {
						Log.d(TAG, "usbAccessoryAttach: Request Permission");
						usbManager.requestPermission(usbDevice, permissionIntent);
						permissionRequestPending = true;
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean getUsbDevice()
	{
		if(usbManager != null) {
			for(UsbDevice dev : usbManager.getDeviceList().values())
			{
				if(usbVidPidChecker(dev.getVendorId(), dev.getProductId())) {
					usbDevice = dev;
					return true;
				}
			}					
		}
		
		return false;
	}
	
	private boolean setEndpoint()
	{
		UsbEndpoint tempEndpoint;
		epTx = null;
		epRx = null;
		/* Assume that 1st Interface has two Bulk endpoints */
		for(int i = 0; i < usbInterface.getEndpointCount(); i++)
		{	
			if(epTx != null && epRx != null)
			{
				Log.d(TAG,"Device connected");
				Toast.makeText(getApplicationContext(), "Device Connected", Toast.LENGTH_SHORT).show();
	            Thread thread = new Thread(null, this, "AccessoryHost");
	            thread.start();
				return true;
			}
			
			tempEndpoint = usbInterface.getEndpoint(i);
			if(tempEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
				if(tempEndpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
					epTx = tempEndpoint;		
				} else {
					epRx = tempEndpoint;
				}
			}			
		}
		
		return false;
	}
	
	private boolean usbVidPidChecker(int vid, int pid)
	{
		if( pid == USB_PRODUCTID_ACCESSORY 
		 || pid == USB_PRODUCTID_ACCESSORY_ADB
		 || pid == USB_PRODUCTID_AUDIO
		 || pid == USB_PRODUCTID_AUDIO_ADB
		 || pid == USB_PRODUCTID_ACCESSORY_AUDIO
		 || pid == USB_PRODUCTID_ACCESSORY_AUDIO_ADB ) {			
			return true;
		}
			
		Log.d(TAG, "Not support Accessory mode yet " + vid + "/" + pid );
		return false;	
	}
	
	private boolean openUsbAsAccessory()
	{
		Log.d(TAG, "openUsbAsAccessory");
		byte[] protocol = new byte[2];
		
		usbInterface = usbDevice.getInterface(0); // Default to 0
		
		usbDeviceConnection = usbManager.openDevice(usbDevice);		
		
		usbDeviceConnection.claimInterface(usbInterface, true); /* Use interface exclusive */
		
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_IN|UsbConstants.USB_TYPE_VENDOR,
											AOAP_GET_PROTOCOL, 0, 0, protocol, 2, 0);
		
		int getProto = ((protocol[1]<<8) | protocol[0]);
		
		Log.d(TAG, "USB Device protocol is " + getProto);
		
		if (getProto < 1) {
			Log.d(TAG, "AOAP should supports protocol 1 or 2");
			return false;
		}
		
		/* Send information of USB Accessory */											
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT|UsbConstants.USB_TYPE_VENDOR, 
											AOAP_SEND_STRING, 0, AOAP_STRING_MANUFACTURER, manufacturer.getBytes(), manufacturer.length(), 0);
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT|UsbConstants.USB_TYPE_VENDOR, 
											AOAP_SEND_STRING, 0, AOAP_STRING_MODEL,model.getBytes(), model.length(), 0);
		/* Start Accessory mode */
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT|UsbConstants.USB_TYPE_VENDOR, 
											AOAP_START_ACCESSORY, 0, 0, null, 0, 0);
		
		Log.d(TAG,"startService: Connnect Accessory Protocol");		
	
		hostUSBDemonThread usbDemon  = new hostUSBDemonThread();
		usbDemon.start();
		
		return true;
	}

    public void run() {
        int ret = 0;
        boolean isRunning = true;
        byte[] buffer = new byte[16384];
        Log.d(TAG, "thread start");
        while (isRunning) {
            ret = usbDeviceConnection.bulkTransfer(epRx, buffer, buffer.length, 500);
 
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
                case MESSAGE_USB_CONNECT_HANDLER:
                {
    				synchronized (usbReceiver) {
    					if(!permissionRequestPending) {
    						Log.d(TAG, "handleMessage : Request Permission");
    						usbManager.requestPermission(usbDevice, permissionIntent);
    						permissionRequestPending = true;
    					}
    				}
                }
            }
        }
    }; 
    
	private class hostUSBDemonThread extends Thread {
		
		public boolean isRunning = true;
		private static final int LOOP_SLEEP_TIME = 200; // ms
		private static final int MAX_LOOP_COUNT = 25; // 5 second
		
		public void run() {
			while(isRunning) {
				usbDevice = null;
				
				for (int count =0; count < MAX_LOOP_COUNT ; count++)
				{
					if(usbManager != null) {
						for(UsbDevice dev : usbManager.getDeviceList().values())
						{
							if(usbVidPidChecker(dev.getVendorId(), dev.getProductId())) {
								usbDevice = dev;
								Log.d(TAG, "Found out Google Device : " +  dev.getVendorId() + "/" + dev.getProductId() );
								mHandler.sendEmptyMessage(MESSAGE_USB_CONNECT_HANDLER);
								return;
							}
						}					
					}
					
					try {
						sleep(LOOP_SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				Log.d(TAG, "Not found Google Device");
				return;
			}
		}
	}
}
