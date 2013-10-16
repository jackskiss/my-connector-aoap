package com.obigo.baidumusic.standard;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.etch.bindings.java.support.ObjSession;
import org.apache.etch.util.core.io.Session;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.music.model.Channel;
import com.baidu.music.model.Music;
import com.baidu.music.model.Topic;
import com.hkmc.sdk.Ubi_Interface.CommunicationManager;
import com.obigo.baidumusic.standard.MusicApplication.MusicServiceConnection;
import com.obigo.baidumusic.standard.player.FavoriteList;
import com.obigo.baidumusic.standard.player.ListDownloader;
import com.obigo.baidumusic.standard.player.ListDownloader.ListDownloadListener;
import com.obigo.baidumusic.standard.player.PlayerListener;
import com.obigo.baidumusic.standard.player.PlayerService;
import com.obigo.baidumusic.standard.playlist.FavoriteListActivity;
import com.obigo.baidumusic.standard.playlist.PlayListActivity;
import com.obigo.baidumusic.standard.playlist.RadioListActivity;
import com.obigo.baidumusic.standard.playlist.RadioListActivityWL;
import com.obigo.baidumusic.standard.playlist.ThemeListActivity;
import com.obigo.baidumusic.standard.playlist.TopListActivity;
import com.obigo.baidumusic.standard.search.SearchActivity;
import com.obigo.baidumusic.standard.search.SearchListActivity;
import com.obigo.baidumusic.standard.util.NetworkChecker;
import com.obigo.baidumusic.standard.util.ObiLog;
import com.obigo.baidumusic.standard.util.Preference;
import com.obigo.baidumusic.standard.view.CategoryView;
import com.obigo.baidumusic.standard.view.MenuButton;
import com.obigo.baidumusic.standard.view.ObigoDialog;
import com.obigo.weblink.ImplWebLinkClient;
import com.obigo.weblink.RemoteWebLinkServer;
import com.obigo.weblink.WebLinkClient;
import com.obigo.weblink.WebLinkHelper;
//WL


