// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / java 1.3.0-incubating (LOCAL-0)
//   Thu Sep 26 14:54:45 KST 2013
// This file is automatically created and should not be edited!

package com.obigo.weblink;


/**
 * Call to message translator for WebLink.
 */
@SuppressWarnings("unused")
public class RemoteWebLink extends org.apache.etch.bindings.java.support.RemoteBase implements WebLink
{
	/**
	 * Constructs the RemoteWebLink.
	 *
	 * @param svc
	 * @param vf
	 */
	public RemoteWebLink( org.apache.etch.bindings.java.support.DeliveryService svc, org.apache.etch.bindings.java.msg.ValueFactory vf )
	{
		super( svc, vf );
	}

	/**
	 * {@link _Async} class instance used to hide asynchronous message
	 * implementation. Use this to invoke the asynchronous message
	 * implementations.
	 */
	public final _Async _async = new _Async();

	/**
	 * {@link _Async} class instance used to hide asynchronous message
	 * implementation. This is here for backwards compatibility only, use
	 * {@link #_async} instead.
	 * @deprecated
	 */
	@Deprecated
	public final _Async _inner = _async;

	/**
	 * Asynchronous implementation of service methods.
	 */
	public class _Async
	{

		// Mixin Methods
	}
}
