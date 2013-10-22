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
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class AoapTransportFactory extends TransportFactory {

	private static final String TAG = "AoapTransportFactory";
	
	private Activity appInstance = null; /* Application activity instance */ 
	private UsbManager usbManager = null;
	private AoapConnection aoapConnection;
	private AoapListener aoapListener;
	
	private final static String AOAP_LISTENER = "AoapTransportFactory.aoapListener";
	
	private final static String AOAP_DEVICE_MODE = "dev";
	private final static String AOAP_ACCESSORY_MODE = "acc";
	
	
	@Override
	protected TransportMessage newTransport(String uri, Resources resources)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TransportMessage newTransport(String uri, Resources resources,
			Object obj) throws Exception {
		
		AoapListener aoapListener = (AoapListener) resources.get( AOAP_LISTENER);
		
		TransportPacket transportPacket = null;
		
		if( obj != null ) /* Should transfer the activity instance */
		{
			appInstance = (Activity)obj;
			
			
			usbManager = (UsbManager)appInstance.getSystemService(Context.USB_SERVICE);
			/*
			 * Check if accessory is with URL user name e.g., aoap://dev@ for host aoap://acc@ for accessory
			 */
			URL url = new URL(uri);
			String devType = url.getUser(); // Check device Type
			
//			if(devType.equals(AOAP_ACCESSORY_MODE))
//				
//				aoapConnection = new AoapConnection( appInstance, usbManager ); //Fix : Must distinguish between accessory and device
//			else /* default device mode */
//				aoapConnection = new AoapConnection( appInstance, usbManager );

			if ( aoapListener != null ) /* Host type */
				transportPacket = new AoapConnection( appInstance, usbManager, aoapListener ); 
			else /* Accessory Type */
				transportPacket = new AoapConnection( appInstance, usbManager );

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
		
		AoapListener transportListener = new AoapListener ( appInstance, usbManager ); //Fix: Input parameter 
					
		return new MySessionListener( this, transportListener, uri, resources ); // Fix: Input parameter
	}
	
	private class MySessionListener implements Transport<ServerFactory>, SessionListener<UsbManager>
	{

		public MySessionListener( AoapTransportFactory atf, AoapListener listener, String uri, Resources resources )
		{
			this.aoapTransportFactory = atf;
			this.listener = listener;
			this.resources = resources;
			this.uri = uri;
		}

		private ServerFactory session;
		private AoapTransportFactory aoapTransportFactory;
		private AoapListener listener;
		private String uri;
		private Resources resources;

		
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
			ValueFactory vf = session.newValueFactory( uri );
			Resources r = new Resources ( resources );
			
			r.put( AOAP_LISTENER, listener);
			r.put( Transport.VALUE_FACTORY,  vf);
			
			TransportMessage t = aoapTransportFactory.newTransport(uri, r);
			
			session.newServer(t, uri, r);
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
