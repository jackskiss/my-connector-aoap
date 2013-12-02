/*
 * UsbHostController.cpp
 *
 *  Created on: 2013. 11. 28.
 *      Author: jackmin
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <libusb.h>
#include <pthread.h>

typedef struct __usb_connection_cb_type {
	void (*device_connect) ( void * , ssize_t);
	void (*device_diconnect) ( void * , ssize_t);
} usb_connection_cb_type;

typedef struct _device_info_t {
	uint16_t vid;
	uint16_t pid;
	uint16_t pid_adb;
	int bulk_interface;
	unsigned char bulk_ep_in;
	unsigned char bulk_ep_out;
	int audio_interface;
	unsigned char audio_ep_in;
	int audio_alt;
} device_info_t;

/* Structures */
typedef struct _accessory_t {
  struct libusb_device_handle* handle;
  struct libusb_transfer* bulk_transfer;
  struct libusb_transfer* audio_transfer;
  uint32_t device_version;
  uint16_t vid;
  uint16_t pid;
  const char *device;
  const char *manufacturer;
  const char *model;
  const char *description;
  const char *version;
  const char *url;
  const char *serial;
} accessory_t;


typedef enum __aoap_device_connect_step_type {
	AOAP_DEVICE_NOT_CONNECT = 0,
	AOAP_DEVICE_CONNECTED,
	AOAP_DEVICE_OPENED,
	AOAP_DEVICE_ACCESSORY_REQUEST,
	AOAP_DEVICE_GOOGLE_MODE_WAIT,
	AOAP_DEVICE_GOOGLE_MODE,
	AOAP_DEVICE_ACCESSORY_CONNECTED
} aoap_device_connect_step_type;

static device_info_t device_info[2] = {
	{
		0x18D1,   /* Google VID*/
		0x4e21,   /* Google Nexus S PID */
		0x4e22,   /* Google Nexus S PID with ADB */
		0x00,     /* Google Nexus S BULK INTERFACE */
		0x85,     /* Google Nexus S BULK EP IN */
		0x07,     /* Google Nexus S BULK EP OUT */
		0x02,     /* Google Nexus S AUDIO_INTERFACE */
		0x8F,     /* Google Nexus S AUDIO EP IN */
		1         /* Google Nexus S AUDIO ALT SETTING */
	},
	{
		0x04E8,   /* Samsung VID */
		0x685C,   /* Samsung Galaxy Nexus PID */
		0x6860,   /* Samsung Galaxy Nexus PID with ADB */
		0x00,     /* Samsung Galaxy Nexus BULK INTERFACE */
		0x81,     /* Samsung Galaxy Nexus BULK EP IN */
		0x02,     /* Samsung Galaxy Nexus BULK EP OUT */
		0x02,     /* Samsung Galaxy Nexus AUDIO_INTERFACE */
		0x82,     /* Samsung Galaxy Nexus AUDIO EP IN */
		1         /* Samsung Galaxy Nexus AUDIO ALT SETTING */
	}
};

static accessory_t accessory = {
		NULL, 								  		//struct libusb_device_handle* handle;
		NULL,                                 //struct libusb_transfer* bulk_transfer;
		NULL,                                 //struct libusb_transfer* audio_transfer;
		0x00000000,                           //uint32_t device_version;
		0x0000,                               //uint16_t vid;
		0x0000,                               //uint16_t pid;
		"OBIGO-HUD",                          //const char *device;
		"OBIGO",                              //const char *manufacturer;
		"OBIGO-HUD-2014",                     //const char *model;
		"OBIGO AppConnector",                 //const char *description;
		"1.0",                                //const char *version;
		"http://www.obigo.com",               //const char *url;
		"OBIGO-HUD-0123456789"                //const char *serial;
};

enum accessory_pid_list {
	USB_ACCESSORY_PID					= 0x2D00,  /* accessory */
	USB_ACCESSORY_ADB_PID			= 0x2D01,  /* accessory + adb */
	USB_AUDIO_PID						= 0x2D02,  /* audio */
	USB_AUDIO_ADB_PID					= 0x2D03,  /* audio + adb */
	USB_ACCESSORY_AUDIO_PID			= 0x2D04,  /* accessory + audio */
	USB_ACCESSORY_AUDIO_ADB_PID		= 0x2D05,  /* accessory + audio + adb */
};

