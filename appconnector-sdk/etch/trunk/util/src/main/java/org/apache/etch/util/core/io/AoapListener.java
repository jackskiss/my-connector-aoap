package org.apache.etch.util.core.io;

import java.io.IOException;
import java.net.SocketAddress;

import android.hardware.usb.UsbManager;

public class AoapListener extends Connection<SessionListener<UsbManager>>
		implements Transport<SessionListener<UsbManager>> {

	@Override
	protected boolean openSocket(boolean reconnect) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void setupSocket() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readSocket() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(boolean reset) throws Exception {
		// TODO Auto-generated method stub
		
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
