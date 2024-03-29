// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / java 1.3.0-incubating (LOCAL-0)
//   Thu Sep 26 14:54:45 KST 2013
// This file is automatically created for your convenience and will not be
// overwritten once it exists! Please edit this file as necessary to implement
// your service logic.

package com.obigo.weblink;

/**
 * Your custom implementation of BaseWebLinkClient. Add methods here to provide
 * implementations of messages from the server.
 */
public class ImplWebLinkClient extends BaseWebLinkClient
{
	/**
	 * Constructs the ImplWebLinkClient.
	 *
	 * @param server a connection to the server session. Use this to send a
	 * message to the server.
	 */
	public ImplWebLinkClient( RemoteWebLinkServer server )
	{
		this.server = server;
	}
	
	/**
	 * A connection to the server session. Use this to send a
	 * message to the server.
	 */
	@SuppressWarnings( "unused" )
	private final RemoteWebLinkServer server;

	// TODO insert methods here to provide implementations of WebLinkClient
	// messages from the server.
}