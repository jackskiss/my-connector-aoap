package com.jackskiss.aoapaccessory;

import java.io.File;

import org.apache.etch.util.core.io.AudioDecoder;

import com.obigo.weblink.ImplWebLinkClient;
import com.obigo.weblink.RemoteWebLinkServer;
import com.obigo.weblink.WebLinkClient;
import com.obigo.weblink.WebLinkHelper;

import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AoapAccessory extends Activity implements WebLinkHelper.WebLinkClientFactory {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aoap_accessory);
		
		// TODO Change to correct URI
		String uri = "aoap://dev@200.200.200.200:1942";
		
		RemoteWebLinkServer server;
		
		try {
			server = WebLinkHelper.newServer( uri, null,
					AoapAccessory.this );
			// Connect to the service
			server._startAndWaitUp( 4000 );
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Button btnCheckAccessory = (Button) findViewById(R.id.button1);
		btnCheckAccessory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startIntent = getIntent();
				UsbAccessory usbAccessory =  (UsbAccessory) startIntent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
				if(usbAccessory != null)
					Toast.makeText(AoapAccessory.this, "UsbAccessory: "+ usbAccessory.getManufacturer(), Toast.LENGTH_LONG).show();
			}
		});
		
		final AudioDecoder decoder = new AudioDecoder();
		
		Button btnAudioFile = (Button)findViewById(R.id.play);
		btnAudioFile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText txt = (EditText)findViewById(R.id.filepath);
				File fPath = new File(txt.getText().toString());
				
				decoder.playback_media_content(fPath);
			}
		});

		// TODO Insert Your Code Here		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.aoap_accessory, menu);
		return true;
	}
	
	public WebLinkClient newWebLinkClient( RemoteWebLinkServer server )
			throws Exception
	{
			return new ImplWebLinkClient( server );
	}
}
