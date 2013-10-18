package org.apache.etch.bindings.java.transport;

import org.apache.etch.bindings.java.msg.ValueFactory;
import org.apache.etch.bindings.java.support.ServerFactory;
import org.apache.etch.bindings.java.support.TransportFactory;
import org.apache.etch.util.Resources;
import org.apache.etch.util.URL;
import org.apache.etch.util.core.io.AoapConnection;
import org.apache.etch.util.core.io.AoapListener;
import org.apache.etch.util.core.io.SessionListener;
import org.apache.etch.util.core.io.Transport;
import org.apache.etch.util.core.io.TransportPacket;

import android.app.Activity;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class AoapTransportFactory extends TransportFactory {

	private static final String TAG = "AoapTransportFactory";
	
	private Activity appInstance; /* Application activity instance */ 
	private AoapConnection aoapConnection;
	private AoapListener aoapListener;
	
	private static final String AOAP_DEVICE_MODE = "dev";
	private static final String AOAP_ACCESSORY_MODE = "acc";
	
	
	@Override
	protected TransportMessage newTransport(String uri, Resources resources)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TransportMessage newTransport(String uri, Resources resources,
			Object obj) throws Exception {
		
		TransportPacket transportPacket = null;
		
		if( obj != null ) /* Should transfer the activity instance */
		{
			appInstance = (Activity)obj;
			
			/*
			 * Check if accessory is with URL user name e.g., aoap://dev@ for host aoap://acc@ for accessory
			 */
			URL url = new URL(uri);
			String devType = url.getUser();
			
			if(devType.equals(AOAP_ACCESSORY_MODE))
				aoapConnection = new AoapConnection( ); //Fix : Must distinguish between accessory and device
			else /* default device mode */
				aoapConnection = new AoapConnection( );
			
			TransportMessage transportMessage = new Messagizer ( transportPacket, url, resources );
			transportMessage = addFilters( transportMessage, url, resources );
				
			ValueFactory vf = (ValueFactory) resources.get( Transport.VALUE_FACTORY);
			vf.lockDynamicTypes();
			
			return transportMessage;
	    }

		Log.e(TAG, "Should transfer the activity instance");
		
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
	 * @param obj transfer activity instance
	 * 
	 * @return TransportFactory instance
	 */
	@Override
	protected Transport<ServerFactory> newListener(String uri,
			Resources resources, Object obj) throws Exception {
		
		AoapListener transportListener = new AoapListener (); //Fix: Input parameter 
					
		return new MySessionListener( this, transportListener ); // Fix: Input parameter
	}
	
	private class MySessionListener implements Transport<ServerFactory>, SessionListener<UsbManager>
	{

		public MySessionListener( AoapTransportFactory atf, AoapListener listener )
		{
			this.aoapTransportFactory = atf;
			this.listener = listener;
		}

		private ServerFactory session;
		private AoapTransportFactory aoapTransportFactory;
		private AoapListener listener;

		
		@Override
		public Object sessionQuery(Object query) throws Exception {
			return session.sessionQuery(query);
		}

		@Override
		public void sessionControl(Object control, Object value)
				throws Exception {
			session.sessionControl(control, value);
		}

		@Override
		public void sessionNotify(Object event) throws Exception {
			session.sessionNotify(event);
		}

		@Override
		public void sessionAccepted(UsbManager connection) throws Exception {
			// Fix : Control a connection flag
		}

		@Override
		public ServerFactory getSession() {
			return session;
		}

		@Override
		public void setSession(ServerFactory session) {
			this.session = session;
		}

		@Override
		public Object transportQuery(Object query) throws Exception {
			return listener.transportQuery(query);
		}

		@Override
		public void transportControl(Object control, Object value)
				throws Exception {
			listener.transportControl(control, value);			
		}

		@Override
		public void transportNotify(Object event) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
	}
}
