package org.apache.etch.util.core.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public final class AudioDecoder {
	
	private static final String TAG = "AudioDecoder";
	private static File currentFilePath;
	
	private MusicThread musicThread;
	
	private static BlockingQueue<ByteArrayInputStream> audioBuffer = null;
	
	private static final int AUDIO_BUFFER_NUM = 100;
	
	public AudioDecoder()
	{
		musicThread = new MusicThread();
	}
	
	public void playback_media_content(File path)
	{
		Log.d(TAG, "playback_media_content" + path);

		if(path.isFile())
		{
			currentFilePath = path;
		}
		
		if(audioBuffer == null)
			audioBuffer = new ArrayBlockingQueue<ByteArrayInputStream>(AUDIO_BUFFER_NUM);
	}
	
	private boolean checkNormalMedia(File path)
	{
		
		return false; // 
	}
	
	public void startMediaDecode(File path)
	{
		// Start Decoding Thread
		musicThread.start();
	}
	
	public void stopMediaDecode(File path)
	{
		// Start Decoding Thread
		musicThread.setRunningState(false);
	}
	
	/*
	 * @return Buffer
	 */
	public static ByteArrayInputStream getDecodedBuffer()
	{
		try {
			return audioBuffer.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	

	// Thread for Local contents
	private class MusicThread extends Thread {
		int fileIndex;
		private boolean isRunning;
		int FileType = Constants.FILE_TYPE_NONE;

		
		public MusicThread() {
			//setRunningState(true);
		}
		
		public void run() {
 /*
			int ret = 0;
    		int  bytesRead = 0;
            int bufferSize = 0; // test buffer size
            byte[] buffer;
            FileInputStream audioStream = null;
            Decoder mp3Decoder =null;
            Decoder oggDecoder =null;
            Log.d(TAG, OBI_TAG +  "MusicThread RUN :: open file " + musicList.get(fileIndex)); 
            //File audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + datapath + filepath[fileIndex]);
            //String audioFile = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.MEDIA_PATH + musicList[fileIndex];
          
            String audioFile = musicList.get(fileIndex);
            
            if (audioFile.endsWith(".wav")) {
            	FileType = Constants.FILE_TYPE_WAV;
            	try {
                    audioStream = new FileInputStream(audioFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }            
                try {
    				ret = UsbAccessoryService.readWavHeader(audioStream);
    				bufferSize = AudioTrack.getMinBufferSize(UsbAccessoryService.sampleRate, UsbAccessoryService.channelConfig, UsbAccessoryService.audioFormat);
            		Log.d(TAG, OBI_TAG +  "bufferSize :" + bufferSize);
            		//sendBufferSize(bufferSize);
    			} catch (IOException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
                
                
            } else if (audioFile.endsWith(".mp3")) {
            	FileType = Constants.FILE_TYPE_MP3;
            	mp3Decoder = new Decoder(audioFile);
    			bufferSize = mp3Decoder.GetFrameSize();
    			mp3Decoder.SetBufferSize(bufferSize);
    			//sendBufferSize(bufferSize);
    			
    			
            } else if (audioFile.endsWith(".ogg"))  {
            	FileType = Constants.FILE_TYPE_OGG;
            	Log.d(TAG, OBI_TAG +  "Currently not supported");
            	return; 
            } else {
            	Log.d(TAG, OBI_TAG +  "Not supported file type") ;
            	return;
            }
			
            getMusicMetadata(audioFile);
            sendMusicInfo();
            
            if (ret == 0) {	// file validation success
            	 Log.d(TAG, OBI_TAG +  "MusicThread RUN :: Send  Loop start");
            	 
				while(isRunning) {
            		
					if (FileType == Constants.FILE_TYPE_WAV) {
			            if(audioStream != null){
			                buffer = new byte[bufferSize];
			                do{
			                    try {
			                    	bytesRead = audioStream.read(buffer);
			                    } catch (IOException e) {
			                        Log.e(TAG, OBI_TAG +  "IOException during audio pcm Read", e);
			                        break;
			                    }
			                    if(bytesRead > 0){
			                    	try {
										mOutputStream.write(buffer, 0, bytesRead);
										//Log.d(TAG, OBI_TAG +  "MusicThread RUN :: Audio write + " + bytesRead);
									} catch (IOException e) {
										Log.e(TAG, OBI_TAG +  "IOException during audio pcm write", e);
										// TODO Auto-generated catch block
										//e.printStackTrace();
									}
			                    	
			                    } else {
			                    	Log.d(TAG, OBI_TAG +  "NO DATA");
			                    }
			                } while (bytesRead > 0 && isRunning);
			            }
			            
			            if( audioStream != null){
			                try {
			                	audioStream.close();
			                } catch (IOException e) {
			                }
			            }          
					} else if (FileType == Constants.FILE_TYPE_MP3) {
						if(mp3Decoder != null){
			                buffer = new byte[bufferSize];
			                do{
			                	try {
			        				bytesRead = mp3Decoder.Decode(buffer);
			        			} catch (IOException e2) {
			        				 Log.e(TAG, OBI_TAG +  "IOException during audio pcm Read", e2);
			                        break;
			        			}

			                    if(bytesRead > 0){
			                    	try {
										mOutputStream.write(buffer, 0, bytesRead);
										//Log.d(TAG, OBI_TAG +  "MusicThread RUN :: Audio write + " + bytesRead);
									} catch (IOException e) {
										Log.e(TAG, OBI_TAG +  "IOException during audio pcm write", e);
									}
			                    	
			                    } else {
			                    	Log.d(TAG, OBI_TAG +  "NO DATA");
			                    }
			                } while (bytesRead > 0 && isRunning);
			                
			                try {
								mp3Decoder.Release();
							} catch (IOException e) {
								e.printStackTrace();
							}
			            }
					} else if (FileType == Constants.FILE_TYPE_OGG) {
						
					}

		            if (isRunning) {
		            	Log.d(TAG, OBI_TAG +  "Going to NEXT");
		            	isRunning = false;
		            	mHandler.obtainMessage(Constants.MUSIC_NEXT).sendToTarget();	// TEST CODE NEXT
		            }
            	}
             }    	
*/
		}
		
		public void setRunningState(boolean state) {
			isRunning = state;
		}
	}
	
}
