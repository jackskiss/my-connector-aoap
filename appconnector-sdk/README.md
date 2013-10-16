# App Connector SDK

Automotive Connectivity Framework

## Building Apache Etch

### 0. prerequisites
- autoconf
- libtool
- jdk
- ant
- cmake
- gnu-multilib gnu++_multilib (only for building on 64 bits linux machine)
- please add here if not listed

### 1. building external-sources

etch has some external dependencies. All dependencies are located under 'externals'. Java and source dependencies are already there but some c/c++ based dependencies need to be built before being referened by etch. 

1. go to 'etch'
2. run:
    - $ ./build-externals.sh
3. the followings should be built and installed into etch/externals
    - apr
    - cunit

##### Trubleshooting

You may be getting some errors while building cunit

case 1> aclocal.m4 or automake version related error
- run 'aclocal' in external-sources/CUnit-2.1.2 and then try build-some.sh

case 2> mv: cannot stat `.deps/CUError.Tpo': No such file or directory
- run 'autoreconf' in external-sources/CUnit-2.1.2 and then try build-some.sh

#### To build for iOS

1. go to 'etch'
2. run:
    - $ ./build-externals.sh ios (arch) (sdk)
        - arch:
    		- i386 - build for iPhone Simulator"
    		- armv7 - build for iPhone 4, 4S"
    		- armv7s - build for iPhone 5"
    	- sdk:
    		- 7.0 - use iPhone SDK v.7.0"
    - Examples:
        - $ ./build-externals.sh ios armv7s 7.0
        - $ ./build-externals.sh ios armv7 7.0
        - $ ./build-externals.sh ios i386 7.0

### 2. building etch

1. go to 'etch'
2. run:
    - $ ./build-etch.sh [binding]
        - binding:
        	- c - generate binding-c
        	- java - generate binding-java
        	- no option - generate bindig-c, binding-java, binding-csharp and examples
    - Examples:
        - $ ./build-etch.sh
        - $ ./build-etch.sh c
        - $ ./build-etch.sh java
3. the output should be found under the folder
    - trunk/target/Installers/dist

Or you can set some environment variables by yourself. 

1. set up the following variables with full path
    - ETCH_EXTERNAL_DEPENDS
        - e.g export ETCH_EXTERNAL_DEPENDS=/path-to-weblink/etch/externals
    - ETCH_HOME
        - e.g export ETCH_HOME=/path-to-weblink/etch/trunk/target/Installers/dist
    - JAVA_HOME
        - e.g export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
2. go to 'etch/trunk' and run:
    - $ ant Debug


#### To build binding-c for iOS

> prerequisites
> - copy toolchains/iOS.cmake file to CMake's platform folder
> - e.g:  /Applications/CMake 2.8.11.app/Contentes/share/cmake-2.8/Modules/Platform
    
1. go to 'etch'
2. run:
    - $ ./build-etch-c-ios.sh (arch)
        - arch:
    		- i386 - build for iPhone Simulator"
    		- armv7 - build for iPhone 4, 4S"
    		- armv7s - build for iPhone 5"
    - Examples:
        - $ ./build-etch-c-ios.sh armv7s
        - $ ./build-etch-c-ios.sh armv7
        - $ ./build-etch-c-ios.sh i386

### 3. running 'helloworld' example

#### 3.1 binding-c example

1. go to 'etch/trunk/examples/helloworld/c/target/bin
2. run:
    - $ ./helloworld-server
3. lauch another terminal and goto the same directory above
4. run:
    - $ ./helloworld-client
5. you can see the followings in client terminal, if succeeded

    Hello User
    
    any key …

#### 3.2 binding-java example

1. go to 'etch/trunk/examples/helloworld/java/target/bin'
2. experiment with .jar files