/* Send a 51 control request ("Get Protocol") to figure out
 * if the device supports the Android accessory protocol.
 *
 *  requestType:    USB_DIR_IN | USB_TYPE_VENDOR
 *  request:        51
 *  value:          0
 *  index:          0
 *  data:           protocol version number (16 bits little endian sent
 *                  from the device to the accessory)
 */
#define ACCESSORY_GET_PROTOCOL        51

/* If the device returns a proper protocol version,
 * send identifying string information to the device.
 *
 *  requestType:    USB_DIR_OUT | USB_TYPE_VENDOR
 *  request:        52
 *  value:          0
 *  index:          string ID
 *  data            zero terminated UTF8 string sent from accessory to device
 */
#define ACCESSORY_SEND_STRING         52

/* When the identifying strings are sent,
 * request the device start up in accessory mode.
 *
 *  requestType:    USB_DIR_OUT | USB_TYPE_VENDOR
 *  request:        52
 *  value:          0
 *  index:          string ID
 *  data            zero terminated UTF8 string sent from accessory to device
 */
#define ACCESSORY_START               53

/* Control request for registering a HID device.
 * Upon registering, a unique ID is sent by the accessory in the
 * value parameter. This ID will be used for future commands for
 * the device
 *
 *  requestType:    USB_DIR_OUT | USB_TYPE_VENDOR
 *  request:        54
 *  value:          Accessory assigned ID for the HID device
 *  index:          total length of the HID report descriptor
 *  data            none
 */
#define ACCESSORY_REGISTER_HID        54

/* Control request for unregistering a HID device.
 *
 *  requestType:    USB_DIR_OUT | USB_TYPE_VENDOR
 *  request:        55
 *  value:          Accessory assigned ID for the HID device
 *  index:          0
 *  data            none
 */
#define ACCESSORY_UNREGISTER_HID      55

/* Control request for sending the HID report descriptor.
 * If the HID descriptor is longer than the endpoint zero max packet size,
 * the descriptor will be sent in multiple ACCESSORY_SET_HID_REPORT_DESC
 * commands. The data for the descriptor must be sent sequentially
 * if multiple packets are needed.
 *
 *  requestType:    USB_DIR_OUT | USB_TYPE_VENDOR
 *  request:        56
 *  value:          Accessory assigned ID for the HID device
 *  index:          offset of data in descriptor
 *                      (needed when HID descriptor is too big for one packet)
 *  data            the HID report descriptor
 */
#define ACCESSORY_SET_HID_REPORT_DESC 56

/* Control request for sending HID events.
 *
 *  requestType:    USB_DIR_OUT | USB_TYPE_VENDOR
 *  request:        57
 *  value:          Accessory assigned ID for the HID device
 *  index:          0
 *  data            the HID report for the event
 */
#define ACCESSORY_SEND_HID_EVENT      57

 /* Control request for sending HID events.
 *
 *  requestType:    USB_DIR_OUT | USB_TYPE_VENDOR
 *  request:        58
 *  value:          0 for no audio (default),
 *                  1 for 2 channel, 16-bit PCM at 44100 KHz
 *  index:          0
 *  data            none
 */
#define ACCESSORY_SET_AUDIO_MODE      58

/* String IDs */
#define ACCESSORY_STRING_MANUFACTURER 0
#define ACCESSORY_STRING_MODEL        1
#define ACCESSORY_STRING_DESCRIPTION  2
#define ACCESSORY_STRING_VERSION      3
#define ACCESSORY_STRING_URI          4
#define ACCESSORY_STRING_SERIAL       5

#define ACCESSORY_GOOGLE_VID          0x18D1  /* Google VID*/

static pthread_t usb_thread_id = 0;

static pthread_t usb_connect_thread_id = 0;

static pthread_t usb_bulk_thread_id = 0;

static bool is_usb_detecting = true;

static void *thread_usb_detect(void * data);

static void *thread_device_connect(void * data);

static void *thread_usb_bulk( void * data );

static void usb_attached(void * data, ssize_t cnt);

static void usb_detached(void * data, ssize_t cnt);

static bool connect_accessory_mode(libusb_device *dev);

static bool set_accessory_mode(accessory_t* acc);

static bool get_protocol(void);

static bool detect_google_device(void);

static bool claim_interface(libusb_device_handle* dev, int interface_number);

static bool open_bulk_endpoint();

