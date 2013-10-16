// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / java 1.3.0-incubating (LOCAL-0)
//   Thu Sep 26 14:54:45 KST 2013
// This file is automatically created and should not be edited!

package com.obigo.weblink;

import org.apache.etch.bindings.java.support.DeliveryService;
import org.apache.etch.bindings.java.support.Pool;
import org.apache.etch.bindings.java.support.StubBase;

/**
 * Message to call translator for WebLink.
 * @param <T> WebLink or a subclass thereof.
 */
@SuppressWarnings("unused")
public class StubWebLink<T extends WebLink> extends StubBase<T>
{
	/**
	 * Stub for WebLink.
	 * @param svc the delivery service.
	 * @param obj the implementation of WebLink responsive to requests.
	 * @param queued thread pool used to run AsyncMode.QUEUED methods.
	 * @param free thread pool used to run AsyncMode.FREE methods.
	 */
	public StubWebLink( DeliveryService svc, T obj, Pool queued, Pool free )
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
	}
}
