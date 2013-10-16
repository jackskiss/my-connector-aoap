#!/bin/bash

# patch generation for etch 1.3.0

git diff ../external-sources/apr/apr/include/apr_general.h > patches/apr_general.patch
git diff ../trunk/binding-c/runtime/c/CMakeLists.txt > patches/binding_c_CMakeLists_txt.patch
git diff ../trunk/binding-c/runtime/c/src/main/CMakeLists.txt > patches/binding_c_main_CMakeLists_txt.patch
git diff ../trunk/binding-c/runtime/c/src/main/common/etch_encoding.c > patches/etch_encoding_c.patch
git diff ../trunk/binding-c/runtime/c/src/test/CMakeLists.txt > patches/binding_c_test_CMakeLists_txt.patch
git diff ../trunk/binding-c/runtime/c/src/test/common/test_hashtable.c > patches/test_hashtable_c.patch
git diff ../trunk/examples/example_mixin/CMakeLists.txt > patches/example_mixin_CMakeLists_txt.patch
git diff ../trunk/examples/helloworld/c/CMakeLists.txt > patches/helloworld_c_CMakeLists_txt.patch