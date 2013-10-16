// This file automatically generated by:
//   Apache Etch 1.2.0-incubating (LOCAL-0) / java 1.2.0-incubating (LOCAL-0)
//   Mon Mar 28 11:38:59 CEST 2011
// This file is automatically created for your convenience and will not be
// overwritten once it exists! Please edit this file as necessary to implement
// your service logic.

package org.apache.etch.examples.mixin;

/**
 * Your custom implementation of BaseExampleServer. Add methods here to provide
 * implementations of messages from the client.
 */
public class ImplExampleServer extends BaseExampleServer
{
	/**
	 * Constructs the ImplExampleServer.
	 *
	 * @param client a connection to the client session. Use this to send a
	 * message to the client.
	 */
	public ImplExampleServer( RemoteExampleClient client )
	{
		this.client = client;
	}
	
	/**
	 * A connection to the client session. Use this to send a
	 * message to the client.
	 */
	@SuppressWarnings( "unused" )
	private final RemoteExampleClient client;

	@Override
	public String say_hello(String who){
		return "Server: " + who;
	}

	@Override
	public String say_hello_mixin(String who){
		return "Server-Mixin: " + who;
	}

}