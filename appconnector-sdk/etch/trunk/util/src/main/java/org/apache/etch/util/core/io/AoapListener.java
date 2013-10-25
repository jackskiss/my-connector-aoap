package org.apache.etch.util.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.etch.util.FlexBuffer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class AoapListener extends Connection<SessionListener<UsbManager>>
		implements Transport<SessionListener<UsbManager>> {

	private static final String TAG = "AoapListener";
	
	private Activity appActivity = null;
	
	private hostUSBDemonThread usbHostDemon;
	private UsbManager usbManager = null;
	private UsbDevice usbDevice = null;
	private UsbInterface usbInterface = null;
	private UsbDeviceConnection usbDeviceConnection = null;
	private UsbEndpoint usbEndpointRx0 = null;
	private UsbEndpoint usbEndpointTx0 = null;
	private UsbEndpoint usbEndpointRx1 = null;
	private UsbEndpoint usbEndpointTx1 = null;
	private UsbEndpoint usbEndpointControlRx = null;
	private UsbEndpoint usbEndpointControlTx = null;
	
	private PendingIntent permissionIntent;
	private String actionUsbPermission;
	private boolean isConnected = false;
	private boolean hasPermission = false;
	private int vendorID;
	private int productID;
	
//	private static final int USB_VENDORID_GOOGLE =		0x18D1;
//	private static final int USB_VENDORID_MOTOROLA =	0x22B8;
//	private static final int USB_VENDORID_SAMSUNG =		0x04E8;
//	private static final int USB_VENDORID_LG = 			0x1004;
//	private static final int USB_VENDORID_SHARP = 		0x04DD;
//	private static final int USB_VENDORID_LENOVO = 		0x17EF;

	/* AOA 1.0 */
	private static final int USB_PRODUCTID_ACCESSORY = 				0x2D00;
	private static final int USB_PRODUCTID_ACCESSORY_ADB = 			0x2D01;
	/* AOA 2.0 */
	private static final int USB_PRODUCTID_AUDIO = 					0x2D02;
	private static final int USB_PRODUCTID_AUDIO_ADB = 				0x2D03;
	private static final int USB_PRODUCTID_ACCESSORY_AUDIO = 		0x2D04;
	private static final int USB_PRODUCTID_ACCESSORY_AUDIO_ADB = 	0x2D05;

	private static final int AOAP_GET_PROTOCOL = 51;
	private static final int AOAP_SEND_STRING = 52;
	private static final int AOAP_START_ACCESSORY = 53;
	private static final int AOAP_SUPPORT_AUDIO = 58;
	
	private static final int AOAP_STRING_MANUFACTURER = 0;
	private static final int AOAP_STRING_MODEL = 1;

	private static final String manufacturer = "HKMC";
	private static final String model = "VIT";
	
	private static final int USB_DEFAULT_CONTROL_INTERFACE	= 0x00;
	private static final int USB_XBULK_ENDPOINT_NEEDED_NUM	= 0x02;
	
	private static final int READ_QUEUE_SIZE = 100; // Fix: Need to adjust particular this
	private static final int READ_BYTE_BUFFER_SIZE = 16384; // Fix: Need to adjust particular this
	
	private BlockingQueue<FlexBuffer> readQueues = null;
	
		
	public AoapListener (Activity app, UsbManager um)
	{
		
		if(app != null && um != null) {
			appActivity = app;
			usbManager = um;

			/* Check access permission by application */
			actionUsbPermission = appActivity.getPackageName() + ".USB_PERMISSION";
			
			Log.d(TAG, "AoapListener Creation:" + actionUsbPermission);
			
			Toast.makeText(appActivity.getApplicationContext(),  "AoapListener Creation:" + actionUsbPermission, Toast.LENGTH_SHORT).show();
			
			permissionIntent = PendingIntent.getBroadcast(appActivity.getApplicationContext(), 0, new Intent(actionUsbPermission), 0);
			
			IntentFilter filter = new IntentFilter(actionUsbPermission);
			
			filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
			filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
			appActivity.registerReceiver(usbReceiver, filter);			
			
			usbHostDemon = new hostUSBDemonThread();
			usbHostDemon.start();

			Intent startIntent = appActivity.getIntent();
			
			if(connectUsbDevice(usbManager, startIntent))
				startService();;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void finalize()
	{
		usbHostDemon.stop();
	}

	public BlockingQueue<FlexBuffer> allocReadQueue()
	{				
       Log.d(TAG, "allocReadQueue");
		
		readQueues = new ArrayBlockingQueue<FlexBuffer>(READ_QUEUE_SIZE);
		return readQueues;
	}

	private FlexBuffer getReadQueue()
	{
		try {
	       Log.d(TAG, "getReadQueue");
			return readQueues.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void releaseReadQueue()
	{
		// Free Queue
	}
	
	private boolean usbVidPidChecker(int vid, int pid)
	{
		// Fix: if(Vendor Checker ????? )
       Log.d(TAG, "VID PID Check Vid: " + vid + " Pid: " + pid);
		Toast.makeText(appActivity,  "VID PID Check Vid: " + vid + " Pid: " + pid , Toast.LENGTH_SHORT).show();

		if( pid != USB_PRODUCTID_ACCESSORY 
		 || pid != USB_PRODUCTID_ACCESSORY_ADB
		 || pid != USB_PRODUCTID_AUDIO
		 || pid != USB_PRODUCTID_AUDIO_ADB
		 || pid != USB_PRODUCTID_ACCESSORY_AUDIO
		 || pid != USB_PRODUCTID_ACCESSORY_AUDIO_ADB ) {
			
			Log.d(TAG, "Device does not support Accessory mode");
			return false;
		}
			
		return true;	
	}

	/**
	 * @param dev USB device
	 * @return result to find out
	 */
	private boolean scanEndpoint(UsbDevice dev)
	{
		int infCount = dev.getInterfaceCount();
		int endCount;
		
		UsbInterface tempInterface;
		UsbEndpoint tempEndpoint;

		Log.d(TAG, "scanEndpoint");
		Toast.makeText(appActivity,  "scanEndpoint" , Toast.LENGTH_SHORT).show();
		
		for (int idx=0; idx<infCount; idx++)
		{
			tempInterface = usbDevice.getInterface(idx);
			
			/* Assign endpoints for bulk transfer */
			if(tempInterface.getInterfaceClass() == UsbConstants.USB_CLASS_VENDOR_SPEC ) {
				endCount = tempInterface.getEndpointCount();
				if(endCount >= USB_XBULK_ENDPOINT_NEEDED_NUM) { // Should be #2 more.
					for(int jdx=0; jdx<endCount; jdx++) {
						tempEndpoint = tempInterface.getEndpoint(jdx);
						if(tempEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK ) {
							if(tempEndpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
								if(usbEndpointTx0 == null)
									usbEndpointTx0 = tempEndpoint;
								if(usbEndpointTx1 == null)
									usbEndpointTx1 = tempEndpoint;
							} else if (tempEndpoint.getDirection() == UsbConstants.USB_DIR_IN) {
								if(usbEndpointRx0 == null)
									usbEndpointRx0 = tempEndpoint;							
								if(usbEndpointRx1 == null)
									usbEndpointRx1 = tempEndpoint;
							}
						}	
					}
					
					if (usbEndpointTx0 != null && usbEndpointRx0 != null)
					{
						// Fix: Use first bulk endpoints as control channel.
						usbEndpointControlRx = usbEndpointRx0;
						usbEndpointControlTx = usbEndpointTx0;						
						/* Found out endpoints to communicate */
						Log.d(TAG, "Found out Endpoint RX: " + usbEndpointControlRx.toString() + " TX: " + usbEndpointControlTx.toString());
						Toast.makeText(appActivity,  "Foundout Endpoint RX/TX" , Toast.LENGTH_SHORT).show();
						return true; 
					}
				}
			}
		}
		
		Log.d(TAG, "Not found an Endpoint");
		
		return false;
	}
	
	private class hostUSBDemonThread extends Thread {
		
		public boolean isRunning = true;
		private boolean isConnected = false;
		
		public void run() {
			while(isRunning && !appActivity.isDestroyed()) {
				if(usbManager != null) {
					if(setUsbDevice(appActivity.getIntent())) {
						if(!isConnected) {
							isConnected = true;
							//Fix: Send information of changed.
                        	if(connectUsbDevice(usbManager, appActivity.getIntent())) {
                        		startService();	                        	
                        	}
/*                        	Looper.prepare();
							Toast.makeText(appActivity.getApplicationContext(),  "USB device Connected", Toast.LENGTH_SHORT).show();
							Looper.loop();
*/							Log.d(TAG, "USB device Conneced in Thread");
						}
					} else {
						if(isConnected) {
							isConnected = false;
							hasPermission = false;
							//Fix: Send information of changed.
/*                        	Looper.prepare();
							Toast.makeText(appActivity.getApplicationContext(),  "USB device Disconnected", Toast.LENGTH_SHORT).show();
							Looper.loop();
*/							Log.d(TAG, "USB device Disconneced in Thread");
						}						
					}
				
					
				}
				
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private synchronized boolean setUsbDevice(Intent intent)
	{
		usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		
		if(usbDevice != null)
		{
			vendorID = usbDevice.getVendorId();
			productID = usbDevice.getProductId();
			return true;
		}
		
		HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while(deviceIterator.hasNext()){
			
		    usbDevice = deviceIterator.next();
		    if(usbDevice != null)
			{
				vendorID = usbDevice.getVendorId();
				productID = usbDevice.getProductId();
				return true;
			}	
		}				
		
		Log.d(TAG, "setUsbDevice: Cannot get usbDevice instance");
		return false;
	}
	
	private boolean connectUsbDevice(UsbManager um, Intent intent)
	{
		Log.d(TAG, "Connect USB device");
		Toast.makeText(appActivity,  "Connect USB device" , Toast.LENGTH_SHORT).show();
		
		if(setUsbDevice(intent) && scanEndpoint(usbDevice)) {
//		if(setUsbDevice(intent) && usbVidPidChecker(vendorID, productID) && scanEndpoint(usbDevice)) {
			usbInterface = usbDevice.getInterface(USB_DEFAULT_CONTROL_INTERFACE);
		
			if(usbInterface != null) {
				if(!hasPermission)
				{
					usbManager.requestPermission(usbDevice, permissionIntent);
					Log.d(TAG,"connectUsbDevice: Request Permission");
					return false;
				}
				
				usbDeviceConnection = um.openDevice(usbDevice);		
				if(usbDeviceConnection != null){
					usbDeviceConnection.claimInterface(usbInterface, true); /* Use interface exclusive */
// Fix: Move to openDevice before					usbManager.requestPermission(usbDevice, permissionIntent);
					return true;
				}
			}
		}
		
		Log.d(TAG,"connectUsbDevice: No device in host");
		
		return false;
	}

	private void startService()
	{
		byte[] protocol = new byte[2];
		
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_IN|UsbConstants.USB_TYPE_VENDOR,
											AOAP_GET_PROTOCOL, 0, 0, protocol, 2, 0);

		int getProto = ((protocol[1]<<8) | protocol[0]);
			Log.d(TAG, "USB Device protocol is " + getProto);
		
		if (getProto < 1) {
			Log.d(TAG, "AOAP supports protocol 1 or 2");
			return;
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

	}
	
	
	@Override
	protected boolean openSocket(boolean reconnect) throws Exception {
				
		return true;
	}

	BroadcastReceiver usbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			
			Log.d(TAG,"Boadcast Recevicer: " + intent.getAction());

			if (actionUsbPermission.equals(intent.getAction())) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                synchronized (this) {	                    
	                        if (usbDevice != null)
	                        {
	                        	hasPermission = true;
	                        	if(connectUsbDevice(usbManager, intent)) {
	                        		startService();	                        	
	                        		Log.d(TAG, "permission granted ");
	                        	}
	                        }
	                }        
	             }
				 else 
                     Log.d(TAG, "permission denied for Hostmode ");				
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
            	connectUsbDevice(usbManager, intent);
            	startService();
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
				// Fix: Close usb connection 
				usbDeviceConnection.releaseInterface(usbInterface);
				usbDeviceConnection.close();
				
			}
		}
	};
	
	@Override
	protected void setupSocket() throws Exception {
		
	}

	@Override
	protected void readSocket() throws Exception {

		int ret = 0;
		byte[] buffer = new byte[READ_BYTE_BUFFER_SIZE];
				
		while(isStarted())
		{				
			if( !isConnected ) {
				isConnected = true;
				session.sessionAccepted(usbManager);				
			}
			
			ret = usbDeviceConnection.bulkTransfer(usbEndpointControlRx, buffer, buffer.length, 500);
			
			if( ret > 0 ) {
				readQueues.put( new FlexBuffer(buffer) );
				Log.d(TAG,"Received Packet" + ret); // input Queue;
			}
		}
	}

	@Override
	public void close(boolean reset) throws Exception {
		// TODO Auto-generated method stub
		if(appActivity != null)
			appActivity.unregisterReceiver(usbReceiver);		
	}

	@Override
	public SocketAddress localAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketAddress remoteAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
