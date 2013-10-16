//
//  InternetRadioImpl.h
//  WebLinkRadio
//
//  Created by 강신규 on 13. 9. 12..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#ifndef __WebLinkRadio__RadioImpl__
#define __WebLinkRadio__RadioImpl__

#include "weblink_interface.h"

#ifdef __cplusplus
extern "C" {
#endif

etch_arraytype* weblink_radio_station_list_impl(void* thisx);
etch_int32* weblink_radio_station_count_impl(void* thisx);
weblink_RadioStation* weblink_radio_station_get_at_impl(void* thisx, etch_int32* index);
void* weblink_radio_station_select_impl(void* thisx, etch_int32* radio_station_id);

#ifdef __cplusplus
} //extern "C"
#endif

#endif /* defined(__WebLinkRadio__RadioImpl__) */