static bool check_registered_device(libusb_device **list, ssize_t cnt);

static int bulk_send( void * data, int length );

static usb_connection_cb_type usb_cb;

static aoap_device_connect_step_type device_status = AOAP_DEVICE_NOT_CONNECT;

#define BULK_DATA_PACKET_BUFFER 	16*1024

static unsigned char bulk_buffer[BULK_DATA_PACKET_BUFFER];

#define GET_DEVICE_STATUS()		device_status

#define SET_DEVICE_STATUS(X)	device_status =  ( aoap_device_connect_step_type ) X

#define ACCESSORY					accessory

static device_info_t	*	device = &device_info[1];

#define DEVICE			device

int main (int argc, char *argv[])
{
	libusb_device ** list;

	usb_cb.device_connect = usb_attached;
	usb_cb.device_diconnect = usb_detached;

	libusb_init(NULL);

	// libusb_set_debug ( NULL, 3 );

	ssize_t i = libusb_get_device_list( NULL, &list );

	check_registered_device( list, i );

	if (pthread_create (&usb_thread_id, NULL, thread_usb_detect, &usb_cb)) {
		printf("Failed to create thread_usb thread");
	}

	char msg[100];

	while(true) {

		fgets( msg, 100, stdin );

		if (msg[0] == '\n')
			break;
		else
			bulk_send(msg, strlen(msg));

		usleep(20000);

	}

	is_usb_detecting = false;

	if(usb_thread_id)
		pthread_join(usb_thread_id, NULL);


	fprintf(stdout, "Program Exit");

	libusb_exit( NULL );

	exit(1);
}


static bool check_registered_device(libusb_device **list, ssize_t cnt)
{
	uint16_t vid, pid;

	while(cnt--)
	{
		libusb_device *dev = list[cnt];
		libusb_device_descriptor desc;

		libusb_get_device_descriptor(dev, &desc);

		vid = desc.idVendor;
		pid = desc.idProduct;

		for( unsigned int i= 0; i < sizeof(device_info)/sizeof(device_info_t) ; i++ ) {

			//DEVICE = &device_info[i];

			if ( vid ==  ACCESSORY_GOOGLE_VID ) {

				fprintf(stdout, "Found google device vid %d, pid %d \n", vid, pid);

				SET_DEVICE_STATUS( AOAP_DEVICE_GOOGLE_MODE );

				if( connect_accessory_mode ( dev ) ) {

					open_bulk_endpoint();

					fprintf(stdout, "Device opened as Google USB driver\n");

					SET_DEVICE_STATUS( AOAP_DEVICE_ACCESSORY_CONNECTED );

					if (pthread_create (&usb_bulk_thread_id, NULL, thread_usb_bulk, dev)) {
						printf("Failed to create thread_device_connect thread");
					}

					return true;
				}

			} else if ( vid == device_info[i].vid ) {

				fprintf(stdout, "Found registered device vid %d, pid %d \n", vid, pid);

				SET_DEVICE_STATUS( AOAP_DEVICE_CONNECTED );

				if (pthread_create (&usb_connect_thread_id, NULL, thread_device_connect, dev)) {
					printf("Failed to create thread_device_connect thread");
				}

				return true;
			}
		}
	}

	fprintf(stdout, "Not found a registered device \n");

	return false;
}

static void usb_attached(void *data, ssize_t cnt)
{
	fprintf(stdout, "++++++++++USB Attached++++++++++\n");
	check_registered_device((libusb_device **)data, cnt);
}

static void usb_detached(void *data, ssize_t cnt)
{
	fprintf(stdout, "----------USB Detached----------\n");

	if(ACCESSORY.handle) {
		libusb_release_interface(ACCESSORY.handle, DEVICE->audio_interface);
		libusb_release_interface(ACCESSORY.handle, DEVICE->bulk_interface);
		libusb_close(ACCESSORY.handle);
		ACCESSORY.handle = NULL;
	}

	SET_DEVICE_STATUS( AOAP_DEVICE_NOT_CONNECT );
}


