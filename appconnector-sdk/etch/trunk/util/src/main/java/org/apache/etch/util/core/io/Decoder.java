/*
 * Copyright (C) 2013 Obigo Connectivity Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.etch.util.core.io;// MOD

import java.io.IOException;

public class Decoder {
	private static final String TAG="DECODER";
	private long mFile = 0;	 // File Handle
	
	static {
		System.loadLibrary("ObigoDecoder");
	}
	
	public Decoder(String fileName) {
		mFile = OpenFile(fileName);
	}

	public long GetId() {
		return mFile;
	}

	public int Decode(byte[] buffer) throws IOException {
		int len = ReadSamples(mFile, buffer);
		// fos.write(buffer, 0, len);
		return len;
	}

	public int GetSampleRateInHz() {
		return GetRate(mFile);
	}

	public int GetNumberOfChannels() {
		return GetNumChannels(mFile);
	}

	public void Release() throws IOException {
		CloseFile(mFile);
		// fos.close();JNIExampleInterface
	}

	public void seekTo(int msec) {
		seekTo(mFile, msec);
	}

	public boolean SetBufferSize(int size) {
		return SetBufferSize(mFile, size);
	}

	public int GetFrameSize() {
		return GetBufferSize(mFile);
	}

	public static native int CheckFile(String file);

	private native long OpenFile(String file);

	private native int ReadSamples(long file, byte[] buffer);

	private native int GetRate(long file);

	private native int GetNumChannels(long file);

	private native void CloseFile(long file);

	private native void seekTo(long file, int msec);

	private native boolean SetBufferSize(long file, int size);
	
	private native int GetBufferSize(long file);
}
