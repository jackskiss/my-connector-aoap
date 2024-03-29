// $Id: TestField.cs 743358 2009-02-11 15:18:15Z sccomer $
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
using NUnit.Framework;

namespace Org.Apache.Etch.Bindings.Csharp.Msg
{

    [TestFixture]
    public class TestField
    {
        [TestFixtureSetUp]
        public void First()
        {
            Console.WriteLine();
            Console.Write( "TestField" );
        }

        [Test]
        public void FieldIntegerString()
        {
            TestMf(1, "one");
            TestMf(2, "two");
            TestMf(3, "three");
        }

        [Test]
        public void FieldString()
        {
            TestMf("one");
            TestMf("two");
            TestMf("three");
        }

        private void TestMf(int id, string name)
        {
            Field mf = new Field(id, name);
            Assert.AreEqual(id, mf.Id);
            Assert.AreEqual(name, mf.Name);
        }

        private void TestMf(string name)
        {
            Field mf = new Field(name);
            Assert.AreEqual(IdName.Hash(name), mf.Id);
            Assert.AreEqual(name, mf.Name);
        }
    }
}