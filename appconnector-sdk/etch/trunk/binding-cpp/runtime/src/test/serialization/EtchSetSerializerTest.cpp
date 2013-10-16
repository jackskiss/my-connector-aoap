/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "serialization/EtchValueFactory.h"
#include "serialization/EtchSetSerializer.h"
#include "common/EtchNativeArray.h"

class MockValueFactory5 : public virtual EtchValueFactory {
public:

  ~MockValueFactory5() {

  }
  MOCK_METHOD2(getType, status_t(capu::uint32_t id, EtchType*& result));

  MOCK_METHOD2(getType, status_t(EtchString& name, EtchType*& result));

  MOCK_METHOD1(addType, status_t(EtchType* type));

  MOCK_METHOD0(lockDynamicTypes, status_t());

  MOCK_METHOD0(unlockDynamicTypes, status_t());

  MOCK_METHOD2(getMessageId, status_t(EtchMessage* msg, capu::int64_t &result));

  MOCK_METHOD2(setMessageId, status_t(EtchMessage* msg, capu::int64_t msgid));

  MOCK_METHOD0(get_mf__messageId, EtchField());

  MOCK_METHOD2(getInReplyToMessageId, status_t(EtchMessage* msg, capu::int64_t &result));

  MOCK_METHOD2(setInReplyToMessageId, status_t(EtchMessage* msg, capu::int64_t msgid));

  MOCK_METHOD0(get_mf__inReplyTo, EtchField());

  MOCK_METHOD2(importCustomValue, status_t(EtchStructValue* _struct, capu::SmartPointer<EtchObject> & result));

  MOCK_METHOD2(exportCustomValue, status_t(capu::SmartPointer<EtchObject> value, EtchStructValue*& result));

  MOCK_METHOD0(get_mt__exception, EtchType*());

  MOCK_METHOD2(getCustomStructType, status_t(const EtchObjectType *c, EtchType *& type));

  EtchLevel getLevel() {
    return LEVEL_FULL;
  }

  MOCK_METHOD1(setLevel, EtchLevel(EtchLevel level));

};

class EtchSetSerializerTest
  : public ::testing::Test {
protected:
  virtual void SetUp() {
    mRuntime = new EtchRuntime();
    mRuntime->start();
  }

  virtual void TearDown() {
    mRuntime->shutdown();
    delete mRuntime;
    mRuntime = NULL;
  }

  EtchRuntime* mRuntime;
};

TEST_F(EtchSetSerializerTest, initTest) {
  EtchClass2TypeMap* c2type = new EtchClass2TypeMap();
  EtchString typeName("type1");
  EtchType* type = new EtchType(10, typeName);
  EtchType* result;
  EXPECT_TRUE(EtchSetSerializer::Init(mRuntime, type, c2type) == ETCH_OK);
  c2type->get(EtchNativeArray<capu::SmartPointer<EtchObject> >::TYPE(), &result);

  //check the added type to class to type matching
  EXPECT_TRUE(type == result);
  //check the initialized component type
  EXPECT_TRUE(type->getComponentType() == EtchHashSet<capu::SmartPointer<EtchObject> >::TYPE());

  //to check the initialized validator
  capu::SmartPointer<EtchValidator> validator;
  EtchField field;
  typeName = "keys";
  type->getField(typeName, &field);
  type->getValidator(field, validator);
  capu::SmartPointer<EtchValidator> val;
  EtchValidatorObject::Get(mRuntime, 1, val);
  EXPECT_TRUE(validator == val);
  delete type;
  delete c2type;
}

