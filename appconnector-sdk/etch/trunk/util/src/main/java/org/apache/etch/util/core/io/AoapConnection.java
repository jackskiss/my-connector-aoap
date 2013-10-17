package org.apache.etch.util.core.io;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.etch.util.FlexBuffer;
import org.apache.etch.util.core.Who;

public class AoapConnection extends Connection<SessionPacket> implements
		TransportPacket {

	@Override
	public int headerSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void transportPacket(Who recipient, FlexBuffer buf) throws Exception {
		// TODO Auto-generated method stub

	}

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
