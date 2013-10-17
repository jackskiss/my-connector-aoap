package org.apache.etch.bindings.java.transport;

import org.apache.etch.bindings.java.support.ServerFactory;
import org.apache.etch.bindings.java.support.TransportFactory;
import org.apache.etch.util.Resources;
import org.apache.etch.util.core.io.Transport;

public class AoapTransportFactory extends TransportFactory {

	@Override
	protected TransportMessage newTransport(String uri, Resources resources)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Transport<ServerFactory> newListener(String uri,
			Resources resources) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.etch.bindings.java.support.TransportFactory#newListener(java.lang.String, org.apache.etch.util.Resources, java.lang.Object)
	 * @param uri transfer URI string parameter
	 * @param resource relative resources
	 * @param obj transfer acitivity instance
	 * 
	 * @return transportfactory instance
	 */
	@Override
	protected Transport<ServerFactory> newListener(String uri,
			Resources resources, Object obj) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
