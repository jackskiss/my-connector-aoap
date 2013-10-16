/* $Id: TransportMessage.java 743147 2009-02-10 22:53:01Z sccomer $
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

package org.apache.etch.bindings.java.transport;

import org.apache.etch.bindings.java.msg.Message;
import org.apache.etch.util.core.Who;
import org.apache.etch.util.core.io.Transport;


/**
 * Interface used to deliver messages to the transport from the session.
 */
public interface TransportMessage extends Transport<SessionMessage>
{
	/**
	 * Delivers the data to the transport.
	 * @param recipient specifies the recipient when there is the possibility
	 * of more than one.
	 * @param msg the Message.
	 * @throws Exception
	 */
	public void transportMessage( Who recipient, Message msg ) throws Exception;
}
