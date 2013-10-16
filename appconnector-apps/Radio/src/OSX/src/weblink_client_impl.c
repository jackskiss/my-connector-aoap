
// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / c 1.3.0-incubating (LOCAL-0)
//   Wed Sep 11 19:40:10 KST 2013
// This file is automatically created and should not be edited!

#include "weblink_client_impl.h"
#include "weblink_valufact.h"
#include "etch_url.h"
#include "etch_arrayval.h"
#include "etch_binary_tdo.h"
#include "etch_exception.h"
#include "etch_general.h"
#include "etch_log.h"
#include "etch_map.h"
#include <stdio.h>

unsigned short CLASSID_WEBLINK_CLIENT_IMPL;	
	
	
char* WEBLINK_ETCHCIMP = "CIMP";

/* generated signatures */
int destroy_weblink_client_implx(void*);
weblink_client_impl* init_weblink_client_impl(struct weblink_remote_server*, etch_object_destructor);


/* - - - - - - - -    
 * instantiation
 * - - - - - - - -   
 */

/**
 * new_weblink_client_impl()
 * weblink_client_impl constructor.
 * add your custom initialization and virtual method overrides here.
 */
weblink_client_impl* new_weblink_client_impl(struct weblink_remote_server* server) 
{
    weblink_client_impl* pclient  /* allocate object */
        = init_weblink_client_impl(server, destroy_weblink_client_implx);	
    i_weblink_client* pclient_base = pclient->weblink_client_base;
    /* add virtual method overrides, if any, here */
    //etchmap_insertxw(pclient_base->virtuals, weblink_valufact_get_static()->str_weblink_xxx, implementation, FALSE);

    return pclient;
}


/**
 * destroy_weblink_client_implx()
 * destructor for any user allocated memory.
 * this code is invoked by the private perf_client_impl destructor,
 * via perf_client.destroyex(). add code here to destroy any memory you  
 * may have allocated for your custom perf_client implementation.
 */
int destroy_weblink_client_implx(void* data)
{
    /*
      weblink_client_impl* thisx = (weblink_client_impl*)data;
     */

    return 0;
}

/* - - - - - - - - - - - - - - - - - - -
 * session interface method overrides
 * - - - - - - - - - - - - - - - - - - -
 */

 /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 * implementations of weblink_client messages from server, if any 
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 */
 
