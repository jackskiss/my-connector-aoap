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

# set include dirs
include_directories (${APR_INCLUDE_DIR})
include_directories (${APR-ICONV_INCLUDE_DIR})
include_directories (${CUNIT_INCLUDE_DIR})
include_directories (${PROJECT_SOURCE_DIR}/src/extern/jenkhash)
include_directories (${PROJECT_SOURCE_DIR}/include)

add_library (etch-c STATIC
    apr/etch_threadpool_apr.c
    common/etch_arraylist.c
    common/etch_cache.c
    common/etch_collection.c
    common/etch_config.c
    common/etch_encoding.c
    common/etch_exception.c
    common/etch_flexbuffer.c
    common/etch_general.c
    common/etch_hash.c
    common/etch_hashfunc.c
    common/etch_linked_list.c
    common/etch_log.c
    common/etch_map.c
    common/etch_mem.c
    common/etch_mutex.c
    common/etch_nativearray.c
    common/etch_object.c
    common/etch_object_types.c
    common/etch_runtime.c
    common/etch_thread.c
    common/etch_thread2.c
    common/etch_threadpool.c
    common/etch_runtime.c
    common/etch_wait.c
    bindings/msg/etch_arrayval.c
    bindings/msg/etch_value_factory.c
    bindings/msg/etch_default_value_factory.c
    bindings/msg/etch_field.c
    bindings/msg/etch_id_name.c
    bindings/msg/etch_message.c
    bindings/msg/etch_structval.c
    bindings/msg/etch_tagdata.c
    bindings/msg/etch_type.c
    bindings/msg/etch_validator.c
    bindings/msg/etch_binary_tdi.c
    bindings/msg/etch_binary_tdo.c
    bindings/msg/etch_tagdata_inp.c
    bindings/msg/etch_tagdata_out.c
    bindings/support/etch_resources.c
    transport/etch_tdformat.c
    transport/etch_tcp_server.c
    transport/etch_mailbox_manager.c
    transport/etch_connection_event.c
    transport/etch_connection.c
    transport/etch_transport.c
    transport/etch_transport_data.c
    transport/etch_transport_message.c
    transport/etch_transport_packet.c
    support/etch_transportint.c
    transport/etch_url.c
    transport/etch_queue.c
    transport/etch_messagizer.c
    transport/etch_packetizer.c
    transport/etch_plain_mailbox.c
    transport/etch_plain_mailbox_manager.c
    transport/etch_session_data.c
    transport/etch_session_listener.c
    transport/etch_session_message.c
    transport/etch_session_packet.c
    transport/etch_tcp_connection.c
    support/etch_sessionint.c
    support/etch_mailboxint.c
    support/etch_queue_apr.c
    support/etch_remote.c
    support/etch_serializer.c
    support/etch_stub.c
    support/etch_simpletimer.c
    support/etch_sourceint.c
    internal/etchinternal_single_linked_list.c
    ${PROJECT_SOURCE_DIR}/src/extern/jenkhash/jenkrecy.c
    ${PROJECT_SOURCE_DIR}/src/extern/jenkhash/jenklook.c
    ${PROJECT_SOURCE_DIR}/src/extern/jenkhash/jenkhtab.c
)

target_link_libraries(etch-c "${APR_LIBRARY}")
target_link_libraries(etch-c "${APR-ICONV_LIBRARY}")
target_link_libraries(etch-c "${CUNIT_LIBRARY}")

IF (UNIX)
set_target_properties(etch-c PROPERTIES COMPILE_FLAGS "-m32" LINK_FLAGS "-m32")
ENDIF (UNIX)
set_target_properties(etch-c PROPERTIES OUTPUT_NAME "etch")

# OBIGO(PATCH): Installation for iOS
IF (IOS)
    install(DIRECTORY ${PROJECT_SOURCE_DIR}/include/ DESTINATION include)
    install(DIRECTORY ${ETCH_EXTERNAL_DEPENDS}/apr/1.4.8/include/ DESTINATION extern/apr/include)
    install(DIRECTORY ${ETCH_EXTERNAL_DEPENDS}/apr/1.4.8/lib/ DESTINATION extern/apr/lib FILES_MATCHING PATTERN "lib*.a" PATTERN "iconv" EXCLUDE PATTERN "pkgconfig" EXCLUDE)
    # CMake 2.8.11.2 does not support TARGETS installation for iOS yet.
    #   see: http://www.cmake.org/Bug/print_bug_page.php?bug_id=12506
    #   install(TARGETS etch-c DESTINATION .)
    install (DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/\$ENV{CONFIGURATION}\$ENV{EFFECTIVE_PLATFORM_NAME}/ DESTINATION lib COMPONENT etch-c FILES_MATCHING PATTERN "*.a")
ENDIF (IOS)
