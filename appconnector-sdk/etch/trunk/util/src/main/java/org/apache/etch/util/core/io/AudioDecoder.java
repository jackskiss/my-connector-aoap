package org.apache.etch.util.core.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
	private static File currentFile;
	private static int curAudioFormat;
	
	private MusicThread musicThread;
	
	private static BlockingQueue<ByteBuffer> audioBuffer = null;
	
	private static final int AUDIO_BUFFER_NUM = 100;
	
	private static final String AUDIO_FORMAT_WAV = ".wav";
	private static final String AUDIO_FORMAT_MP3 = ".mp3";
	private static final String AUDIO_FORMAT_OGG = ".ogg";
	private static final String AUDIO_FORMAT_AAC = ".aac";
	private static final String AUDIO_FORMAT_WMA = ".wma";
	
	private static final int AUDIO_ACTION_PLAY 		= 100;
	private static final int AUDIO_ACTION_STOP 		= 101;
	private static final int AUDIO_ACTION_PAUSE 	= 102;
	private static final int AUDIO_ACTION_RESUME 	= 103;
	private static final int AUDIO_ACTION_FORWARD 	= 104;
	private static final int AUDIO_ACTION_REWARD 	= 105;
	
	private static int currentAction = AUDIO_ACTION_STOP;
	
	public AudioDecoder()
	{
		musicThread = new MusicThread();
	}
	
	@SuppressWarnings("deprecation")
	public void playback_media_content(File path)
	{
		Log.d(TAG, "playback_media_content" + path);

		if(path.isFile()) {
			/* If a file is under playback, it will be stopped */
			if(currentFile.equals(path)) 
				stopMediaDecode(path);
			
			currentFile = path;
			// Fix : Need to add control interface API for supporting another controls except PLAY/STOP
			startMediaDecode(path);
			
			if(audioBuffer == null)
				audioBuffer = new ArrayBlockingQueue<ByteBuffer>(AUDIO_BUFFER_NUM);
		} else {
			stopMediaDecode(currentFile);
		}
	}
	
	private boolean checkNormalMedia(File path)
	{
		
		return false; // 
	}
	
	public void startMediaDecode(File path)
	{
		// Start Decoding Thread
		currentAction = AUDIO_ACTION_PLAY;
		musicThread.start();
	}
	
	public void stopMediaDecode(File path)
	{	
		currentAction = AUDIO_ACTION_STOP;
		musicThread.stop();
	}
	
	/*
	 * @return Buffer
	 */
	public static ByteBuffer getDecodedBuffer()
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
		
		private int sampleRate;
		private int channelConfig;
		private int audioFormat;
		private final int HEADER_SIZE = 44;
		
		public MusicThread() {
			//setRunningState(true);
		}
		
		private int readWavHeader(FileInputStream audio) throws IOException {
			ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			 
			audio.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
			 
			buffer.rewind();
			buffer.position(buffer.position() + 20);
			
			int format = buffer.getShort();
			if (format != 1) {
				Log.d (TAG, "Unsupported encoding: " + format);
				return -1;
			}

			int channels = buffer.getShort();
			Log.d(TAG, "channels: " + channels);
			if (channels == 1) {	
				channelConfig = AudioFormat.CHANNEL_OUT_MONO;
			} else if ( channels == 2) {
				channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
			}  else {
				Log.d(TAG, "Unsupported channels: " + channels);
				return -1;
			}
			
			int rate = buffer.getInt();
			Log.d(TAG, "Sampling Rate: " + rate);
			if (rate <= 48000 && rate >= 11025) {
				sampleRate = rate;
			} else {
				Log.d(TAG, "Unsupported rate: " + rate);
				return -1;
			}

			buffer.position(buffer.position() + 6);
			int bits = buffer.getShort();
			Log.d(TAG, "Bits per Sample: " + bits);
			if (bits == 16){
				audioFormat = AudioFormat.ENCODING_PCM_16BIT;
			} else if (bits == 8) {
				audioFormat = AudioFormat.ENCODING_PCM_8BIT;
			} else {
				Log.d(TAG, "Unsupported bits: " + bits);
				return -1;
			}

			int dataSize = 0;
			while (buffer.getInt() != 0x61746164) { // "data" marker
				Log.d(TAG, "Skipping non-data chunk");
				int size = buffer.getInt();
				audio.skip(size);
				   
				buffer.rewind();
				audio.read(buffer.array(), buffer.arrayOffset(), 8);
				buffer.rewind();
			} 
			dataSize = buffer.getInt();
			if (dataSize <= 0) {
				Log.d(TAG, "Wrong data size: " + dataSize);
				return -1;
			}
			     
			return 0;
		}
		
		public void run() {
			
			if(currentFile != null) {
				String audioFile = currentFile.getAbsolutePath();
				long fileSize = currentFile.length();
				int readByte = 0;
				int bufferSize;
				
				/* Ghost file */
				if(fileSize <= 0)
					return;
				
				if(audioFile.endsWith(AUDIO_FORMAT_WAV)) {
					curAudioFormat = Constants.FILE_TYPE_MP3;					
				} else if (audioFile.endsWith(AUDIO_FORMAT_MP3)) {
					curAudioFormat = Constants.FILE_TYPE_MP3;
					
				} else if (audioFile.endsWith(AUDIO_FORMAT_OGG)) {
					curAudioFormat = Constants.FILE_TYPE_OGG;
				} else {
					Log.d(TAG,"Not defined type");
					return;
				}
				
				
				switch(curAudioFormat) {
				case Constants.FILE_TYPE_WAV: 
				{
					try {
						FileInputStream readFile = new FileInputStream(currentFile);
						
						try {
							readWavHeader(readFile);
							bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
							
							do {
								ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
								readByte = readFile.read(buffer.array()); // Fix: Check current Index. 
								audioBuffer.put(buffer);
							} while (readByte > 0);
							readFile.close();
							return;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				}
				case Constants.FILE_TYPE_MP3:
				{
	            	Decoder mp3Decoder = new Decoder(audioFile);
	            	if(mp3Decoder != null) {
		    			bufferSize = mp3Decoder.GetFrameSize();
		    			mp3Decoder.SetBufferSize(bufferSize);
		    			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		    			
		    			do {
		    				try {
								readByte = mp3Decoder.Decode(buffer.array());
			    				try {
									audioBuffer.put(buffer);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
		    			} while(readByte > 0);
	            	}	
					break;
				}
				case Constants.FILE_TYPE_OGG:
				default: 
					Log.d(TAG, "Not support file type");
					return;
				}
			} 
		} /* End run () */
	} /* End MusicThread */
	
}
