/* $Id: ImplIOTClient.cs 779260 2009-05-27 17:56:47Z sccomer $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
 
using System;

using org.apache.etch.interoptester.example.iot.types.IOT;

///<summary>Your custom implementation of BaseIOTClient. Add methods here to provide
///implementation of messages from the server. </summary>
namespace org.apache.etch.interoptester.example.iot
{
	///<summary>Implementation for ImplIOTClient</summary>
	public class ImplIOTClient : BaseIOTClient
	{
		/// <summary>Constructs the ImplIOTClient.</summary>
 		/// <param name="server">a connection to the server session. Use this to
 		/// send a message to the server.</param>
		public ImplIOTClient(RemoteIOTServer server)
		{
			this.server = server;
		}
		
		/// <summary>A connection to the server session. Use this to
 		/// send a message to the server.</summary>
		private readonly RemoteIOTServer server;
	
		// TODO: Implement delegates or provide implementation of IOTClient
		// messages from the server	
	}
}