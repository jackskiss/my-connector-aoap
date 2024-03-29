// $Id: TestStrStrHashMapSerializer.cs 743358 2009-02-11 15:18:15Z sccomer $
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
using Org.Apache.Etch.Bindings.Csharp.Msg;
using Org.Apache.Etch.Bindings.Csharp.Support;
using NUnit.Framework;

namespace Org.Apache.Etch.Bindings.Csharp.Util
{
    [TestFixture]
    public class TestStrStrHashMapSerializer
    {
        private readonly ValueFactory vf = new DummyValueFactory();

        [TestFixtureSetUp]
        public void First()
        {
            Console.WriteLine();
            Console.Write( "TestStrStrHashMapSerializer" );
        }


        [Test]
        public void TestImport0()
        {
            StrStrHashMap map = new StrStrHashMap();
            TestMap( map );
        }

        [Test]
        public void TestImport1()
        {
            StrStrHashMap map = new StrStrHashMap();
            map.Add( "a", "1" );
            TestMap( map );
        }

        [Test]
        public void TestImport2()
        {
            StrStrHashMap map = new StrStrHashMap();
            map.Add( "a", "1" );
            map.Add( "b", "2" );
            TestMap( map );
        }

        [Test]
        public void TestImport3()
        {
            StrStrHashMap map = new StrStrHashMap();
            map.Add( "a", "1" );
            map.Add( "b", "2" );
            map.Add( "c", "3" );
            TestMap( map );
        }

        // no tests for null keys as string? isn't allowed

        public void TestMap( StrStrHashMap map )
        {
            XType type = new XType( "map" );
		   
            Class2TypeMap class2type = new Class2TypeMap();
		    StrStrHashMapSerializer.Init( type, class2type );
		    ImportExportHelper helper = type.GetImportExportHelper();
    		
		    StructValue sv = helper.ExportValue( vf, map );
    		
		    Assert.AreEqual( sv.GetXType, type );
    		
		    StrStrHashMap map2 = (StrStrHashMap) helper.ImportValue( sv );
    		
            Assert.AreEqual( map, map2 );
        }


    }
}
