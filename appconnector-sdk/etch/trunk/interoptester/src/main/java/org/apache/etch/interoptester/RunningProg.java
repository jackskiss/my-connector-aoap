/* $Id: RunningProg.java 743147 2009-02-10 22:53:01Z sccomer $
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

package org.apache.etch.interoptester;

/**
 * RunningProg models a running program.
 */
public interface RunningProg
{
	/**
	 * Stops the running program.
	 * @return the status of the program. 0 means it exited normally.
	 * @throws InterruptedException 
	 */
	public Integer stop() throws InterruptedException;
	
	/**
	 * Waits for the running program to terminate.
	 * @return the status of the program. 0 means it exited normally.
	 * @throws InterruptedException 
	 */
	public Integer join() throws InterruptedException;
}