package org.apache.etch.util.core.io;

import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.etch.util.FlexBuffer;
import org.apache.etch.util.core.Who;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

public class AoapConnection extends Connection<SessionPacket> implements
		TransportPacket, Runnable {
	
	private static final String TAG = "AoapConnection"; 
	private static String actionUsbPermission;
	private UsbManager usbManager = null;
	private UsbAccessory usbAccessory = null;
	private AoapListener listener = null;
	
	private Activity appActivity = null;

    private ParcelFileDescriptor fileDescriptor;
    private FileInputStream inputStream = null;
    private FileOutputStream outputStream = null;	

	private static final int USB_PERMISSION_NO = 0;
	private static final int USB_PERMISSION_PENDING = 1;	
	private static final int USB_PERMISSION_HAVE = 1;
	private int havePermission = USB_PERMISSION_NO;
	private PendingIntent permissionIntent;
	
    private BlockingQueue<ByteBuffer> readQueues;
    private BlockingQueue<ByteBuffer> sendQueues;
    
    private static final int SEND_QUEUE_SIZE = 100; // Fix: Need to adjust particular this
	private static final int READ_BYTE_BUFFER_SIZE = 16*1024; // Fix: Need to adjust particular this
	
	private AoapPacketizer aoapPacket;
	
	private usbPacketTransmitThread usbPacketDemon;

	public AoapConnection(Activity app, UsbManager um, AoapListener listener) {
		
		Log.d(TAG, "Activity Connection creator");
		
		if(app == null || um == null)
		{
			Log.d(TAG, "Error: app or um is null");
			return;
		}
				
		usbManager = um;
		appActivity = app;	
		this.listener = listener;
		readQueues = listener.allocReadQueue();
	}

	public AoapConnection(Activity app, UsbManager um ) {

		Log.d(TAG, "Activity Connection creator");
		
		if(app == null || um == null)
		{
			Log.d(TAG, "Error: app or um is null");
			return;
		}
		
		usbManager = um;
		appActivity = app;	
		
		/* Initialize */
		havePermission = USB_PERMISSION_NO;
		sendQueues = new ArrayBlockingQueue<ByteBuffer>(SEND_QUEUE_SIZE);
		
		setUsbIntentFilter(appActivity);
		
		setUSBDevice();
	}

/*	
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
*/
	private void setUsbIntentFilter(Activity app)
	{
		/* To check access permission by application */	
		actionUsbPermission = app.getPackageName() + ".USB_PERMISSION";
		Intent startIntent = app.getIntent(); 

		Log.d(TAG, "Accessory Intent Persmission: " + actionUsbPermission);
		permissionIntent = PendingIntent.getBroadcast(app, 0, new Intent(actionUsbPermission), 0);
		
		IntentFilter filter = new IntentFilter(actionUsbPermission);
		
		//filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		
		appActivity.registerReceiver(usbReceiver, filter);		
	}
	
	@SuppressWarnings("deprecation")
	public void finalize()
	{	
		if(usbReceiver != null && appActivity != null)
			appActivity.unregisterReceiver (usbReceiver);
	}
	
	Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg){
			if(msg.what == TOAST_MESSAGE)
			{
				Toast.makeText(appActivity, (String) msg.obj, Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private final static int TOAST_MESSAGE = 1;
	
	private void aoapToastMessage(String msg)
	{
		
		Message tmsg = Message.obtain();
		
		tmsg.what = TOAST_MESSAGE;
		tmsg.obj = msg;
		
		mToastHandler.sendMessage(tmsg);
	}
	
	private boolean openAccessory(UsbAccessory accessory) {
        
    	Log.d(TAG, "openAccessory: " + accessory);
    	
        fileDescriptor = usbManager.openAccessory(accessory);
        
        if (fileDescriptor != null) {
        
        	FileDescriptor fd = fileDescriptor.getFileDescriptor();
        	Log.d(TAG, "File Descriptor" + fd);
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
            
            if(inputStream != null && outputStream != null) {
        		usbPacketDemon = new usbPacketTransmitThread();
        		usbPacketDemon.start();
        		Log.d(TAG, "openAccessory success");
        		aoapToastMessage("openAccessory: Connected");
        		return true;
            }
        } 

        Log.d(TAG, "openAccessory fail");
        aoapToastMessage("openAccessory: Connection Fail");

        return false;
    }	
	
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
	
            Log.d(TAG, "Received Event" + intent.getAction());
            aoapToastMessage("Received Event" + intent.getAction());
        	/* USB Permission message */
            if (actionUsbPermission.equals(intent.getAction())) { 
                synchronized (this) {
                    usbAccessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    	havePermission = USB_PERMISSION_HAVE;
                        if (usbAccessory != null) {
                            openAccessory(usbAccessory);
                        }
                    } else {
                        Log.d(TAG, "permission denied for accessory " + usbAccessory);
                    }
                }
            }
            /* USB Disconnection message */
            else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(intent.getAction()))
            {
            	usbAccessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				/* Precondition: Only one accessory can be connected with this device */
            	if (usbAccessory != null) 
				{
					
				} 
            	havePermission = USB_PERMISSION_NO;
            }
            /* USB Disconnection message */
            else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(intent.getAction()))
            {
        		if(setUSBDevice())
        			openAccessory(usbAccessory);
            }
        }
    };    
    
	@Override
	public int headerSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void transportPacket(Who recipient, FlexBuffer buf) throws Exception {
		
		if(buf.length() > 0) {
			Log.d(TAG, "transporPacket buf length: " + buf.getBuf().length);
			ByteBuffer sendBuf = ByteBuffer.allocate(buf.getBuf().length);
			sendBuf.put(buf.getBuf());
			sendQueues.put(sendBuf);
		}		
	}

	private boolean checkUsbAccessory()
	{
		
		UsbAccessory[] accessoryList = usbManager.getAccessoryList();
		
		usbAccessory = null;
		
		for(UsbAccessory acc : accessoryList) {
			Log.d(TAG, "Manufacturer: " + acc.getManufacturer() + " Model:" + acc.getModel());
			if(acc.getModel().equals("VIT") && acc.getManufacturer().equals("HKMC")) {
				usbAccessory = acc;
				break;
			}
		}

		if(usbAccessory == null) {
			usbAccessory = (UsbAccessory) appActivity.getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
		}
		
		if(usbAccessory != null) {
			Log.d(TAG, "Found out Manufacturer: " + usbAccessory.getManufacturer() + " Model:" + usbAccessory.getModel());
			return true;
		}
		
		return false;
	}
	
	private boolean setUSBDevice()
	{
		
		if(usbManager != null && appActivity != null && havePermission != USB_PERMISSION_HAVE)
		{
			if(!checkUsbAccessory())
				return false;
			if(usbAccessory != null) {
				if(!usbManager.hasPermission(usbAccessory))
				{		
					usbManager.requestPermission(usbAccessory, permissionIntent);
					havePermission = USB_PERMISSION_PENDING;
					Log.d(TAG, "Request permission");
					return false;
				}
				return true;
			}
		}
	
		return false;
	}
	
	@Override
	protected boolean openSocket(boolean reconnect) throws Exception {

		// if a one time connection from a server socket listener, just
		// return the existing socket. Bail if this is a reconnect.
		
       Log.d(TAG, "openSocket request connection :" + reconnect );

		if (listener != null)
		{
			if (!reconnect && readQueues == null)
				readQueues = listener.allocReadQueue();

			return !reconnect;
		}
		
		return true;
	}

	@Override
	protected void setupSocket() throws Exception {

	}

	@Override
	protected void readSocket() throws Exception {
		// Fix: Read buffer
		
		FlexBuffer flexBuffer = new FlexBuffer();
        int ret = 0;
        
        while (isStarted())
        {  
        	if(readQueues != null)
        	{
        		flexBuffer.get(readQueues.take().array());
        	}
        	else
        	{
        		ByteBuffer buf;
        		
				try {
					buf = readPacket();
					if(buf != null) {
	        			flexBuffer = new FlexBuffer(buf.array());
	        			flexBuffer.setIndex(0);
	        			flexBuffer.setLength(buf.position());
	        		}
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	// Send Message
	        session.sessionPacket(null, flexBuffer);
	        Log.d(TAG,"Received packet length" + flexBuffer.length());
        }
	}
	
	private ByteBuffer readPacket() throws Throwable
	{
		int ret = 0;
		
		if(inputStream == null)
			return null;

		ByteBuffer srcBuffer = ByteBuffer.allocate(READ_BYTE_BUFFER_SIZE);		

		ret = inputStream.read(srcBuffer.array(),  0,  READ_BYTE_BUFFER_SIZE); 
	
		Log.d(TAG,"Received Packet" + ret);
		
		if(ret > 0) {
			
			ByteBuffer dstBuffer = ByteBuffer.allocate(ret - AoapPacketizer.AOAP_PACKET_HEADER_LENGTH);
			
			byte type = aoapPacket.getAoapPacketType(srcBuffer);
			if(aoapPacket.checkAoapPacket(srcBuffer)) {
				if(aoapPacket.aoapUnPacket(type, srcBuffer, dstBuffer) > 0) {
					switch(type) {
					case AoapPacketizer.AOAP_ETCH_PACKET:
						// Input Etch Buffer
						readQueues.put(dstBuffer);
						return dstBuffer;
					default: 
						break;
					}
				}
				
			} else {
				Log.d(TAG, "Received broken packet");
			}
		}
	
		return null;
		
	}
	
	@Override
	public void close(boolean reset) throws Exception {
		if(usbReceiver != null && appActivity != null)
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
	
	public class usbPacketTransmitThread extends Thread {
		
		int ret = 0;
		AoapPacketizer aoapPacket = new AoapPacketizer();
		ByteBuffer srcBuf;
		ByteBuffer dstBuf;
		
		public void run() {	
			
			do {
				
				ByteBuffer srcBuf = AudioDecoder.getDecodedBuffer();
			
				if(srcBuf != null) {
					dstBuf = ByteBuffer.allocate(srcBuf.position() + AoapPacketizer.AOAP_PACKET_HEADER_LENGTH);
					ret = aoapPacket.aoapSetPacket((byte) AoapPacketizer.AOAP_AUDIO_PACKET, srcBuf, dstBuf);
					if(ret > 0) {
						try {
							Log.d(TAG, "Transport Audio packet buffer length:" + dstBuf.position());
							outputStream.write(dstBuf.array());
							
					    } catch (IOException e) {
					        Log.e(TAG, "write failed", e);
					    }
					}
				}
				
				try {
					srcBuf = sendQueues.take();
					
					if(srcBuf != null) {
						dstBuf = ByteBuffer.allocate(srcBuf.position() + AoapPacketizer.AOAP_PACKET_HEADER_LENGTH);
						ret = aoapPacket.aoapSetPacket((byte) AoapPacketizer.AOAP_ETCH_PACKET, srcBuf, dstBuf);
						if(ret > 0) {
							try {
								Log.d(TAG, "Transport ETCH packet buffer length:" + dstBuf.position());
								outputStream.write(dstBuf.array());
								
						    } catch (IOException e) {
						        Log.e(TAG, "write failed", e);
						    }
						}
					}
					
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while(true);
		}	
	}
 
}