void *thread_usb_detect(void * data)
{
	libusb_device ** list;

	static ssize_t cnt = -1;

	usb_connection_cb_type * usbcb = ( usb_connection_cb_type * ) data;

	while(is_usb_detecting)
	{
		ssize_t i = libusb_get_device_list( NULL, &list );

		if( cnt < 0 ) // Initialize
			cnt = i;

		if( i > cnt ) {
			if( GET_DEVICE_STATUS() == AOAP_DEVICE_NOT_CONNECT) {
				usbcb->device_connect( list, i );
			}

		} else if (i < cnt) {
			usbcb->device_diconnect( NULL, i );
		}

		cnt = i;

		usleep(1000000);
	}

	libusb_free_device_list(list, 1);

	pthread_exit( NULL );
}

typedef enum __aoap_error_type {
	AOAP_ACCESORY_OPEN_ERROR = -1,
	AOAP_GET_PROTOCOL_ERROR = -2,
	AOAP_ACCESSORY_START_ERROR = -3 ,
	AOAP_KERNEL_DRIVER_DETATCH_ERROR = -4,
	AOAP_AUDIO_INTERFACE_CLAIM_ERROR = -5,
	AOAP_BULK_INTERFACE_CLAIM_ERROR = -6,
	AOAP_GOOGLE_DEVICE_DETECT_ERROR = -7,
} aoap_error_type;

static void *thread_device_connect( void * data )
{
	fprintf(stdout, "start thread_device_connect\n");

	aoap_error_type result;

	libusb_device * dev = ( libusb_device *) data;

	if ( GET_DEVICE_STATUS() == AOAP_DEVICE_GOOGLE_MODE ) {

		fprintf(stdout, "connect_accessory_mode\n");

		if( !connect_accessory_mode( dev ) ) {
			result = 	AOAP_ACCESORY_OPEN_ERROR;
			goto Connect_Err;
		}

	} else {

		fprintf(stdout, "connect_accessory_mode\n");

		if( !connect_accessory_mode( dev ) ) {
			result = 	AOAP_ACCESORY_OPEN_ERROR;
			goto Connect_Err;
		}

		fprintf(stdout, "get_protocol\n");

		if (!get_protocol()) {
			result = 	AOAP_GET_PROTOCOL_ERROR;
			goto Connect_Err;
		}

		fprintf(stdout, "set_accessory_mode\n");

		if ( !set_accessory_mode( &ACCESSORY ) ) {
			result = 	AOAP_ACCESSORY_START_ERROR;
			goto Connect_Err;
		}

		if (! detect_google_device() ) {
			result = 	AOAP_GOOGLE_DEVICE_DETECT_ERROR;
			goto Connect_Err;
		}

	}

	pthread_exit( NULL );
	return 0;

Connect_Err:
	fprintf(stdout, "Connection Error: %d result\n", (int)result);
	SET_DEVICE_STATUS( AOAP_DEVICE_NOT_CONNECT );

	if(ACCESSORY.handle) {
		libusb_close(ACCESSORY.handle);
		ACCESSORY.handle = NULL;
	}

	pthread_exit( NULL );
}

static bool connect_accessory_mode(libusb_device *dev)
{
	libusb_device_handle *handle;

	int result = libusb_open( dev, &handle);

	if (result == 0) {
		ACCESSORY.handle = handle;
		return true;
	}

	fprintf(stdout, "libusb_open Error: %d\n", result);

	return false;
}

static bool get_protocol( void ) {

	uint8_t version[2] = {0, 0};

	int result = libusb_control_transfer( accessory.handle,
				  LIBUSB_ENDPOINT_IN |
				  LIBUSB_REQUEST_TYPE_VENDOR,
				  ACCESSORY_GET_PROTOCOL, 0, 0, version,
				  sizeof(version), 0);

	if (result < 0) {
		//libusb_error(result);
		return false;
	}

	accessory.device_version = ((version[1] << 8) | version[0]);
	return true;
}

static int send_string(libusb_device_handle* handle, int index, const char* string) {
  return libusb_control_transfer(handle,
				  LIBUSB_ENDPOINT_OUT
				  | LIBUSB_REQUEST_TYPE_VENDOR,
				  ACCESSORY_SEND_STRING, 0,
				  index,
				  (uint8_t *) string,
				  strlen(string) + 1, 0);
}


