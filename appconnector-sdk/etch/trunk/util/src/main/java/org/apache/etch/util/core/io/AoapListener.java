package org.apache.etch.util.core.io;

import java.io.IOException;
import java.net.SocketAddress;

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
import android.util.Log;

public class AoapListener extends Connection<SessionListener<UsbManager>>
		implements Transport<SessionListener<UsbManager>> {

	private static final String TAG = "AoapListener";
	
	private Activity appActivity = null;
	
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
	private int vendorID;
	private int productID;
	
	private static final int USB_VENDORID_GOOGLE =		0x18D1;
	private static final int USB_VENDORID_MOTOROLA =	0x22B8;
	private static final int USB_VENDORID_SAMSUNG =		0x18D1;
	private static final int USB_VENDORID_LG = 			0x1004;
	private static final int USB_VENDORID_SHARP = 		0x04DD;
	private static final int USB_VENDORID_LENOVO = 		0x17EF;

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
	
	private static final int AOAP_STRING_MANUFACTURER = 0;
	private static final int AOAP_STRING_MODEL = 1;

	private static final String manufacturer = "HKMC";
	private static final String model = "VIT";
	
	private static final int USB_DEFAULT_CONTROL_INTERFACE	= 0x00;
	private static final int USB_XBULK_ENDPOINT_NEEDED_NUM	= 0x02;
		
	public AoapListener (Activity app, UsbManager um)
	{
		if(app != null && um != null) {
			appActivity = app;
			usbManager = um;
		}		
	}
	
	private boolean usbVidPidChecker(int vid, int pid)
	{
		// Fix: if(Vendor Checker ????? )
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
						return true; 
					}
				}
			}
		}
		
		Log.d(TAG, "Not found an Endpoint");
		
		return false;
	}
	
	private void setUsbDevice(Intent intent)
	{
		usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		vendorID = usbDevice.getVendorId();
		productID = usbDevice.getProductId();
		
	}
	
	private boolean connectUsbDevice(UsbManager um, Intent intent)
	{
		permissionIntent = PendingIntent.getBroadcast(appActivity, 0, new Intent(actionUsbPermission), 0);
		
		setUsbDevice(intent);
		
		if(usbVidPidChecker(vendorID, productID) && scanEndpoint(usbDevice)) {
			usbInterface = usbDevice.getInterface(USB_DEFAULT_CONTROL_INTERFACE);
		
			if(usbInterface != null) {
				usbDeviceConnection = um.openDevice(usbDevice);		
				if(usbDeviceConnection != null){
					usbDeviceConnection.claimInterface(usbInterface, true); /* Use interface exclusive */
					usbManager.requestPermission(usbDevice, permissionIntent);
					return true;
				}
			}
		}		
		return false;
	}

	private void startService()
	{
		IntentFilter filter = new IntentFilter(actionUsbPermission);
		
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		appActivity.registerReceiver(usbReceiver, filter);
		
		/* Send information of USB Accessory */											
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT|UsbConstants.USB_TYPE_VENDOR, 
											AOAP_SEND_STRING, 0, AOAP_STRING_MANUFACTURER, manufacturer.getBytes(), manufacturer.length(), 0);
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT|UsbConstants.USB_TYPE_VENDOR, 
											AOAP_SEND_STRING, 0, AOAP_STRING_MODEL,model.getBytes(), model.length(), 0);
		/* Start Accessory mode */
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_OUT|UsbConstants.USB_TYPE_VENDOR, 
											AOAP_START_ACCESSORY, 0, 0, null, 0, 0);
	}
	
	
	@Override
	protected boolean openSocket(boolean reconnect) throws Exception {
		
		if(usbManager != null && appActivity != null && !reconnect )
		{
			/* Check access permission by application */
			actionUsbPermission = appActivity.getPackageName() + ".action.USB_PERMISSION";
			Intent startIntent = appActivity.getIntent();
			
			if(connectUsbDevice(usbManager, startIntent))
				return true;
		}
				
		return false;
	}

	BroadcastReceiver usbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (actionUsbPermission.equals(intent.getAction())) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                synchronized (this) {	                    
	                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                        if (usbDevice != null)
	                        	usbManager.openDevice(usbDevice);
	                        Log.d(TAG, "permission granted ");        
	                    } else 
	                        Log.d(TAG, "permission denied for Hostmode ");        
	                }
				} 
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
            	setUsbDevice(intent);
    			usbManager.requestPermission(usbDevice, permissionIntent);
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
				// Fix: Close usb connection 
			}
		}
	};
	
	@Override
	protected void setupSocket() throws Exception {
		startService();
	}

	@Override
	protected void readSocket() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(boolean reset) throws Exception {
		// TODO Auto-generated method stub
		
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
