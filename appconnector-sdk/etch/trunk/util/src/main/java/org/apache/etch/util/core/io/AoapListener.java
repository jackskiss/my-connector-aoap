package org.apache.etch.util.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
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
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
	private int vendorID;
	private int productID;

	private static final int USB_PERMISSION_NO = 0;
	private static final int USB_PERMISSION_PENDING = 1;	
	private static final int USB_PERMISSION_HAVE = 1;	
	private int havePermission = USB_PERMISSION_NO;
	
	private static final int USB_VENDORID_GOOGLE =				0x18D1;
	private static final int USB_VENDORID_MOTOROLA =				0x22B8;
	private static final int USB_VENDORID_SAMSUNG =				0x04E8;
	private static final int USB_VENDORID_LG = 					0x1004;
	private static final int USB_VENDORID_SHARP = 				0x04DD;
	private static final int USB_VENDORID_LENOVO = 				0x17EF;

	/* AOA 1.0 */
	private static final int USB_PRODUCTID_ACCESSORY = 			0x2D00;
	private static final int USB_PRODUCTID_ACCESSORY_ADB = 		0x2D01;
	/* AOA 2.0 */
	private static final int USB_PRODUCTID_AUDIO = 				0x2D02;
	private static final int USB_PRODUCTID_AUDIO_ADB = 			0x2D03;
	private static final int USB_PRODUCTID_ACCESSORY_AUDIO = 		0x2D04;
	private static final int USB_PRODUCTID_ACCESSORY_AUDIO_ADB = 	0x2D05;

	private static final int AOAP_GET_PROTOCOL = 		51;
	private static final int AOAP_SEND_STRING = 		52;
	private static final int AOAP_START_ACCESSORY = 	53;
	private static final int AOAP_SUPPORT_AUDIO = 	58;
	
	private static final int AOAP_STRING_MANUFACTURER = 0;
	private static final int AOAP_STRING_MODEL = 1;

	private static final String manufacturer = "HKMC";
	private static final String model = "VIT";
	
	private static final int USB_DEFAULT_CONTROL_INTERFACE	= 0x00;
	private static final int USB_XBULK_ENDPOINT_NEEDED_NUM	= 0x02;
	
	private static final int READ_QUEUE_SIZE = 100; // Fix: Need to adjust particular this
	private static final int READ_BYTE_BUFFER_SIZE = 16384; // Fix: Need to adjust particular this
	
	private BlockingQueue<ByteBuffer> readQueues = null;
	private AoapPacketizer aoapPacket;
	
	/*SS - AOAP 1.0 SPECIFIED PROTOCOL FOR AUDIO STREAM - SS*/
	private BlockingQueue<ByteBuffer> readStreamQueues = null;
	private hostAudioDemonThread hostAudioDemon;

	/*EE - AOAP 1.0 SPECIFIED PROTOCOL FOR AUDIO STREAM - EE*/
	
	public AoapListener (Activity app, UsbManager um)
	{
		
		if(app != null && um != null) {
			appActivity = app;
			usbManager = um;

			/* Check access permission by application */
			actionUsbPermission = appActivity.getPackageName() + ".USB_PERMISSION";
			
			Log.d(TAG, "AoapListener Creation:" + actionUsbPermission);
			permissionIntent = PendingIntent.getBroadcast(appActivity.getApplicationContext(), 0, new Intent(actionUsbPermission), 0);
			
			IntentFilter filter = new IntentFilter(actionUsbPermission);

			filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
			filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
			appActivity.registerReceiver(usbReceiver, filter);			
			
			Intent startIntent = appActivity.getIntent();

			/* Initialize */
			havePermission = USB_PERMISSION_NO;
			isConnected = false;
			aoapPacket = new AoapPacketizer();
			
			// For Audio Stream
			audioStreamingInit();
			
			usbConnectHandler.sendEmptyMessage(USB_CONNECT_HANDLER);
		}
	}
	
	private final static int USB_CONNECT_HANDLER = 1;
	
	Handler usbConnectHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == USB_CONNECT_HANDLER )
			{
				if(usbManager != null)
					connectUsbDevice(usbManager, appActivity.getIntent());
			}
		}
		
	};
	
	private final static int TOAST_MESSAGE = 1;
	
	Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg){
			if(msg.what == TOAST_MESSAGE)
			{
				Toast.makeText(appActivity, (String) msg.obj, Toast.LENGTH_SHORT).show();
			}
		}
	};
	

	private void audioStreamingInit()
	{
		readStreamQueues = new ArrayBlockingQueue<ByteBuffer>(READ_QUEUE_SIZE);
		if(hostAudioDemon == null)
			hostAudioDemon = new hostAudioDemonThread();
	}
	
	private void aoapToastMessage(String msg)
	{
		
		Message tmsg = Message.obtain();
		
		tmsg.what = TOAST_MESSAGE;
		tmsg.obj = msg;
		
		mToastHandler.sendMessage(tmsg);
	}
	
	@SuppressWarnings("deprecation")
	public void finalize()
	{
/*		
		if(usbHostDemon != null)
			usbHostDemon.stop();
		
		if(hostAudioDemon != null) {
			hostAudioDemon.setAudioThreadState(false);
			hostAudioDemon.stop();
		}
*/		
		if(usbReceiver != null && appActivity != null)
			appActivity.unregisterReceiver (usbReceiver);

	}

	public BlockingQueue<ByteBuffer> allocReadQueue()
	{				
       Log.d(TAG, "allocReadQueue");
		
		readQueues = new ArrayBlockingQueue<ByteBuffer>(READ_QUEUE_SIZE);
		return readQueues;
	}

	private ByteBuffer getReadQueue()
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
        // Log.d(TAG, "VID PID Check Vid: " + vid + " Pid: " + pid);
		
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
		
		usbInterface = null; //
		
		for (int idx=0; idx<infCount; idx++)
		{
			tempInterface = usbDevice.getInterface(idx);
			
			/* Assign endpoints for bulk transfer */
			if(tempInterface.getInterfaceClass() == UsbConstants.USB_CLASS_VENDOR_SPEC ) {
				if(usbInterface == null)
				{
					usbInterface = tempInterface;
					openUsbDeviceConnection(usbInterface);
				}
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
						// Fix: Use first bulk Endpoints as control channel.
						usbEndpointControlRx = usbEndpointRx0;
						usbEndpointControlTx = usbEndpointTx0;						
						/* Found out Endpoints to communicate */
						Log.d(TAG, "Found out Endpoint RX: " + usbEndpointControlRx.toString() + " TX: " + usbEndpointControlTx.toString());
						aoapToastMessage("Foundout Endpoint RX/TX");
						return true; 
					}
				}
			}
		}
		
		Log.d(TAG, "Not found an Endpoint");
		
		return false;
	}
	
	private class hostAudioDemonThread extends Thread {
		int sampleRate = 44100;
		int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		int bufferSize = 0;
		private boolean isAudioStreaming = false;
		
		public void run() {
			// Fix: sampleRate, channelConfig, audioFormat must get from audio information.
			bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
			Log.d(TAG, "Initial buffer size is " + bufferSize);
			AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, 
												channelConfig, audioFormat, 4*bufferSize, AudioTrack.MODE_STREAM);
			do {
				try {
					ByteBuffer buffer = readStreamQueues.take();
					track.write(buffer.array(), 0, buffer.position());
					track.play();
					sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (isAudioStreaming);
			track.flush();
			track.stop();
		}
		
		public void setAudioThreadState(boolean set)
		{
			isAudioStreaming = set;
		}
	}
	
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
								vendorID = dev.getVendorId();
								productID = dev.getProductId();
								usbConnectHandler.sendEmptyMessage(USB_CONNECT_HANDLER);
								Log.d(TAG, "Found out Google Device : " +  dev.getVendorId() + "/" + dev.getProductId() );
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
				havePermission = USB_PERMISSION_NO; // New Connection Start
				return;
			}
		}
	}
	
	private synchronized boolean setUsbDevice(Intent intent)
	{
		usbDevice = null;
		vendorID = 0;
		productID = 0;
		
		for(UsbDevice dev : usbManager.getDeviceList().values())
		{
			if(usbVidPidChecker(dev.getVendorId(), dev.getProductId()))
			{
				usbDevice = dev;
				break;
			}
		}
		
		if(usbDevice == null)
		{
			usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			if(usbDevice != null) {
				vendorID = usbDevice.getVendorId();
				productID = usbDevice.getProductId();
			}
		}

		if(usbDevice != null) {
			Log.d(TAG, "Vendor ID: " + usbDevice.getVendorId() + " Product ID: " + usbDevice.getProductId());
			return true;
		}

		Log.d(TAG, "setUsbDevice: Cannot get usbDevice instance");
		return false;
	}
	
	synchronized private boolean connectUsbDevice(UsbManager um, Intent intent)
	{
		Log.d(TAG, "Connecting USB device");
	
		if(havePermission == USB_PERMISSION_NO)
		{
			if(setUsbDevice(intent))
			{
				if(true) //!usbManager.hasPermission(usbDevice))
				{
					usbManager.requestPermission(usbDevice, permissionIntent);
					havePermission = USB_PERMISSION_HAVE;
					Log.d(TAG,"connectUsbDevice: Request Permission");
				
					Handler handle = new Handler();
					handle.postDelayed(new Runnable() {
						public void run () {
							usbConnectHandler.sendEmptyMessage(USB_CONNECT_HANDLER);;
						}
					}, 2000); /* Adjust next */
				} else {
					havePermission = USB_PERMISSION_HAVE;
					Log.d(TAG,"connectUsbDevice: Already Permission have");
					usbConnectHandler.sendEmptyMessage(USB_CONNECT_HANDLER);
				}
				return true;
			}	
		} else if (havePermission == USB_PERMISSION_HAVE) {			
			/* During device permission or entering into accessory mode, vendorID and productID will be seen as 0 values */
			if(vendorID == 0 && productID == 0) {
				usbHostDemon = new hostUSBDemonThread();
				usbHostDemon.start();
				return true;
			}			
			// If this state is Google Mode 			
			if(usbVidPidChecker(vendorID, productID)) {
				if(scanEndpoint(usbDevice))
				{
					// Audio Stream
					hostAudioDemon.start();
					Log.d(TAG, "Connection completed");
					aoapToastMessage("Connection Completed");
					return true;
				}
			} else {
				startAccessoryService();
				havePermission = USB_PERMISSION_NO; // For reconnection to Google Accessory Device. 
				usbHostDemon = new hostUSBDemonThread();
				usbHostDemon.start();				
				return true;
			}
			
		}
		
		Log.d(TAG,"connectUsbDevice: No connect yet");
		return false;	
	}

	private boolean openUsbDeviceConnection(UsbInterface usbinf)
	{	
		if(usbDeviceConnection != null) 
		{
			usbDeviceConnection.releaseInterface(usbInterface);
			usbDeviceConnection.close();
		}
			
		usbDeviceConnection = usbManager.openDevice(usbDevice);		
		
		if(usbDeviceConnection == null)
		{
			Log.d(TAG,"connectUsbDevice: usbDeviceConnection is null");
			return false;
		}		
		usbDeviceConnection.claimInterface(usbinf, true); /* Use interface exclusive */
		return true;
	}
	
	private void startAccessoryService()
	{
		byte[] protocol = new byte[2];
		
		if(usbDevice == null)
			return;
		
		usbInterface = usbDevice.getInterface(USB_DEFAULT_CONTROL_INTERFACE);
		
		if(!openUsbDeviceConnection(usbInterface))
		{
			Log.d(TAG, "Fail to open openUsbDeviceConnection");
			return;			
		}
		
		usbDeviceConnection.controlTransfer(UsbConstants.USB_DIR_IN|UsbConstants.USB_TYPE_VENDOR,
											AOAP_GET_PROTOCOL, 0, 0, protocol, 2, 0);

		int getProto = ((protocol[1]<<8) | protocol[0]);
			Log.d(TAG, "USB Device protocol is " + getProto);
		
		if (getProto < 1) {
			Log.d(TAG, "AOAP should supports protocol 1 or 2");
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
		
		isConnected = true;
		Log.d(TAG,"startService: Connnect Accessory Protocol");		
	}
	
	
	@Override
	protected boolean openSocket(boolean reconnect) throws Exception {
				
		return true;
	}

	BroadcastReceiver usbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			
			Log.d(TAG,"Boadcast Recevicer: " + intent.getAction());
			aoapToastMessage("Boadcast Recevicer: " + intent.getAction());
			
			if (actionUsbPermission.equals(intent.getAction())) {
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
	                synchronized (this) {	                    
	                        if (usbDevice != null)
	                        {
	                        	havePermission = USB_PERMISSION_HAVE;
	                        	if(connectUsbDevice(usbManager, intent)) {
	                        		Log.d(TAG, "permission granted ");
	                        	}
	                        }
	                }        
	             }
				 else 
                     Log.d(TAG, "permission denied for Hostmode ");				
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
            	havePermission = USB_PERMISSION_NO;
            	if(connectUsbDevice(usbManager, intent)) {
            		Log.d(TAG, "Device attaced and ");
            	}
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
            	 // Fix: Close USB connection 
				usbDeviceConnection.releaseInterface(usbInterface);
				usbDeviceConnection.close();
				havePermission = USB_PERMISSION_NO;
				isConnected = false;
            }
		}
	};
	
	@Override
	protected void setupSocket() throws Exception {
		
	}

	@Override
	protected void readSocket() throws Exception {
		
		while(isStarted())
		{				
			if( !isConnected ) {
				isConnected = true;
				session.sessionAccepted(usbManager);				
			}
			
			try {
				readPacket();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readPacket() throws Throwable
	{
		int ret = 0;
		
		ByteBuffer srcBuffer = ByteBuffer.allocate(READ_BYTE_BUFFER_SIZE);
		
		ret = usbDeviceConnection.bulkTransfer(usbEndpointControlRx, srcBuffer.array(), srcBuffer.array().length, 500);;
	
		if(ret > 0) {
			Log.d(TAG,"Received Packet" + ret);
			
			ByteBuffer dstBuffer = ByteBuffer.allocate(ret - AoapPacketizer.AOAP_PACKET_HEADER_LENGTH);
			
			byte type = aoapPacket.getAoapPacketType(srcBuffer);
			if(aoapPacket.checkAoapPacket(srcBuffer)) {
				if(aoapPacket.aoapUnPacket(type, srcBuffer, dstBuffer) > 0) {
					switch(type) {
					case AoapPacketizer.AOAP_AUDIO_PACKET:
						// Input Audio Buffer
						readStreamQueues.put(dstBuffer);
						break;
					case AoapPacketizer.AOAP_ETCH_PACKET:
						// Input Etch Buffer
						readQueues.put(dstBuffer);
						break;
					default: 
						break;
					}
				}
				
			} else {
				Log.d(TAG, "Received broken packet");
			}
		}
		sleep(200);
	}
	
	
	private void sleep(int i) {
		// TODO Auto-generated method stub
		
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
