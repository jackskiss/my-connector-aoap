/* $Id: CompilerVersion.java.tmpl 767991 2009-04-23 17:36:27Z sccomer $
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

package org.apache.etch.bindings.java.compiler;

/**
 * The version info of this Etch backend (compiler).
 */
public interface CompilerVersion
{
	// This file is edited by the production build system to replace the value
	// of VERSION below with whatever it wants the version string to actually be.
	
	/** The version of this Etch backend (compiler) */
	public String VERSION = "java 1.3.0 (LOCAL-0)";
}
