#!/bin/bash

print_usage(){
    echo "Usage:"
    echo "    ./build-etch.sh [option]"
    echo "    option:"
    echo "        c       build binding-c in debug mode"
    echo "        java    build binding-java in debug mode"
}

set_etch_env(){
    CURRENT_DIR=$(pwd)

    export ETCH_EXTERNAL_DEPENDS=$CURRENT_DIR/externals
    echo using ETCH_EXTERNAL_DEPENDS: $ETCH_EXTERNAL_DEPENDS

    export ETCH_HOME=$CURRENT_DIR/trunk/target/Installers/dist
    echo using ETCH_HOME: $ETCH_HOME
}

set_java_home(){
    if [ -z $JAVA_HOME ]; then
        case $(uname -s) in
            Darwin) 
                JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
                ;;
            Linux)
                JAVAC_CMD=$(which javac)
                JAVA_HOME=$(readlink -f "${JAVAC_CMD}" | sed "s:/bin/javac::")
                ;;
        esac
    fi

    if [ -z $JAVA_HOME ]; then
        echo "Please set JAVA_HOME"
        exit 1
    fi

    export JAVA_HOME
    echo using JAVA_HOME: $JAVA_HOME
}

set_etch_env
set_java_home

# build etch
cd trunk
OPTIONS=Debug
if [ $# -eq 1 ]; then
    OPTIONS="-Dbinding=$1 debug-binding"
fi
ant $OPTIONS
cd ..
