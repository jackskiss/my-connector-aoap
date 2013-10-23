package org.apache.etch.util.core.io;

import android.os.Environment;

public class Constants {
	
	// Music control commands
	static final int START_COMMUNICATION = 100;	
	static final int MUSIC_START = 101;
	static final int MUSIC_STOP = 102;
	static final int MUSIC_NEXT = 103;
	static final int MUSIC_PREV = 104;
	static final int STREAMING_START = 105;
	static final int STOP_COMMUNICATION = 106;

	// Music control header
	static String CONTROL_HEADER_ID = "OBIGO";
	static byte CONTROL_FILE_INDEX = (byte)0x01;
	static byte CONTROL_BUF_SIZE = (byte)0x02;
	static byte CONTROL_MUSIC_INFO = (byte)0x03;
	
	static final String MEDIA_PATH = "/ObigoConnectivity";
	static final String SCAN_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + MEDIA_PATH;
	
	static final int FILE_TYPE_NONE = 0;
	static final int FILE_TYPE_WAV = 1;
	static final int FILE_TYPE_MP3 = 2;
	static final int FILE_TYPE_OGG = 3;
	
	// Sampling rate
	static final int SAMPLE_RATE_44100 = 1;
	static final int SAMPLE_RATE_48000 = 2;
	
	// Channel number
	static final int CHANNEL_MONO = 1;
	static final int CHANNEL_STEREO = 2;
	
	// Audio format
	static final int AUDIO_ENCODING_PCM_16 = 1;
	static final int AUDIO_ENCODING_PCM_8 = 2;
	
}
