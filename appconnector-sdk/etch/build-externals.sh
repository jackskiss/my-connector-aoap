#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one   *
# or more contributor license agreements.  See the NOTICE file *
# distributed with this work for additional information        *
# regarding copyright ownership.  The ASF licenses this file   *
# to you under the Apache License, Version 2.0 (the            *
# "License"); you may not use this file except in compliance   *
# with the License.  You may obtain a copy of the License at   *
#                                                              *
#   http://www.apache.org/licenses/LICENSE-2.0                 *
#                                                              *
# Unless required by applicable law or agreed to in writing,   *
# software distributed under the License is distributed on an  *
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
# KIND, either express or implied.  See the License for the    *
# specific language governing permissions and limitations      *
# under the License.                                           *
#                                                              *
#--------------------------------------------------------------*
#
# original: apr-install.sh
# - 2013/08/12 modified by WebLink Authors
# - 2013/08/21 added build for iOS

# assumed source tree
#   ../appconnector-sdk
#       /etch
#           /external-sources
#           /externals
#           /toolchains        
#           /trunk

cd external-sources

HOST_OS=linux
HOST_ARCH=i386
CFLAGS="-m32"
LDFLAGS="-m32"
EXTRA_CONFIGURE_FLAGS=
BUILD_DIR="build.unknown"

print_usage(){
    echo "Usage:"
    echo "    ./build-externals.sh ios [arch] [sdk]"
    echo "    arch:"
    echo "        i386      build for iPhone Simulator"
    echo "        armv7     build for iPhone 4, 4S"
    echo "        armv7s    build for iPhone 5"
    echo "    sdk:"
    echo "        7.0       use iPhone SDK v.7.0"
}

setup_ios_env(){
    # Transfer arguments to more meaningful names
    IOS_ARCH=$1
    IOS_SDK_VERSION=$2

    echo $IOS_ARCH
    echo $IOS_SDK_VERSION

     
    # IOS_PLATFORM and IOS_HOST only depend on IOS_ARCH
    case $IOS_ARCH in
      i386 )
        IOS_PLATFORM=iPhoneSimulator
        IOS_HOST=i386-apple-darwin12.5.0
        ;;
      armv7 )
        IOS_PLATFORM=iPhoneOS 
        IOS_HOST=arm-apple-darwin12.5.0
        ;;
      armv7s )
        IOS_PLATFORM=iPhoneOS 
        IOS_HOST=arm-apple-darwin12.5.0
        ;;
      * )
        echo "Unrecognised architecture $IOS_ARCH"
        print_usage
        exit 1
        ;;
    esac

    case $IOS_SDK_VERSION in
      7.0 )
        ;;
      * )
        echo "Unrecognised SDK version $IOS_SDK_VERSION"
        print_usage
        exit 1
        ;;
    esac
             
    # Derive the environment variables
    IOS_XCODE_ROOT=`xcode-select -print-path`
    IOS_PLATFORM_PATH=$IOS_XCODE_ROOT/Platforms/$IOS_PLATFORM.platform/Developer
    IOS_SDK_PATH=$IOS_PLATFORM_PATH/SDKs/$IOS_PLATFORM$IOS_SDK_VERSION.sdk
    IOS_FLAGS="-isysroot $IOS_SDK_PATH -arch $IOS_ARCH"
    IOS_PLATFORM_BIN_PATH=$IOS_PLATFORM_PATH/usr/bin

    HOST_ARCH=$IOS_ARCH

    CFLAGS="$CFLAGS $IOS_FLAGS"
    LDFLAGS="$LDFLAGS $IOS_FLAGS"
    
    if [ $IOS_PLATFORM = "iPhoneOS" ]; then
        #CROSS_COMPILING_FLAGS="ac_cv_file__dev_zero=no ac_cv_func_setpgrp_void=yes apr_cv_tcp_nodelay_with_cork=no apr_cv_process_shared_works=no ac_cv_sizeof_struct_iovec=1 apr_cv_mutex_recursive=yes"
        CROSS_COMPILING_FLAGS="ac_cv_file__dev_zero=yes ac_cv_func_setpgrp_void=yes apr_cv_tcp_nodelay_with_cork=yes apr_cv_process_shared_works=yes apr_cv_mutex_robust_shared=no ac_cv_sizeof_struct_iovec=8 apr_cv_mutex_recursive=yes"
    fi
    EXTRA_CONFIGURE_FLAGS="--host=$IOS_HOST $CROSS_COMPILING_FLAGS"
                        #--disable-shared"    
}