public class MainActivity extends BaseActivity implements PlayerListener,
        OnClickListener, OnDismissListener, ListDownloadListener,
        MusicServiceConnection,
        com.obigo.baidumusic.standard.view.MenuButton.OnCheckedChangeListener,
        WebLinkHelper.WebLinkClientFactory,
        ObjSession {
    private static String TAG = "MainActivity";

    private TextView mTvTitle, mTvArtist, mBottomMessage;
    private Button mBtnPlay;
    private PlayerService mPlayer;
    private MenuButton mBtnMenu;
    private PopupWindow mMenu;
    private CategoryView mCategory;
    
    private Context mContext;
    private Handler handler;
    
    private int exitTimer = 10; // set timer
    private final int HandlerCycle = 1000;
    
    private RemoteWebLinkServer mServerWL;
    private boolean mServerConnectedWL = false;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        if (Preference.getDebugMode())
            connectSimulator();
        mContext = this;
        
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.mainmenu_favorite).setOnClickListener(this);
        findViewById(R.id.mainmenu_theme).setOnClickListener(this);
        findViewById(R.id.mainmenu_radio).setOnClickListener(this);
        findViewById(R.id.mainmenu_chinese_hit).setOnClickListener(this);
        findViewById(R.id.mainmenu_billboard).setOnClickListener(this);
        findViewById(R.id.mainmenu_new_hit).setOnClickListener(this);
        findViewById(R.id.bottom).setOnClickListener(this);

        try {
            mBtnMenu = findMenuButton(R.id.menu_button);
            mBtnMenu.setOnCheckedChangeListener(this);
        } catch (Exception e) {
            ObiLog.e(TAG, "findMenuButton(R.id.menu_button) : error");
        }
        
        try {
            mBtnPlay = findButton(R.id.icon_play);
            mBtnPlay.setOnClickListener(this);
        } catch (Exception e) {
            ObiLog.e(TAG, "findButton(R.id.icon_play) : error");
        }
        
        try {
            mTvTitle = findTextView(R.id.music_title);
            mTvArtist = findTextView(R.id.music_artist);
        } catch (Exception e) {
            ObiLog.e(TAG, "findTextView() : error");
        }
        
        try {
            mCategory = findCategoryView(R.id.category);
        } catch (Exception e) {
            ObiLog.e(TAG, "findCategoryView() : error");
        }
        
        try {
        	mBottomMessage = findTextView(R.id.bottom_notify);
        } catch (Exception e) {
            ObiLog.e(TAG, "findTextView() : error");
        }
        
        

        View popupView = View.inflate(this, R.layout.popup_main, null);

        (popupView.findViewById(R.id.search)).setOnClickListener(this);
        (popupView.findViewById(R.id.notice)).setOnClickListener(this);
        (popupView.findViewById(R.id.version)).setOnClickListener(this);
        (popupView.findViewById(R.id.exit)).setOnClickListener(this);
        (popupView.findViewById(R.id.connectWL)).setOnClickListener(this);
        (popupView.findViewById(R.id.disconnectWL)).setOnClickListener(this);

        //mMenu = new PopupWindow(popupView, 190, 176, true);
        mMenu = new PopupWindow(popupView, 190, 264, true); //WL for connect/disconnect
        mMenu.setOutsideTouchable(true);
        mMenu.setBackgroundDrawable(new BitmapDrawable());
        mMenu.setOnDismissListener(this);

        MusicApplication.setListener(this);
        
        if(!NetworkChecker.isConnected(this)) {
            ObiLog.e(TAG, "Network is disconnected");
            
            ObigoDialog.builder(this, new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.button_left) {
                        ((MainActivity)mContext).exit();
                    }
                }
            });
            ObigoDialog.setButtons(R.string.btn_yes, R.string.btn_no);
            ObigoDialog.setTitleContent(R.string.warning, R.string.warning_network_connection2);
            ObigoDialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        
        ((MusicApplication)getApplication()).bindMusicService();

        try {
            mPlayer = MusicApplication.getPlayer();
            mPlayer.requestAudioFocus();

            Music music = mPlayer.getCurrentMusic();

            if (music != null) {
                mTvTitle.setText(music.mTitle);
                mTvArtist.setText("- " + music.mArtist);

                mCategory.setVisibility(View.VISIBLE);
                mCategory.setCategory(mPlayer.getListType());
                mBtnPlay.setVisibility(View.VISIBLE);
            } else {
                showBottomMessage(R.string.selectmusic);
            }

            // set play status
            int state = mPlayer.getPlayerStatus();

            switch (state) {
            case PlayerService.STATUS_PLAYING:
            	unshowBottomMessage();
                setPlayButton(true);
                break;
            case PlayerService.STATUS_STOPPED:
            	showBottomMessage(R.string.selectmusic);
            	break;
            default:
                setPlayButton(false);
                break;
            }

            mPlayer.setOnPlayerListener(this);
            
        } catch (Exception e) {
            ObiLog.e(TAG, "onResume ", e);
            mTvTitle.setText("");
            mTvArtist.setText("");

            mCategory.setVisibility(View.INVISIBLE);
            mBtnPlay.setVisibility(View.INVISIBLE);
            
            showBottomMessage(R.string.selectmusic);
        }
        
        // Inactive favorite menu when no favorites in the list
        if (FavoriteList.getInstance(mContext).size() == 0)
        	disableFavorite();
        else
        	enableFavorite();
               
        // Initializing Favorites For first launch
        if (ListDownloader.getInstance(mContext).isDownloadingList() > 0) {
        	showBottomMessage(R.string.greeting);
        	disableAllMenu();
        	handler = new Handler() {
        		public void handleMessage(Message msg) {
        			if ((FavoriteList.getInstance(mContext).size() == 6) || exitTimer()) {
        				if (mPlayer.getPlayerStatus() == 0) {
        					showBottomMessage(R.string.selectmusic);
        				} else {
        					unshowBottomMessage();
        				}
        				enableAllMenu();
        			} else if (FavoriteList.getInstance(mContext).size() != 6) {
        				handler.sendEmptyMessageDelayed(0, HandlerCycle);
        			}
        		}
        	};
        	handler.sendEmptyMessage(0);
        }
    }
    
    private void showBottomMessage(int msg) {
    	mBottomMessage.setText(msg);
		findViewById(R.id.icon_play).setVisibility(View.GONE);
    	findViewById(R.id.bottom).setVisibility(View.GONE);
		mBottomMessage.setVisibility(View.VISIBLE);
    }
    
    private void unshowBottomMessage() {
    	findViewById(R.id.icon_play).setVisibility(View.VISIBLE);
    	findViewById(R.id.bottom).setVisibility(View.VISIBLE);
		mBottomMessage.setVisibility(View.GONE);
    }
    
    private boolean exitTimer() {
    	if (exitTimer > 0) {
    		exitTimer--;
    		return false;
    	}
    	return true; 		
    }
    
    private void enableAllMenu() {
    	findViewById(R.id.mainmenu_chinese_hit).setEnabled(true);
    	findViewById(R.id.mainmenu_theme).setEnabled(true);
    	findViewById(R.id.mainmenu_radio).setEnabled(true);
    	findViewById(R.id.mainmenu_new_hit).setEnabled(true);
    	findViewById(R.id.mainmenu_billboard).setEnabled(true);
    	enableFavorite();
    	
    }
    
    private void disableAllMenu() {
    	findViewById(R.id.mainmenu_chinese_hit).setEnabled(false);
    	findViewById(R.id.mainmenu_theme).setEnabled(false);
    	findViewById(R.id.mainmenu_radio).setEnabled(false);
    	findViewById(R.id.mainmenu_new_hit).setEnabled(false);
    	findViewById(R.id.mainmenu_billboard).setEnabled(false);
    	disableFavorite();   	
    }
    
    private void enableFavorite() {
    	// Exception
    	if (FavoriteList.getInstance(mContext).size() == 0)
    		return;
    	
    	findViewById(R.id.mainmenu_favorite).setEnabled(true);
    	findViewById(R.id.mainmenu_favorite).setBackgroundResource(R.drawable.selector_btn_main_favorite);
    }
    
    private void disableFavorite() {
    	findViewById(R.id.mainmenu_favorite).setEnabled(false);
    	findViewById(R.id.mainmenu_favorite).setBackgroundResource(R.drawable.btn_main_favorite_disable);
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            if (mPlayer != null) {
                mPlayer.setOnPlayerListener(null);
            }
        } catch (Exception e) {
            ObiLog.e(TAG, "onPause() error : ", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void goPlayList() {
        if(!NetworkChecker.isConnected(this)) {
            NetworkChecker.showError(this);
            return;
        }
        
        if (mPlayer == null
                || mPlayer.getPlayList().size() <= 0) {
            return;
        }

        switch (mPlayer.getListType()) {
        case ListDownloader.TYPE_FAVORATE:
            goFavoriteList();
            break;
        case ListDownloader.TYPE_THEME:
            goThemeList();
            break;
        case ListDownloader.TYPE_RADIO:
            goRadioList();
            break;
        case ListDownloader.TYPE_POPULAR:
        case ListDownloader.TYPE_BILLBOARD:
        case ListDownloader.TYPE_NEW:
            goTopList(mPlayer.getListType());
            break;
        case ListDownloader.TYPE_SEARCH:
            goSearchList();
            break;
        default:
            break;
        }
    }

    private void goFavoriteList() {
        if (NetworkChecker.isConnected(this)) {
            Intent intent = new Intent(MainActivity.this,
                    FavoriteListActivity.class);
            intent.putExtra(PlayListActivity.LIST_TYPE,
                    ListDownloader.TYPE_FAVORATE);
            startActivity(intent);
            Preference.putInt(this, Preference.KEY_LAST_CATEGORY,
                    ListDownloader.TYPE_FAVORATE);
        } else {
            NetworkChecker.showError(this);
        }
    }

    private void goThemeList() {
        if (NetworkChecker.isConnected(this)) {
            Intent intent = new Intent(MainActivity.this,
                    ThemeListActivity.class);
            intent.putExtra(PlayListActivity.LIST_TYPE,
                    ListDownloader.TYPE_THEME);
            startActivity(intent);
            Preference.putInt(this, Preference.KEY_LAST_CATEGORY,
                    ListDownloader.TYPE_THEME);
        } else {
            NetworkChecker.showError(this);
        }
    }

    private void goRadioList() {
        if (NetworkChecker.isConnected(this)) {
            Intent intent = new Intent(MainActivity.this,
                    RadioListActivity.class);
            intent.putExtra(PlayListActivity.LIST_TYPE,
                    ListDownloader.TYPE_RADIO);
            startActivity(intent);
            Preference.putInt(this, Preference.KEY_LAST_CATEGORY,
                    ListDownloader.TYPE_RADIO);
        } else {
            NetworkChecker.showError(this);
        }
    }

    private void goRadioListWL() {
    	if (mServerWL != null) {
            Intent intent = new Intent(MainActivity.this,
                    RadioListActivityWL.class);
            intent.putExtra(PlayListActivity.LIST_TYPE,
                    ListDownloader.TYPE_RADIO);
            startActivity(intent);
            Preference.putInt(this, Preference.KEY_LAST_CATEGORY,
                    ListDownloader.TYPE_RADIO);
        } else {
        	Toast.makeText(mContext, "WebLink server is NOT connected", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void goTopList(int type) {
        if (NetworkChecker.isConnected(this)) {
            Intent intent = new Intent(MainActivity.this, TopListActivity.class);
            intent.putExtra(PlayListActivity.LIST_TYPE, type);
            startActivity(intent);
            Preference.putInt(this, Preference.KEY_LAST_CATEGORY, type);
        } else {
            NetworkChecker.showError(this);
        }
    }

    private void goSearchList() {
        if (NetworkChecker.isConnected(this)) {
            Intent intent = new Intent(MainActivity.this,
                    SearchListActivity.class);
            intent.putExtra(PlayListActivity.LIST_TYPE,
                    ListDownloader.TYPE_SEARCH);
            startActivity(intent);
        } else {
            NetworkChecker.showError(this);
        }
    }

    private void buttonDelay(final Button button) {
        button.setEnabled(false);
        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setEnabled(true);
                    }
                });
            }
            
        }, PlayListActivity.BUTTON_DELAY);
    }

    @Override
    public void onPreparePlayer(Music music, int position) {
        mTvTitle.setText(music.mTitle);
        mTvArtist.setText("- " + music.mArtist);

        mCategory.setCategory(mPlayer.getListType());
    }

    @Override
    public void onStartPlayer() {
        unshowBottomMessage();
        setPlayButton(true);

    }

    @Override
    public void onPausePlayer() {
    	unshowBottomMessage();
        setPlayButton(false);
    }

    @Override
    public void onResumePlayer() {
    	unshowBottomMessage();
        setPlayButton(true);
    }

    @Override
    public void onStopPlayer() {
        ListDownloader.getInstance(this).setType(ListDownloader.TYPE_UNKNOWN);
        
        mTvTitle.setText("");
        mTvArtist.setText("");

        mCategory.setVisibility(View.INVISIBLE);
        mBtnPlay.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUpdateProgress(int duration, int position) {
    }

    @Override
    public void onUpdateLoading(int duration, int position) {
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        case R.id.search:
            mMenu.dismiss();

            if (MusicApplication.isRegulation()) {
                ObigoDialog.builder(this, null);
                ObigoDialog.setButton(R.string.btn_ok);
                ObigoDialog.setTitleContent(R.string.warning,
                        R.string.warning_driving_search);
                ObigoDialog.show();
                return;
            }

            if (NetworkChecker.isConnected(this)) {
                Intent intent = new Intent(MainActivity.this,
                        SearchActivity.class);
                startActivity(intent);
            } else {
                NetworkChecker.showError(this);
            }
            break;
        case R.id.back:
            onBackPressed();
            break;
        case R.id.mainmenu_favorite:
            goFavoriteList();
            break;
        case R.id.mainmenu_theme:
            goThemeList();
            break;
        case R.id.mainmenu_radio:
            // WebLink::Radio
        	//goRadioList();
        	goRadioListWL();
            break;
        case R.id.mainmenu_chinese_hit:
            goTopList(ListDownloader.TYPE_POPULAR);
            break;
        case R.id.mainmenu_billboard:
            goTopList(ListDownloader.TYPE_BILLBOARD);
            break;
        case R.id.mainmenu_new_hit:
            goTopList(ListDownloader.TYPE_NEW);
            break;
        case R.id.bottom:
            goPlayList();
            break;
        case R.id.notice:
            mMenu.dismiss();
            ObigoDialog.builder(this, null);
            ObigoDialog.setButton(R.string.btn_ok);
            ObigoDialog.setTitleContent(R.string.notice,
                    R.string.notice_content);
            ObigoDialog.show();
            break;
        case R.id.version:
            mMenu.dismiss();
            ObigoDialog.builder(this, null);
            ObigoDialog.setButton(R.string.btn_ok);
            ObigoDialog.setTitleContent(R.string.information,
                    R.string.version_name);
            ObigoDialog.show();
            break;
        case R.id.exit:
            mMenu.dismiss();

            ObigoDialog.builder(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.button_left) {
                        exit();
                    }
                }
            });
            ObigoDialog.setButtons(R.string.btn_yes, R.string.btn_no);
            ObigoDialog.setTitleContent(R.string.warning, R.string.dialog_exit);
            ObigoDialog.show();
            break;
        case R.id.icon_play:
            buttonDelay(mBtnPlay);
            int state = mPlayer.getPlayerStatus();

            switch (state) {
            case PlayerService.STATUS_PLAYING:
                mPlayer.pause();
                break;
            default:
                mPlayer.resume();
                break;
            }
            break;
        case R.id.connectWL:
            mMenu.dismiss();
            if (mServerWL != null) {
            	Toast.makeText(mContext, "Alread connected", Toast.LENGTH_SHORT).show();
            } else {
            	connectServerWL();
            	Toast.makeText(mContext, "connected. please go to Radio", Toast.LENGTH_SHORT).show();            	
            } 
            break;
        case R.id.disconnectWL:
            mMenu.dismiss();
            disconnectServerWL();
            Toast.makeText(mContext, "disconnected.", Toast.LENGTH_SHORT).show();  
            break;
            
        default:
            break;
        }
    }

    private boolean connectServerWL() {
		// TODO Change to correct URI
		String uri = "tcp://192.168.0.32:4001";
		
		try {
			mServerWL = WebLinkHelper.newServer( uri, null,
				MainActivity.this );

			// Connect to the service
			mServerWL._startAndWaitUp( 10000 );
			
			MusicApplication.setServerWL(mServerWL);				
		} catch (Exception e) {
			mServerWL = null;
			return false;
		}
		
    	return true;
    }
    
    private void disconnectServerWL() {
		try {
			// Disconnect from the service
			if (mServerWL != null) {
				mServerWL._stopAndWaitDown( 4000 );
				
				mServerWL = null;
				MusicApplication.setServerWL(null);								
			}
		} catch (Exception e) {
			
		}				
    }
    
    // WL
	public WebLinkClient newWebLinkClient( RemoteWebLinkServer server )
			throws Exception
	{
		return new ImplWebLinkClient( server );
	}	

	// WL: ObjSession
	public Object _sessionQuery( Object query )
	{
		throw new UnsupportedOperationException( "unknown query: "+query );
	}

	public void _sessionControl( Object control, Object value )
	{
		throw new UnsupportedOperationException( "unknown control: "+control );
	}

	public void _sessionNotify( Object event )
	{
		if (event.equals( Session.UP ) )
		{
			Toast.makeText(mContext, "WebLink server is connected", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (event.equals( Session.DOWN ) )
		{
			Toast.makeText(mContext, "WebLink server is disconnected", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (event instanceof Throwable)
		{
			((Throwable) event).printStackTrace();
			return;
		}
	}
	
    @Override
    public void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            mMenu.showAtLocation(findViewById(R.id.header), Gravity.RIGHT
                    | Gravity.TOP, 0, 108);
        }
    }

    @Override
    public void onDismiss() {
        mBtnMenu.setChecked(false);
    }

    private void setPlayButton(boolean isPlay) {
        if (isPlay) {
            mBtnPlay.setBackgroundResource(R.drawable.selector_btn_stop);
        } else {
            mBtnPlay.setBackgroundResource(R.drawable.selector_btn_play);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDownloadChannelList(int type, List<?> list,
            int selectedChannel) {
        List<Channel> channels = (List<Channel>) list;

        int idx = RadioListActivity.selectDefaultChannel(channels);
        Channel channel = channels.get(idx);
        ListDownloader downloader = ListDownloader.getInstance(this);
        downloader.setChannel(idx);

        downloader.requestRadioList(channel);
    }

    @Override
    public void onDownloadTheme(int type, Topic theme) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDownloadMusicList(int type, boolean isSeamless,
            List<Music> list) {
        try {
            mBtnPlay.setVisibility(View.VISIBLE);
            
            mPlayer = MusicApplication.getPlayer();
            mPlayer.setOnPlayerListener(this);
            mPlayer.setPlayList(type, list);
            mPlayer.play();
        } catch (Exception e) {
            ObiLog.e(TAG, "onDownloadMusicList error : ", e);            
            showBottomMessage(R.string.selectmusic);
        }
    }

    @Override
    public void onError() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onServiceConnected() {
        try {
            mPlayer = MusicApplication.getPlayer();
            mPlayer.requestAudioFocus();

            if (mPlayer.getPlayerStatus() == PlayerService.STATUS_STOPPED) {
                int cat = Preference.getInt(this, Preference.KEY_LAST_CATEGORY);

                if (cat != Integer.MIN_VALUE) {
                    ListDownloader downloader = ListDownloader.getInstance(this);
                    downloader.requestList(cat, this);
                }
            }
        } catch (Exception e) {
            ObiLog.e(TAG, "onDownloadMusicList error : ", e);
        }
    }
    
    // For Simulator
    private String getIpAddress(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(MainActivity.class.getResourceAsStream(path));

        return (String)properties.get("IP_ADDRESS");
    }

    private void connectSimulator() {
        String ipAddress = null;
        try {
        	ipAddress = getIpAddress("/host.properties");
        } catch (IOException e) {
        	e.printStackTrace();
        }

        try {
            Class c = Class.forName("com.hkmc.sdk.CommunicationService");
            ObiLog.d(TAG, ">>>>>> SDK MODE <<<<<<");
            CommunicationManager m = CommunicationManager.getInst();
            m.setHostIP(ipAddress);
            if (m.connectToSimulator()) {
                ObiLog.d(TAG, "Connect to Simulator");
                if (m.sdk_isReady()) {
                    ObiLog.d(TAG, "Simulator is READY!");
                } else {
                    ObiLog.d(TAG, "Failed! Simulator is NOT READY");
                }
            } else {
                ObiLog.d(TAG, "Failed! Connect to Simulator");
            }
        } catch (ClassNotFoundException e) {
            ObiLog.d(TAG, ">>>>> Real Mode <<<<<");
        }
    }
    // End For Simulator

    private void exit() {
    	
    	disconnectServerWL();
    	
        /* case 1 */
//        ActivityManager am =
//        (ActivityManager)getSystemService(ACTIVITY_SERVICE);
//        am.restartPackage(getPackageName());

        /* case 2 */
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
