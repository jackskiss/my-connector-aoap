package com.obigo.radiotest;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.obigo.weblink.*; 

public class MainActivity extends Activity implements WebLinkHelper.WebLinkClientFactory {
	
	private static final String TAG = "WLRadio";
	
	private ArrayList<String> stationList = new ArrayList<String>();
	private ArrayAdapter<String> stationListAdapter;
	
	private RemoteWebLinkServer weblinkServer;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button connectButton = (Button)findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText serverIpEdit = (EditText)findViewById(R.id.serverIpEdit);
				String serverIp = serverIpEdit.getText().toString();
				
				// TODO Change to correct URI
//				String uri = "tcp://" + serverIp;
				String uri = "aoap://dev@" + serverIp;
				
				try {
					weblinkServer = WebLinkHelper.newServer( uri, null,
						MainActivity.this );
	
					// Connect to the service
					weblinkServer._startAndWaitUp( 10000 );
					Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();						
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
					Toast.makeText(MainActivity.this, "failed to connect", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		Button disconnectButton = (Button)findViewById(R.id.disconnectButton);
		disconnectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
					// Disconnect from the service
					weblinkServer._stopAndWaitDown( 4000 );
					Toast.makeText(MainActivity.this, "disconnected", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
					Toast.makeText(MainActivity.this, "failed to disconnect", Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button playButton = (Button)findViewById(R.id.playButton);
		playButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
					weblinkServer.remote_control(WebLink.RemoteControl.PLAY);
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
			}
		});
		
		Button pauseButton = (Button)findViewById(R.id.pauseButton);
		pauseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
					weblinkServer.remote_control(WebLink.RemoteControl.PAUSE);				
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
			}
		});

		Button prevButton = (Button)findViewById(R.id.prevButton);
		prevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
					weblinkServer.remote_control(WebLink.RemoteControl.PREV);
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
			}
		});

		Button nextButton = (Button)findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
					weblinkServer.remote_control(WebLink.RemoteControl.NEXT);					
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
			}
		});

		Button volumeUpButton = (Button)findViewById(R.id.volumeUpButton);
		volumeUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
					weblinkServer.remote_control(WebLink.RemoteControl.VOLUME_UP);
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
			}
		});

		Button volumeDownButton = (Button)findViewById(R.id.volumeDownButton);
		volumeDownButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
					weblinkServer.remote_control(WebLink.RemoteControl.VOLUME_DOWN);
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
			}
		});

		ListView stationListView = (ListView)findViewById(R.id.stationListView);
		stationListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stationList);
		stationListView.setAdapter(stationListAdapter);
		
		Button stationListButton = (Button)findViewById(R.id.stationListButton);
		stationListButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stationList.clear();

				try {
					Integer stationCount = weblinkServer.radio_station_count();
					for (Integer i = 0; i < stationCount; i++) {
						WebLink.RadioStation station = weblinkServer.radio_station_get_at(i);
						stationList.add(station.title);
					}
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
				
				stationListAdapter.notifyDataSetChanged();
			}
		});
		
		Button stationSelectButton = (Button)findViewById(R.id.stationSelectButton);
		stationSelectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				try {
				} catch (Exception e) {
					Log.d(TAG, "WebLink Exception:" + e);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public WebLinkClient newWebLinkClient( RemoteWebLinkServer server )
			throws Exception
		{
			return new ImplWebLinkClient( server );
		}	
}
