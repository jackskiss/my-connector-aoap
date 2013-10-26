package com.jackskiss.aoaphost;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.etch.bindings.java.support.ServerFactory;
import org.apache.etch.util.core.io.Transport;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.obigo.weblink.ImplWebLinkServer;
import com.obigo.weblink.MainWebLinkListener;
import com.obigo.weblink.RemoteWebLinkClient;
import com.obigo.weblink.WebLink;
import com.obigo.weblink.WebLinkHelper;
import com.obigo.weblink.WebLinkServer;

public class AoapHost extends Activity implements WebLinkHelper.WebLinkServerFactory {
	
	private final static String TAG = "AoapHost";
	ServerFactory listener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aoap_host);
		
		
		try {
			// TODO Change to correct URI
			String uri = "aoap://acc@100.100.100.100:1942";
			
			listener = WebLinkHelper.newListener( uri, null,
				AoapHost.this );

			// Start the Listener
			listener.transportControl( Transport.START_AND_WAIT_UP, 4000 );
			
		} catch (Exception e) {
			Log.d(TAG, "WebLink Exception:" + e);
		}

		
/*		Button connectButton = (Button)findViewById(R.id.button1);
		connectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					// TODO Change to correct URI
					String uri = "aoap://acc@100.100.100.100:1942";
					
					ServerFactory listener = WebLinkHelper.newListener( uri, null,
						AoapHost.this );

					// Start the Listener
					listener.transportControl( Transport.START_AND_WAIT_UP, 4000 );
					
					//Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();						
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
					//Toast.makeText(MainActivity.this, "failed to connect", Toast.LENGTH_SHORT).show();
				}
			}
		});
*/		
		Button usbButton = (Button)findViewById(R.id.button2);
		usbButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub			

				Intent intent = getIntent();	
				UsbManager usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
//				UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				UsbDevice device;
				
		 		HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
				if(!deviceList.isEmpty()) {			
					Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
					while(deviceIterator.hasNext()){
						
						device = deviceIterator.next();
					    if(device != null)
						{
					    	Toast.makeText(AoapHost.this, "USB Class:" + device.getDeviceClass() + " USB VID:" + device.getVendorId(), Toast.LENGTH_LONG).show();
					    	return;
						}	
					}				
				}
				
				// Toast.makeText(AoapHost.this, "USB PID:" + device.getProductId() + " USB VID:" + device.getVendorId(), Toast.LENGTH_LONG).show();
			}
		
		});
		
		Button btnPlayback = (Button)findViewById(R.id.playback);
		btnPlayback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				try {
//					weblinkServer.remote_control(WebLink.RemoteControl.PLAY);
//					listener.transportControl(, value);
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.aoap_host, menu);
		return true;
	}

	@Override
	public WebLinkServer newWebLinkServer(RemoteWebLinkClient client)
			throws Exception {
		return new ImplWebLinkServer( client );
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();	
		UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

		if(device != null)
		{
	    	Toast.makeText(AoapHost.this, "USB Class:" + device.getDeviceClass() + " USB VID:" + device.getVendorId(), Toast.LENGTH_LONG).show();
		}	
		
		super.onResume();
	}

}
