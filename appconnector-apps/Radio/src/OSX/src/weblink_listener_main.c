
// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / c 1.3.0-incubating (LOCAL-0)
//   Wed Sep 11 19:40:10 KST 2013
// This file is automatically created and should not be edited!

/*
 * weblink_listener_main.c
 */
 
#include "weblink_listener_main.h"
#include "etch_objecttypes.h"
#include "etch_runtime.h"
#include "etch_arrayval.h"
#include "etch_nativearray.h"
#include "etch_binary_tdo.h"
#include "etch_general.h"


/**
 * new_weblink_server
 * create an individual client's weblink_server implementation.
 * this is java binding's newweblinkServer().
 * this is called back from helper.new_helper_accepted_server() (java's newServer).
 * @param p parameter bundle. caller retains. 
 * @return the i_weblink_server, whose thisx is the weblink_server_impl.
 */
static void* weblink_server_create(void* factoryData, void* sessionData)
{
    etch_session* session = (etch_session*)sessionData;
    weblink_remote_client* client  = (weblink_remote_client*) session->client;

    weblink_server_impl* newserver = new_weblink_server_impl(client);

    return newserver->weblink_server_base;
}

etch_status_t weblink_listener_start(i_sessionlistener** pplistener, wchar_t* uri, int waitupms)
{
    etch_status_t etch_status = ETCH_SUCCESS;
    
    etch_status = weblink_helper_listener_create(pplistener, uri, NULL, weblink_server_create);
    if(etch_status == ETCH_SUCCESS) 
    {
        etch_status = weblink_helper_listener_start_wait(*pplistener, waitupms);
    }

    return etch_status;
}

etch_status_t weblink_listener_stop(i_sessionlistener* plistener, int waitupms)
{
    etch_status_t etch_status = ETCH_SUCCESS;
    
    etch_status = weblink_helper_listener_stop_wait(plistener, waitupms);
    if(etch_status == ETCH_SUCCESS) 
    {
        weblink_helper_listener_destroy(plistener);
    }

    return etch_status;
}


#ifndef NO_ETCH_SERVER_MAIN

/**
 * main()
 */
int main(int argc, char* argv[])
{
	etch_status_t etch_status = ETCH_SUCCESS;
    i_sessionlistener* listener = NULL;
    int waitupms = 4000;
    
    wchar_t* uri = L"tcp://0.0.0.0:4001";

    etch_config_t* config = NULL;
    etch_config_create(&config);
	// set properties or read file, e.g.
    //etch_config_set_property(config, "etch.log", "xdebug, consoleappender");
	
    etch_status = etch_runtime_initialize(config);
    if(etch_status != ETCH_SUCCESS) {
        // error
        return 1;
    }

    etch_status = weblink_listener_start(&listener, uri, waitupms);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

    // wait for keypress
    waitkey();

    etch_status = weblink_listener_stop(listener, waitupms);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

    etch_status = etch_runtime_shutdown();
    if(etch_status != ETCH_SUCCESS) {
        // error
        return 1;
    }
	etch_config_destroy(config);
    // wait for keypress
    waitkey();
	
    return 0;
}

#endif /* NO_ETCH_SERVER_MAIN */