static bool set_accessory_mode(accessory_t* acc) {
  int result;

  result = send_string(acc->handle, ACCESSORY_STRING_MANUFACTURER, acc->manufacturer);
  if (result < 0) goto accessory_error;

  result = send_string(acc->handle, ACCESSORY_STRING_MODEL, acc->model);
  if (result < 0) goto accessory_error;

  result = send_string(acc->handle, ACCESSORY_STRING_DESCRIPTION, acc->description);
  if (result < 0) goto accessory_error;

  result = send_string(acc->handle, ACCESSORY_STRING_VERSION, acc->version);
  if (result < 0) goto accessory_error;

//  result = send_string(acc->handle, ACCESSORY_STRING_URI, acc->url);
//  if (result < 0) goto accessory_error;

  result = send_string(acc->handle, ACCESSORY_STRING_SERIAL, acc->serial);
  if (result < 0) goto accessory_error;

  if (ACCESSORY.device_version >= 2) {
	 result = libusb_control_transfer(acc->handle,
					 LIBUSB_ENDPOINT_OUT
					 | LIBUSB_REQUEST_TYPE_VENDOR,
					 ACCESSORY_SET_AUDIO_MODE, 1, 0, 0, 0, 0);
	 if (result < 0)
		goto accessory_error;
  }

  result = libusb_control_transfer(acc->handle,
				  LIBUSB_ENDPOINT_OUT |
				  LIBUSB_REQUEST_TYPE_VENDOR,
				  ACCESSORY_START, 0, 0, NULL, 0, 0);
  if (result < 0)
	 goto accessory_error;

  SET_DEVICE_STATUS( AOAP_DEVICE_GOOGLE_MODE_WAIT );

  libusb_reset_device(ACCESSORY.handle);

  return true;

accessory_error:
//  libusb_error(result);
  return false;
}

static bool detect_google_device(void)
{
	int loop_cnt = 10; // 10 seconds

	libusb_device **list;

	while(loop_cnt--)
	{
		ssize_t i = libusb_get_device_list( NULL, &list );

		if( check_registered_device( list, i ) && ( GET_DEVICE_STATUS() == AOAP_DEVICE_ACCESSORY_CONNECTED ) ) {
				return true;
		}

		usleep(1000000);
	}

	libusb_free_device_list(list, 1);

	fprintf(stdout, "Not found Google device\n");

	return false;
}

static bool open_bulk_endpoint()
{
	aoap_error_type result;

	libusb_detach_kernel_driver(ACCESSORY.handle, DEVICE->audio_interface);

	libusb_detach_kernel_driver(ACCESSORY.handle, DEVICE->bulk_interface);

	if (!claim_interface(ACCESSORY.handle, DEVICE->audio_interface)) {
		result = AOAP_AUDIO_INTERFACE_CLAIM_ERROR; goto end;
	}

	if (!claim_interface(ACCESSORY.handle, DEVICE->bulk_interface)) {
		result = AOAP_BULK_INTERFACE_CLAIM_ERROR; goto end;
	}

	return true;

end:
	SET_DEVICE_STATUS( AOAP_DEVICE_NOT_CONNECT );
	fprintf(stdout, "Fail to open the endpoint result : %d\n", result);
	return false;
}


static bool claim_interface(libusb_device_handle* dev, int interface_number)
{
	int result;

	result = libusb_claim_interface(dev, interface_number);

	if (result != 0) {
		return false;
	}

	return true;
}


static int bulk_send( void * data, int length )
{
	int actual_length;
	int result;

	if( GET_DEVICE_STATUS() == AOAP_DEVICE_ACCESSORY_CONNECTED ) {

		result = libusb_bulk_transfer ( ACCESSORY.handle, DEVICE->bulk_ep_out, (unsigned char *) data, length, &actual_length, 500 );

		if(result == 0 && actual_length > 0) {
			// fprintf(stdout, "Send data : %d Byte ", actual_length);
		}

		usleep(20000);
	}

	return actual_length;
}


/*static int bulk_receive( void ** data )
{
	return 0;
}*/


static void *thread_usb_bulk( void * data )
{
	int result;
	int length;

	while( GET_DEVICE_STATUS() == AOAP_DEVICE_ACCESSORY_CONNECTED ) {

		result = libusb_bulk_transfer ( ACCESSORY.handle, DEVICE->bulk_ep_in, (unsigned char *) bulk_buffer, sizeof(bulk_buffer), &length, 500 );

		if(result == 0 && length > 0) {
			fprintf(stdout, "Bulk Data received %s : %d \n", (char *) bulk_buffer, length);
		}

		usleep(20000);
	}

	pthread_exit( NULL );
}

