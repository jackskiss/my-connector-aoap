// $Id: Todo.cs 743358 2009-02-11 15:18:15Z sccomer $
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
using System;

namespace Org.Apache.Etch.Bindings.Csharp.Util
{
    /// <summary>
    /// A TODO is used to perform a lightweight action
    /// </summary>
    public interface Todo
    {
        ///<summary>
        /// Performs the action
        /// </summary>
        /// <param name="mgr">the todo manager where this todo was queued</param>
        /// Exception:
        ///     throws Exception
        ///     
        void Doit(TodoManager mgr);

        /// <summary>
        /// Reports an exception that occurred while running the todo.
        /// </summary>
        /// <param name="mgr">the todo manager where this todo was queued.</param>
        /// <param name="e">the exception that the todo threw.</param>
        void Exception(TodoManager mgr, Exception e);
    }
}
