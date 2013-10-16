//
//  InternetRadioImpl.cpp
//  WebLinkRadio
//
//  Created by 강신규 on 13. 9. 12..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#include "WLRadioImpl.h"
#include "etch_nativearray.h"
#include "etch_objecttypes.h"
#include "etch_arrayval.h"
#include "etch_log.h"
#include <wchar.h>

#import "WLRadioStationStore.h"
#import "WLRadioStation.h"

#import "WLRadioImplDelegate.h"


static const char* LOG_CATEGORY =  "WLRadio";

// FIXME
static __weak id<WLRadioImplDelegate> _delegate;

void RadioImplDelegateSet(__weak id<WLRadioImplDelegate> delegate)
{
    _delegate = delegate;
}

etch_arraytype* weblink_radio_station_list_impl(void* thisx)
{
    etch_arraytype* ret = NULL;
    
    ETCH_LOG(LOG_CATEGORY, ETCH_LOG_DEBUG, "weblink_radio_station_list_impl()");
    
    NSArray *stations = [[WLRadioStationStore sharedStore] allStations];
    int stationCount = [stations count];
    
    etch_nativearray* na = new_etch_nativearray(CLASSID_ARRAY_OBJECT, sizeof(weblink_RadioStation *), 1, stationCount, 0, 0);
    //etch_nativearray* na = new_etch_nativearray(CLASSID_ARRAY_STRUCT, sizeof(weblink_RadioStation *), 1, stationCount, 0, 0);
    
    int index = 0;
    for (WLRadioStation *item in stations) {
        weblink_RadioStation *station = new_weblink_RadioStation();

        station->id = index;
        station->title = new_stringa([[item title] UTF8String]);
        station->genre = new_stringa([[item genre] UTF8String]);
        station->url = new_stringa([[item url] UTF8String]);
        station->thumb = new_stringa("");
       
        na->put1(na, &station, index++);
    }

    etch_arrayvalue* av = new_arrayvalue_from(na, ETCH_XTRNL_TYPECODE_ARRAY, NULL, 1, 0, FALSE);
    //etch_arrayvalue* av = new_arrayvalue_from(na, ETCH_XTRNL_TYPECODE_CUSTOM, NULL, 1, 0, FALSE);

    ret = (etch_arraytype*)av;
 
    return ret;
}

etch_int32* weblink_radio_station_count_impl(void* thisx)
{
    etch_int32* ret = NULL;
 
    ETCH_LOG(LOG_CATEGORY, ETCH_LOG_DEBUG, "weblink_radio_station_count_impl()");
    
    NSArray *stations = [[WLRadioStationStore sharedStore] allStations];
    int stationCount = [stations count];
    
    ret = new_int32(stationCount);
    
    return ret;
}

weblink_RadioStation* weblink_radio_station_get_at_impl(void* thisx, etch_int32* index)
{
    weblink_RadioStation* ret = NULL;
    
    ETCH_LOG(LOG_CATEGORY, ETCH_LOG_DEBUG, "weblink_radio_station_count_impl()");
    
    NSArray *stations = [[WLRadioStationStore sharedStore] allStations];
    WLRadioStation *station = [stations objectAtIndex:index->value];
    
    ret = new_weblink_RadioStation();
    
    ret->id = index->value;
    ret->title = new_stringa([[station title] UTF8String]);
    ret->genre = new_stringa([[station genre] UTF8String]);
    ret->url = new_stringa([[station url] UTF8String]);
    ret->thumb = new_stringa("");
    
    return ret;
}

void* weblink_radio_station_select_impl(void* thisx, etch_int32* radio_station_id)
{
    void* ret = NULL;
    
    ETCH_LOG(LOG_CATEGORY, ETCH_LOG_DEBUG, "weblink_radio_station_select_impl(%d)", radio_station_id->value);
      
    [_delegate radioImplDidSelectStation:radio_station_id->value];
    
    etch_object_destroy(radio_station_id);
    
    return ret;
}
