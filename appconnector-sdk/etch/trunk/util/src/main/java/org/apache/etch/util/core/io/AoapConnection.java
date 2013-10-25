package org.apache.etch.util.core.io;

import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
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
	
    private BlockingQueue<FlexBuffer> readQueue;
    
	private static final int READ_BYTE_BUFFER_SIZE = 16384; // Fix: Need to adjust particular this

	public AoapConnection(Activity app, UsbManager um, AoapListener listener) {
		
		Log.d(TAG, "Activity Connection creator");
		
		if(app == null || um == null)
		{
			Log.d(TAG, "Error: app or um is null");
			return;
		}
		
		aoapToastMessage("Activity Connection creator");
		
		usbManager = um;
		appActivity = app;	
		this.listener = listener;
		readQueue = listener.allocReadQueue();

	}

	public AoapConnection(Activity app, UsbManager um ) {

		Log.d(TAG, "Activity Connection creator");
		
		if(app == null || um == null)
		{
			Log.d(TAG, "Error: app or um is null");
			return;
		}
		
		aoapToastMessage("Activity Connection creator");
		
		usbManager = um;
		appActivity = app;	
		
		/* Initialize */
		havePermission = USB_PERMISSION_NO;
				
		IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
		appActivity.registerReceiver(usbReceiver, filter);
		
		if(setUSBDevice())
			openAccessory(usbAccessory);
		
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
    	aoapToastMessage("openAccessory: " + accessory);
        fileDescriptor = usbManager.openAccessory(accessory);
        
        if (fileDescriptor != null) {
        
        	FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
 
            // Fix: Check if need to thread. 
            // Thread thread = new Thread(null, this, "AoapConnection Thread");
            // thread.start();
            
            Log.d(TAG, "openAccessory succeeded");
            aoapToastMessage("openAccessory: Connected");
            return true;
        } else {
            Log.d(TAG, "openAccessory fail");
            aoapToastMessage("openAccessory: Connection Fail");
            return false;
        }
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

		if(outputStream != null)
		{
			try {
		       Log.d(TAG, "Transport packet buffer length:" + buf.length());
	            outputStream.write(buf.getBuf());
	        } catch (IOException e) {
	            Log.e(TAG, "write failed", e);
	        }
		}
		
	}

	private boolean setUSBDevice()
	{
		
		if(usbManager != null && appActivity != null && havePermission != USB_PERMISSION_HAVE)
		{
			/* Check access permission by application */
			actionUsbPermission = appActivity.getPackageName() + ".USB_PERMISSION";
			Intent startIntent = appActivity.getIntent(); 
			usbAccessory = (UsbAccessory) startIntent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);	   
			permissionIntent = PendingIntent.getBroadcast(appActivity, 0, new Intent(actionUsbPermission), 0);
			
			if(usbAccessory != null) {
				if(!usbManager.hasPermission(usbAccessory))
				{
					usbManager.requestPermission(usbAccessory, permissionIntent);
					havePermission = USB_PERMISSION_PENDING;
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
			if (!reconnect && readQueue == null)
				readQueue = listener.allocReadQueue();

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
		
		FlexBuffer flexBuffer = null;
        int ret = 0;
        
        byte[] buffer = new byte[READ_BYTE_BUFFER_SIZE];
        
        while (isStarted())
        {  
        	if(readQueue != null)
        	{
        		flexBuffer = readQueue.take();
        	}
        	else
        	{
        		ret = inputStream.read(buffer);
        		if(ret > 0) {
        			flexBuffer = new FlexBuffer(buffer);
        			flexBuffer.setIndex(0);
        			flexBuffer.setLength(buffer.length);
        		}
        			
        	}
 	        
        	// Send Message
	        session.sessionPacket(null, flexBuffer);
	        Log.d(TAG,"Received packet length" + flexBuffer.length());
        }
        
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

}
