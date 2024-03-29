
// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / c 1.3.0-incubating (LOCAL-0)
//   Wed Sep 11 19:40:10 KST 2013
// This file is automatically created and should not be edited!

/*
 * weblink_client_main.c
 */
 
#include "weblink_client_main.h"
#include "etch_objecttypes.h"
#include "etch_runtime.h"
#include "etch_arrayval.h"
#include "etch_nativearray.h"
#include "etch_binary_tdo.h"
#include "etch_general.h"


/**
 * new_weblink_client().
 * callback constructor for client implementation object.
 * this callback address is passed to start_weblink_client() in [main].
 * @param server the remote server. 
 * @remarks this callback must be supplied, i.e. its functionality cannot be 
 * defaulted, since the client implementation constructor new_weblink_client_impl()
 * is not known to start_weblink_client().
 */
static i_weblink_client* weblink_client_create(void* factory_thisx, weblink_remote_server* server)
{
    weblink_client_impl* client = new_weblink_client_impl(server);
    return client? client->weblink_client_base:NULL;
}

static void send_remote_control(weblink_remote_server* remote, weblink_RemoteControl_enum val)
{
    void* result = NULL;

    weblink_RemoteControl* control = new_weblink_RemoteControl();
    control->value = val;
    result = remote->remote_control(remote, control);
    if(is_etch_exception(result)) {
        etch_exception* ex = (etch_exception*)result;
        etch_string* ex_string = etch_exception_get_message(ex);
        printf("Exception: %S\n",ex_string->v.valw);
    }
    etch_object_destroy(result);   
}

static void send_radio_station_list(weblink_remote_server* remote)
{
    etch_arraytype* result = NULL;

    result = remote->radio_station_list(remote);
    if(is_etch_exception(result)) {
        etch_exception* ex = (etch_exception*)result;
        etch_string* ex_string = etch_exception_get_message(ex);
        printf("Exception: %S\n",ex_string->v.valw);
    }
    else {
        struct etch_nativearray* na = (struct etch_nativearray*)result;
        for (int i = 0; i < arrayvalue_count(na); i++) {
            weblink_RadioStation* item = arrayvalue_get(na, i);
            printf("station id: %c\n", item->id + 'a');
            printf("        title=%S\n", (wchar_t*)item->title->v.valw);
            printf("        genre=%S\n", (wchar_t*)item->genre->v.valw);
            printf("        url=%S\n", (wchar_t*)item->url->v.valw);
        }
    }

    etch_object_destroy(result);
}

static void send_radio_station_list_v2(weblink_remote_server* remote)
{
    etch_int32* result_count = NULL;

    result_count = remote->radio_station_count(remote);
    if(is_etch_exception(result_count)) {
        etch_exception* ex = (etch_exception*)result_count;
        etch_string* ex_string = etch_exception_get_message(ex);
        printf("Exception: %S\n",ex_string->v.valw);
    }
    else {
        for (int i = 0; i < result_count->value; i++) {
            weblink_RadioStation* result_station = NULL;
            etch_int32* station_index = new_int32(i);
            
            result_station = remote->radio_station_get_at(remote, station_index);
            if(is_etch_exception(result_station)) {
                etch_exception* ex = (etch_exception*)result_station;
                etch_string* ex_string = etch_exception_get_message(ex);
                printf("Exception: %S\n",ex_string->v.valw);
            } else {
                printf("station id: %c\n", result_station->id + 'a');
                printf("        title=%S\n", (wchar_t*)result_station->title->v.valw);
                printf("        genre=%S\n", (wchar_t*)result_station->genre->v.valw);
                printf("        url=%S\n", (wchar_t*)result_station->url->v.valw);
            }

            etch_object_destroy(result_station);
        }
    }

    etch_object_destroy(result_count);
}

static void send_radio_station_select(weblink_remote_server* remote, int id)
{
    void* result = NULL;

    etch_int32* station_id = new_int32(id);
    result = remote->radio_station_select(remote, station_id);
    if(is_etch_exception(result)) {
        etch_exception* ex = (etch_exception*)result;
        etch_string* ex_string = etch_exception_get_message(ex);
        printf("Exception: %S\n",ex_string->v.valw);
    }
    etch_object_destroy(result);    
}

int get_user_cmd()
{
    char* usage = "\n"
                  "\n"
                  "Available commands:\n"
                  "    1> print radion statoins\n"
                  "    a-g> select radio station\n"
                  "    2> play\n"
                  "    3> pause\n"
                  "    4> next station\n"
                  "    5> previous station\n"
                  "    6> volume up\n"
                  "    7> volume down\n"
                  "    x> exit\n"
                  "select command: ";

    printf(usage);
    return getc(stdin);
}

/**
 * main()
 */
int main(int argc, char* argv[])
{
    etch_status_t etch_status    = ETCH_SUCCESS;
    weblink_remote_server* remote = NULL;
    int waitupms = 4000;
    
    //wchar_t* uri = L"tcp://127.0.0.1:4001";	
    wchar_t* uri = L"tcp://192.168.0.32:4001"; 
	
       etch_config_t* config = NULL;
    etch_config_create(&config);
    etch_config_set_property(config, "etch.mailbox.timeout.read", "10000");
    // set properties or read file, e.g.
    //etch_config_set_property(config, "etch.log", "xdebug, consoleappender");

    etch_status = etch_runtime_initialize(config);
    if(etch_status != ETCH_SUCCESS) {
        // error
        return 1;
    }

    etch_status = weblink_helper_remote_server_create(&remote, uri, NULL, weblink_client_create);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

    etch_status = weblink_helper_remote_server_start_wait(remote, waitupms);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }
	
    int cmd;
    while ((cmd = get_user_cmd()) != 'x') {
        switch (cmd) {
        case '1':
            send_radio_station_list_v2(remote);
            break; 

        case '2':
            send_remote_control(remote, RemoteControl_PLAY);
            break; 
        
        case '3':
            send_remote_control(remote, RemoteControl_PAUSE);
            break; 
        
        case '4':
            send_remote_control(remote, RemoteControl_NEXT);
            break; 
        
        case '5':
            send_remote_control(remote, RemoteControl_PREV);
            break; 

        case '6':
            send_remote_control(remote, RemoteControl_VOLUME_UP);
            break; 
        
        case '7':
            send_remote_control(remote, RemoteControl_VOLUME_DOWN);
            break; 

        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
            send_radio_station_select(remote, (cmd - 'a'));
            break;        
        }
    }
    
    // wait until key press
    waitkey();

    etch_status = weblink_helper_remote_server_stop_wait(remote, waitupms);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

    etch_status = weblink_helper_remote_server_destroy(remote);
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

	etch_status = etch_runtime_shutdown();
    if(etch_status != ETCH_SUCCESS) {
        // error
    }

	etch_config_destroy(config);

	return 0;
}

