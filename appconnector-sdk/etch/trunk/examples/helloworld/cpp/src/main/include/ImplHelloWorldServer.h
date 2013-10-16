// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / c 1.3.0-incubating (LOCAL-0)
//   Fri Jul 27 11:35:36 CEST 2012
// This file is automatically created for your convenience and will not be
// overwritten once it exists! Please edit this file as necessary to implement
// your service logic.

#ifndef __HELLOWORLDSERVER_H__
#define __HELLOWORLDSERVER_H__

#include "BaseHelloWorldServer.h"
#include "HelloWorldHelper.h"

namespace org_apache_etch_examples_helloworld_HelloWorld {
  /**
   * Your custom implementation of BaseHelloWorldServer. Add methods here to provide
   * implementations of messages from the mClient.
   */
  class ImplHelloWorldServer : public BaseHelloWorldServer
  {
    /**
     * A connection to the mClient session. Use this to send a
     * message to the mClient.
     */
  private:
    RemoteHelloWorldClient* mClient;

  public:
    /**
     * Constructs the ImplHelloWorldServer.
     *
     * @param mClient a connection to the mClient session. Use this to send a
     * message to the mClient.
     */
    ImplHelloWorldServer( RemoteHelloWorldClient* mClient );
    virtual ~ImplHelloWorldServer() {}

    // TODO insert methods here to provide declarations of HelloWorldServer
    // messages from the mClient.
    say_helloAsyncResultPtr say_hello(HelloWorld::userPtr to_whom);

  };
}

#endif /* __HELLOWORLDSERVER_H__ */