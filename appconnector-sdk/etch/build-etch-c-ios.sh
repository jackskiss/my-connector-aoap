#!/bin/bash

# goto bindig-c
cd trunk/binding-c

ETCH_DIR=`dirname $(dirname $(pwd))`
echo $ETCH_DIR

print_usage(){
    echo "Command is not valid"
    echo "Usage:"
    echo "    ./build-etch-c-ios.sh [arch]"
    echo "    arch:"
    echo "        i386      build for iPhone Simulator"
    echo "        armv7     build for iPhone 4, 4S"
    echo "        armv7s    build for iPhone 5"
}

IOS_ARCH=$1
case $IOS_ARCH in
  i386 )
    TOOLCHAIN_FILE=iOS-Simulator.cmake
    IOS_PLATFORM=SIMULATOR
    ;;
  armv7 )
    TOOLCHAIN_FILE=iOS-Device.cmake
    IOS_PLATFORM=DEVICE
    ;;
  armv7s )
    TOOLCHAIN_FILE=iOS-Device.cmake
    IOS_PLATFORM=DEVICE
    ;;
  * )
    print_usage
    exit 1
esac

cd runtime/c

mkdir -p target
cd target

export ETCH_EXTERNAL_DEPENDS=${ETCH_DIR}/externals
export ETCH_HOME=${ETCH_DIR}/trunk/target/Installers/dist/binding-c-ios-$IOS_ARCH

CMAKE_TOOLCHAIN_FILE=${ETCH_DIR}/toolchains/${TOOLCHAIN_FILE}
cmake -DETCH_HOME=$ETCH_HOME -DETCH_EXTERNAL_DEPENDS:PATH=$ETCH_EXTERNAL_DEPENDS -DCMAKE_TOOLCHAIN_FILE:PATH=${ETCH_DIR}/toolchains/${TOOLCHAIN_FILE} -DIOS_ARCH=${IOS_ARCH} -GXcode ..

xcodebuild -target install -configuration Debug

cd ../../..

# go back to root
cd ../..