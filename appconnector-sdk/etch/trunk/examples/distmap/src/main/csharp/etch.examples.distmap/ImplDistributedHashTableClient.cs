// $Id: ImplDistributedHashTableClient.cs 743358 2009-02-11 15:18:15Z sccomer $
// 
// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements. See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership. The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License. You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied. See the License for the
// specific language governing permissions and limitations
// under the License.
// 
namespace org.apache.etch.examples.distmap
{
	///<summary>Implementation for ImplDistributedHashTableClient</summary>
	public class ImplDistributedHashTableClient : BaseDistributedHashTableClient
	{
		/// <summary>Constructs the ImplDistributedHashTableClient.</summary>
 		/// <param name="server">a connection to the server session. Use this to
 		/// send a message to the server.</param>
		public ImplDistributedHashTableClient(RemoteDistributedHashTableServer server)
		{
			this.server = server;
		}
		
		/// <summary>A connection to the server session. Use this to
 		/// send a message to the server.</summary>
		private readonly RemoteDistributedHashTableServer server;
	
		// TODO: Implement delegates or provide implementation of DistributedHashTableClient
		// messages from the server	
	}
}