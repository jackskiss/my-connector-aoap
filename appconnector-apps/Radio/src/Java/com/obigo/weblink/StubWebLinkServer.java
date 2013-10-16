// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / java 1.3.0-incubating (LOCAL-0)
//   Wed Sep 25 20:31:58 KST 2013
// This file is automatically created and should not be edited!

package com.obigo.weblink;

import org.apache.etch.util.core.Who;
import org.apache.etch.bindings.java.msg.Message;
import org.apache.etch.bindings.java.support.DeliveryService;
import org.apache.etch.bindings.java.support.Pool;
import org.apache.etch.bindings.java.support.StubHelper;
import org.apache.etch.bindings.java.support._Etch_AuthException;
import org.apache.etch.bindings.java.support.Pool.PoolRunnable;

/**
 * Message to call translator for WebLinkServer.
 */
@SuppressWarnings("unused")
public class StubWebLinkServer extends StubWebLink<WebLinkServer>
{
	/**
	 * Stub for WebLinkServer.
	 * @param svc the delivery service.
	 * @param obj the implementation of WebLinkServer responsive to requests.
	 * @param queued thread pool used to run AsyncMode.QUEUED methods.
	 * @param free thread pool used to run AsyncMode.FREE methods.
	 */
	public StubWebLinkServer( DeliveryService svc, WebLinkServer obj, Pool queued, Pool free )
	{
		super( svc, obj, queued, free );
	}
	
	static
	{
	}

	/**
	 * Method used to force static initialization.
	 */
	public static void init()
	{
		// nothing to do.
	}
	
	static
	{
		ValueFactoryWebLink._mt_com_obigo_weblink_WebLink_remote_control.setStubHelper( new StubHelper<WebLinkServer>()
		{
			public final void run( DeliveryService _svc, WebLinkServer _obj, Who _sender, Message _msg ) throws Exception
			{
				final Message _rmsg = _msg.reply();
				try
				{
					_obj.remote_control(
						(com.obigo.weblink.WebLink.RemoteControl) _msg.get( ValueFactoryWebLink._mf_control )
					);
				}
				catch ( Exception e )
				{
					sessionNotify( _obj, e );
					_rmsg.put( ValueFactoryWebLink._mf_result, e );
				}
				_svc.transportMessage( _sender, _rmsg );
			}
		} );
		ValueFactoryWebLink._mt_com_obigo_weblink_WebLink_radio_station_list.setStubHelper( new StubHelper<WebLinkServer>()
		{
			public final void run( DeliveryService _svc, WebLinkServer _obj, Who _sender, Message _msg ) throws Exception
			{
				final Message _rmsg = _msg.reply();
				try
				{
					_rmsg.put( ValueFactoryWebLink._mf_result,
					_obj.radio_station_list(
					)
					);
				}
				catch ( Exception e )
				{
					sessionNotify( _obj, e );
					_rmsg.put( ValueFactoryWebLink._mf_result, e );
				}
				_svc.transportMessage( _sender, _rmsg );
			}
		} );
		ValueFactoryWebLink._mt_com_obigo_weblink_WebLink_radio_station_select.setStubHelper( new StubHelper<WebLinkServer>()
		{
			public final void run( DeliveryService _svc, WebLinkServer _obj, Who _sender, Message _msg ) throws Exception
			{
				final Message _rmsg = _msg.reply();
				try
				{
					_obj.radio_station_select(
						(Integer) _msg.get( ValueFactoryWebLink._mf_radio_station_id )
					);
				}
				catch ( Exception e )
				{
					sessionNotify( _obj, e );
					_rmsg.put( ValueFactoryWebLink._mf_result, e );
				}
				_svc.transportMessage( _sender, _rmsg );
			}
		} );
	}
}
