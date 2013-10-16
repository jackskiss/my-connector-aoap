#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements. See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to you under the Apache License, Version
# 2.0 (the "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
cmake_minimum_required (VERSION 2.8)
project (etch-c-weblink)

#if ETCH_HOME has not been set by Ant it is set to the OS environment variable
IF (NOT ETCH_HOME)
    SET(ETCH_HOME $ENV{ETCH_HOME})
ENDIF (NOT ETCH_HOME)

message(STATUS "using etch home ${ETCH_HOME}")

#Etch Home
IF (NOT ETCH_HOME)
    MESSAGE(FATAL_ERROR "ETCH_HOME not set")
ENDIF (NOT ETCH_HOME)

# Set definitions
IF (UNIX)
    add_definitions(-D_GNU_SOURCE -D_REENTRANT -DLINUX=2 -D__LINUX__)
ENDIF (UNIX)
add_definitions(-D_UNICODE -DUNICODE)

# ETCH library
SET(ETCH ${ETCH_HOME})
message(STATUS ${ETCH})
FIND_PATH(ETCH_INCLUDE_DIR etch.h ${ETCH}/include)
FIND_LIBRARY(ETCH_LIBRARY NAMES etch.lib libetch.a PATHS ${ETCH}/lib NO_DEFAULT_PATH)
message(STATUS ${ETCH_INCLUDE_DIR})
message(STATUS ${ETCH_LIBRARY})
IF (ETCH_INCLUDE_DIR AND ETCH_LIBRARY)
    SET(ETCH_FOUND TRUE)
ENDIF (ETCH_INCLUDE_DIR AND ETCH_LIBRARY)
IF (NOT ETCH_FOUND)
    MESSAGE(FATAL_ERROR "Could not find libetch")
ENDIF (NOT ETCH_FOUND)

# APR library
SET(APR ${ETCH_HOME}/extern/apr)
FIND_PATH(APR_INCLUDE_DIR apr.h ${APR}/include ${APR}/include/apr-1)
FIND_LIBRARY(APR_LIBRARY NAMES libapr-1.lib libapr-1.so libapr-1.dylib PATHS ${APR}/lib NO_DEFAULT_PATH)
IF (APR_INCLUDE_DIR AND APR_LIBRARY)
    SET(APR_FOUND TRUE)
ENDIF (APR_INCLUDE_DIR AND APR_LIBRARY)
IF (NOT APR_FOUND)
    MESSAGE(FATAL_ERROR "Could not find libapr")
ENDIF (NOT APR_FOUND)

# APR-ICONV library
SET(APR ${ETCH_HOME}/extern/apr)
FIND_PATH(APR-ICONV_INCLUDE_DIR apr_iconv.h ${APR}/include ${APR}/include/apr-1)
FIND_LIBRARY(APR-ICONV_LIBRARY NAMES libapriconv-1.lib libapriconv-1.so libapriconv-1.dylib PATHS ${APR}/lib NO_DEFAULT_PATH)
IF (APR-ICONV_INCLUDE_DIR AND APR-ICONV_LIBRARY)
    SET(APR-ICONV_FOUND TRUE)
ENDIF (APR-ICONV_INCLUDE_DIR AND APR-ICONV_LIBRARY)
IF (NOT APR-ICONV_FOUND)
    MESSAGE(FATAL_ERROR "Could not find libapriconv")
ENDIF (NOT APR-ICONV_FOUND)

#MESSAGE(STATUS ${PROJECT_SOURCE_DIR}/etch/generated-sources)

# set include dirs
include_directories (${APR_INCLUDE_DIR})
include_directories (${APR-ICONV_INCLUDE_DIR})
include_directories (${ETCH_INCLUDE_DIR})
include_directories (${PROJECT_SOURCE_DIR}/src)
include_directories (${PROJECT_SOURCE_DIR}/../../../WebLink/weblink-sources/c)

add_executable(etch-c-weblink-server
  src/weblink_server_implx.c
  src/weblink_server_impl.c
  src/weblink_listener_main.c
  ../../../WebLink/weblink-sources/c/weblink_interface.c
  ../../../WebLink/weblink-sources/c/weblink_client.c
  ../../../WebLink/weblink-sources/c/weblink_server_stub.c
  ../../../WebLink/weblink-sources/c/weblink_helper.c
  ../../../WebLink/weblink-sources/c/weblink_client_stub.c
  ../../../WebLink/weblink-sources/c/weblink_remote_client.c
  ../../../WebLink/weblink-sources/c/weblink_remote_server.c
  ../../../WebLink/weblink-sources/c/weblink_server.c
  ../../../WebLink/weblink-sources/c/weblink_valufact.c
  ../../../WebLink/weblink-sources/c/weblink_remote.c
)

add_executable(etch-c-weblink-client
  src/weblink_client_impl.c
  src/weblink_client_implx.c
  src/weblink_client_main.c
  ../../../WebLink/weblink-sources/c/weblink_interface.c
  ../../../WebLink/weblink-sources/c/weblink_client.c
  ../../../WebLink/weblink-sources/c/weblink_server_stub.c
  ../../../WebLink/weblink-sources/c/weblink_helper.c
  ../../../WebLink/weblink-sources/c/weblink_client_stub.c
  ../../../WebLink/weblink-sources/c/weblink_remote_client.c
  ../../../WebLink/weblink-sources/c/weblink_remote_server.c
  ../../../WebLink/weblink-sources/c/weblink_server.c
  ../../../WebLink/weblink-sources/c/weblink_valufact.c
  ../../../WebLink/weblink-sources/c/weblink_remote.c
)

target_link_libraries(etch-c-weblink-server "${ETCH_LIBRARY}")
target_link_libraries(etch-c-weblink-server "${APR_LIBRARY}")
target_link_libraries(etch-c-weblink-server "${APR-ICONV_LIBRARY}")
IF (UNIX)
set_target_properties(etch-c-weblink-server PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
ENDIF (UNIX)
set_target_properties(etch-c-weblink-server PROPERTIES OUTPUT_NAME "weblink-server")

target_link_libraries(etch-c-weblink-client "${ETCH_LIBRARY}")
target_link_libraries(etch-c-weblink-client "${APR_LIBRARY}")
target_link_libraries(etch-c-weblink-client "${APR-ICONV_LIBRARY}")
IF (UNIX)
set_target_properties(etch-c-weblink-client PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
ENDIF (UNIX)
set_target_properties(etch-c-weblink-client PROPERTIES OUTPUT_NAME "weblink-client")