TEST_F(EtchSetSerializerTest, exportTest) {
  EtchClass2TypeMap* c2type = new EtchClass2TypeMap();
  EtchString typeName("type1");
  EtchValueFactory* factory = new MockValueFactory5();
  EtchType* type = new EtchType(10, typeName);
  capu::SmartPointer<EtchObject> object = new EtchHashSet<capu::SmartPointer<EtchObject> > ();
  capu::SmartPointer<EtchObject> object2 = new EtchInt32();
  capu::SmartPointer<EtchObject> object3;
  EtchStructValue* result;
  //initialize the serializer
  EXPECT_TRUE(EtchSetSerializer::Init(mRuntime, type, c2type) == ETCH_OK);
  EtchImportExportHelper* test = type->getImportExportHelper();
  //check with invalid values
  EXPECT_TRUE(test->exportValue(NULL, NULL, result) == ETCH_EINVAL);
  EXPECT_TRUE(test->exportValue(NULL, object, result) == ETCH_EINVAL);
  EXPECT_TRUE(test->exportValue(factory, NULL, result) == ETCH_EINVAL);

  capu::SmartPointer<EtchHashSet<capu::SmartPointer<EtchObject> > > set = capu::smartpointer_cast<EtchHashSet<capu::SmartPointer<EtchObject> > > (object);

  set->put(new EtchInt32(90));
  //export values
  EXPECT_TRUE(test->exportValue(factory, object, result) == ETCH_OK);

  //check it has been correctly serialized or not
  EtchString str("keys");
  EtchField field;

  //get the fields
  EXPECT_TRUE(type->getField(str, &field) == ETCH_OK);
  //use the field to get the serialized value
  EXPECT_TRUE(result->get(field, &object3) == ETCH_OK);
  capu::SmartPointer<EtchNativeArray<capu::SmartPointer<EtchObject> > > array = capu::smartpointer_cast<EtchNativeArray<capu::SmartPointer<EtchObject> > > (object3);
  //check the assigned type
  EXPECT_TRUE(type->getComponentType() == EtchHashSet<capu::SmartPointer<EtchObject> >::TYPE());
  //get the serialized value
  capu::SmartPointer<EtchObject> object4;
  array->get(0, &object4);
  EtchInt32 *serialized = (EtchInt32 *) object4.get();
  //check the serialized value
  EXPECT_TRUE(serialized->get() == 90);

  delete c2type;
  delete factory;
  delete type;
  delete result;
}

TEST_F(EtchSetSerializerTest, importTest) {
  EtchClass2TypeMap* c2type = new EtchClass2TypeMap();
  EtchString typeName("type1");
  EtchValueFactory* factory = new MockValueFactory5();
  EtchType* type = new EtchType(10, typeName);
  capu::SmartPointer<EtchObject> object = new EtchHashSet<capu::SmartPointer<EtchObject> > ();
  capu::SmartPointer<EtchObject> object2;
  EtchStructValue* structValue;
  //initialize the serializer
  EXPECT_TRUE(EtchSetSerializer::Init(mRuntime, type, c2type) == ETCH_OK);
  //get the serializer
  EtchImportExportHelper* test = type->getImportExportHelper();
  //check with invalid values
  capu::SmartPointer<EtchHashSet<capu::SmartPointer<EtchObject> > > set = capu::smartpointer_cast<EtchHashSet<capu::SmartPointer<EtchObject> > > (object);

  set->put(new EtchInt32(90));
  //export values
  EXPECT_TRUE(test->exportValue(factory, object, structValue) == ETCH_OK);

  //UPPER PART IS USED FOR GENERATING EXAMPLE INPUT VALUE FOR IMPORT VALUE
  capu::SmartPointer<EtchObject> result;
  //IMPORT TEST
  EXPECT_TRUE(test->importValue(NULL, result) == ETCH_EINVAL);
  EXPECT_TRUE(test->importValue(structValue, result) == ETCH_OK);

  capu::SmartPointer<EtchHashSet<capu::SmartPointer<EtchObject> > > importedSet = capu::smartpointer_cast<EtchHashSet<capu::SmartPointer<EtchObject> > > (result);
  EtchHashSet<capu::SmartPointer<EtchObject> >::Iterator it = importedSet->begin();
  //get the imported value
  it.next(&object2);
  //check the imported value
  EXPECT_TRUE(((EtchInt32*) object2.get())->get() == 90);
  delete c2type;
  delete factory;
  delete type;
  delete structValue;
}
