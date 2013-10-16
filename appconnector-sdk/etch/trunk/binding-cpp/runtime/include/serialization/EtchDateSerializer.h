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

#ifndef __ETCHDATESERIALIZER_H__
#define __ETCHDATESERIALIZER_H__

#include "common/EtchDate.h"
#include "serialization/EtchImportExportHelper.h"
#include "serialization/EtchStructValue.h"
#include "serialization/EtchField.h"
#include "serialization/EtchClass2TypeMap.h"
#include "serialization/EtchValidatorLong.h"

class EtchRuntime;

class EtchDateSerializer : public EtchImportExportHelper {
public:

  /**
   * Constructor
   */
  EtchDateSerializer(EtchRuntime* runtime, EtchType* type, EtchField* field);

  /**
   * Destructor
   */
  virtual ~EtchDateSerializer();

  /**
   * @see EtchImportExportHelper
   */
  virtual status_t exportValue(EtchValueFactory* vf, capu::SmartPointer<EtchObject> value, EtchStructValue*& result);

  /**
   * @see EtchImportExportHelper
   */
  virtual status_t importValue(EtchStructValue* _struct, capu::SmartPointer<EtchObject> &result);


  /**
   * Defines custom fields in the value factory so that the importer can find them.
   * @param type
   * @param collection
   */
  static status_t Init(EtchRuntime* runtime, EtchType* type, EtchClass2TypeMap* class2type);

private:
  EtchField mField;
  EtchType* mType;
  const static EtchString& FIELD_NAME();
};

#endif /* ETCHDATESERIALIZER_H */

