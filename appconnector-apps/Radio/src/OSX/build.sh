#!/bin/bash

ETCH_DIR=/Users/shka/obigodev/weblink-apps/etch/binding-c-osx-i386
echo $ETCH_DIR

mkdir -p target
cd target

export ETCH_HOME=${ETCH_DIR}

cmake -DETCH_HOME=$ETCH_HOME ..

make

cd ..