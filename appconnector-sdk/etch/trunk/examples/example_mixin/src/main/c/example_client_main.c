
// This file automatically generated by:
//   Apache Etch 1.2.0-incubating (LOCAL-0) / c 1.2.0-incubating (LOCAL-0)
//   Mon Mar 28 14:18:57 CEST 2011
// This file is automatically created and should not be edited!

/*
 * example_client_main.c
 */
//#define HAS_VLD 1
#ifdef HAS_VLD
#include "vld.h"
#endif

#include "example_client_main.h"
#include "etch_objecttypes.h"
#include "etch_runtime.h"
#include "etch_arrayval.h"
#include "etch_nativearray.h"
#include "etch_binary_tdo.h"
#include "etch_general.h"


/**
 * new_example_client().
 * callback constructor for client implementation object.
 * this callback address is passed to start_example_client() in [main].
 * @param server the remote server. 
 * @remarks this callback must be supplied, i.e. its functionality cannot be 
 * defaulted, since the client implementation constructor new_example_client_impl()
 * is not known to start_example_client().
 */
static i_example_client* example_client_create(void* factory_thisx, example_remote_server* server)
{
    example_client_impl* client = new_example_client_impl(server);
    return client? client->example_client_base:NULL;
}


/**
 * main()
 */
int main(int argc, char* argv[])
{
    etch_status_t etch_status    = ETCH_SUCCESS;
    example_remote_server* remote = NULL;
    int waitupms = 4000;
    
    wchar_t* uri = L"tcp://127.0.0.1:4001";	

    // define result string
    etch_string* res = NULL;

    etch_config_t* config = NULL;
    etch_config_create(&config);
    // set properties or read file, e.g.
    //etch_config_set_property(config, "etch.log", "xdebug, consoleappender");

    etch_status = etch_runtime_initialize(config);
    if(etch_status != ETCH_SUCCESS) {
        // error
        return 1;
    }

    etch_status = example_helper_remote_server_create(&remote, uri, NULL, example_client_create);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

    etch_status = example_helper_remote_server_start_wait(remote, waitupms);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

    printf("Client started.\n");

    res = remote->say_hello(remote, new_stringa("Hallo"));
    if(res != NULL) {
        printf("Client: %S\n", res->v.valw);
        etch_object_destroy(res);
    }

    res = remote->say_hello_oneway(remote, new_stringa("Hello"));
    if(res != NULL) {
        etch_object_destroy(res);
    }

    res = remote->say_hello_mixin(remote, new_stringa("Hallo"));
    if(res != NULL) {
        printf("Client-Mixin: %S\n", res->v.valw);
        etch_object_destroy(res);
    }

    res = remote->say_hello_mixin_oneway(remote, new_stringa("Hallo"));
    if(res != NULL) {
        etch_object_destroy(res);
    }

    // wait until key press
    waitkey();

    etch_status = example_helper_remote_server_stop_wait(remote, waitupms);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

    etch_status = example_helper_remote_server_destroy(remote);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

    etch_status = etch_runtime_shutdown();
    if(etch_status != ETCH_SUCCESS) {
        // error
        return 1;
    }

    etch_config_destroy(config);
    return 0;
}

