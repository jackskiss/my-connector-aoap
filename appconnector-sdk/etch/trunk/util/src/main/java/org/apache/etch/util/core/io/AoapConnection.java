package org.apache.etch.util.core.io;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;

import org.apache.etch.util.FlexBuffer;
import org.apache.etch.util.core.Who;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class AoapConnection extends Connection<SessionPacket> implements
		TransportPacket, Runnable {
	
	private static final String TAG = "AoapConnection"; 
	private static String actionUsbPermission;
	private UsbManager usbManager = null;
	private UsbAccessory usbAccessory = null;
	
	private Activity appActivity = null;

    private ParcelFileDescriptor fileDescriptor;
    private FileInputStream inputStream = null;
    private FileOutputStream outputStream = null;	

    private boolean permissionRequestPending;
    
	public AoapConnection(Activity app, UsbManager um) {

		if(app == null || um == null)
		{
			Log.d(TAG, "Error: app or um is null");
			return;
		}
		
		usbManager = um;
		appActivity = app;				
	}

    private boolean openAccessory(UsbAccessory accessory) {
        
    	Log.d(TAG, "openAccessory: " + accessory);
        fileDescriptor = usbManager.openAccessory(accessory);
        
        if (fileDescriptor != null) {
        
        	FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
 
            // Fix: Check if need to thread. 
            // Thread thread = new Thread(null, this, "AoapConnection Thread");
            // thread.start();
            
            Log.d(TAG, "openAccessory succeeded");
            return true;
        } else {
            Log.d(TAG, "openAccessory fail");
            return false;
        }
    }	
	
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	/* USB Permission message */
            if (actionUsbPermission.equals(intent.getAction())) { 
                synchronized (this) {
                    usbAccessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbAccessory != null) {
                            openAccessory(usbAccessory);
                        }
                    } else {
                        Log.d(TAG, "permission denied for accessory " + usbAccessory);
                    }
                    
                    permissionRequestPending = false;
                }
            }
            /* USB Disconnection message */
            else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(intent.getAction()))
            {
            	usbAccessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				/* Precondition: Only one accessory can be connected with this device */
            	if (usbAccessory != null) 
				{
					// Fix: insert close flow
				}           	
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
	            outputStream.write(buf.getBuf());
	        } catch (IOException e) {
	            Log.e(TAG, "write failed", e);
	        }
		}
		
	}

	@Override
	protected boolean openSocket(boolean reconnect) throws Exception {

		if(usbManager != null && appActivity != null && !reconnect )
		{
			/* Check access permission by application */
			actionUsbPermission = appActivity.getPackageName() + ".action.USB_PERMISSION";
			
			Intent startIntent = appActivity.getIntent(); 
			usbAccessory = (UsbAccessory) startIntent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);	        

			if(openAccessory(usbAccessory))
			{
				return true;
			}	
		}
		
		usbAccessory = null;
		return false;
	}

	@Override
	protected void setupSocket() throws Exception {
		
		if(usbManager != null && appActivity != null)
		{
			IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
			appActivity.registerReceiver(usbReceiver, filter);
		}
	}

	@Override
	protected void readSocket() throws Exception {
		// Fix: Read buffer
		
		FlexBuffer flexBuffer = null;
        int ret = 0;
        
        byte[] buffer = new byte[16384];
        flexBuffer = new FlexBuffer( buffer );
        
        while (isStarted())
        {  
	        while (ret >= 0) {
	            try {
	                ret = inputStream.read(buffer);
	            } catch (IOException e) {
	                break;
	            }
	
	            if (ret > 0) {            	
	            	// Send Message
	            	session.sessionPacket(null, flexBuffer);
	            }
	        }
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
