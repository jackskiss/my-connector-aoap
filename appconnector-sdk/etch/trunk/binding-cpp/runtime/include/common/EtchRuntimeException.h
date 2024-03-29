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

#ifndef __ETCHRUNTIMEEXCEPTION_H__
#define __ETCHRUNTIMEEXCEPTION_H__

#include "common/EtchException.h"

class EtchRuntimeException : public EtchException {
public:

  /**
   * EtchObjectType for EtchRuntimeException.
   */
  static const EtchObjectType* TYPE();

  /**
   * Constructs a EtchRuntimeException object.
   */
  EtchRuntimeException();

  /**
   * EtchRuntimeException
   * @param msg Exception Message
   * @param errcode Error Code
   */
  EtchRuntimeException(EtchString msg, status_t errcode);

  /**
   * Constructs a copy of EtchRuntimeException.
   */
  EtchRuntimeException(const EtchRuntimeException& other);

  /**
   * Destructor
   */
  virtual ~EtchRuntimeException();

  //Overridden
  capu::bool_t equals(const EtchObject* other);

};


#endif /* ETCHRUNTIMEEXCEPTION_H */