# set install prefix
if [ -z $INSTALL_PREFIX ]; then
    EXTERNAL_SOURCES_DIR=$(pwd)
    EXTERNALS_DIR=$(dirname "$EXTERNAL_SOURCES_DIR")/externals
    export INSTALL_PREFIX=$EXTERNALS_DIR
fi
echo using INSTALL_PREFIX: $INSTALL_PREFIX

case $(uname -s) in
    Linux)  
        CC="gcc" 
        ;;           
    Darwin) 
        HOST_OS="darwin"
        CC="clang"
        if [ $# -eq 3 ]; then
            if [ $1 = "ios" ]; then
                HOST_OS="ios"
                setup_ios_env $2 $3
            fi
        fi
        ;;
    *)      
        echo this platform is not supported
        exit 1
esac

# export 
export CC CFLAGS LDFLAGS

# 
BUILD_DIR=build.$HOST_OS.$HOST_ARCH

#
# download apr sources
#
download(){
    echo removing apr...
    rm -rf apr
    mkdir apr
    cd apr

    # apr
    echo downloading apr from "https://svn.apache.org/repos/asf/apr/apr/tags/1.4.8/" ...
    svn export https://svn.apache.org/repos/asf/apr/apr/tags/1.4.8/ apr

    #apr-util
    echo downloading "https://svn.apache.org/repos/asf/apr/apr-util/tags/1.5.2/ apr-util"
    svn export https://svn.apache.org/repos/asf/apr/apr-util/tags/1.5.2/ apr-util

    # apr-iconv
    echo downloading apr_iconv from "https://svn.apache.org/repos/asf/apr/apr-iconv/tags/1.2.1/" ...
    svn export https://svn.apache.org/repos/asf/apr/apr-iconv/tags/1.2.1/ apr-iconv

    cd ..
}

#
# build apr sources
#
build_apr(){
    # build apr
    echo building apr...
    cd apr/apr
    ./buildconf
    mkdir -p $BUILD_DIR
    cd $BUILD_DIR
    ../configure --prefix=$INSTALL_PREFIX/apr/1.4.8 $EXTRA_CONFIGURE_FLAGS
    make -j8 | tee make_$HOST_ARCH.log 2>&1
    make install | tee make_install_$HOST_ARCH.log 2>&1
    cd ../../..
}

build_apr_iconv(){
    echo building apr_iconv...
    cd apr/apr-iconv
    ./buildconf
    mkdir -p $BUILD_DIR
    cd $BUILD_DIR
    ../configure --prefix=$INSTALL_PREFIX/apr/1.4.8 --with-apr=$INSTALL_PREFIX/apr/1.4.8 $EXTRA_CONFIGURE_FLAGS
    make -j8 | tee make_$HOST_ARCH.log 2>&1
    make install | tee make_install_$HOST_ARCH.log 2>&1
    cd ../../..
}

#
# build CUnit sources
#
build_cunit(){
    echo building cunit...
    cd CUnit-2.1-2
    ./configure --prefix=$INSTALL_PREFIX/cunit/2.1 $EXTRA_CONFIGURE_FLAGS
    make -j8 | tee make_$HOST_ARCH.log 2>&1
    make install | tee make_install_$HOST_ARCH.log 2>&1
    cd ..
}

#
# Added to github to motify apr_general.h to cross-compile for iOS
#   - download
#
#download
build_apr
build_apr_iconv
build_cunit

cd ..




