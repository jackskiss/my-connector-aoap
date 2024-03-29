// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / java 1.3.0-incubating (LOCAL-0)
//   Thu Sep 26 14:54:45 KST 2013
// This file is automatically created for your convenience and will not be
// overwritten once it exists! Please edit this file as necessary to implement
// your service logic.

package com.obigo.weblink;

/**
 * Main program for WebLinkClient. This program makes a connection to the
 * listener created by MainWebLinkListener.
 */
public class MainWebLinkClient implements WebLinkHelper.WebLinkClientFactory
{
	/**
	 * Main program for WebLinkClient.
	 * 
	 * @param args command line arguments.
	 * @throws Exception
	 */
	public static void main( String[] args ) throws Exception
	{
		// TODO Change to correct URI
		String uri = "tcp://localhost:4001";
		
		RemoteWebLinkServer server = WebLinkHelper.newServer( uri, null,
			new MainWebLinkClient() );

		// Connect to the service
		server._startAndWaitUp( 4000 );

		// TODO Insert Your Code Here

		// Disconnect from the service
		server._stopAndWaitDown( 4000 );
	}

	public WebLinkClient newWebLinkClient( RemoteWebLinkServer server )
		throws Exception
	{
		return new ImplWebLinkClient( server );
	}
}
