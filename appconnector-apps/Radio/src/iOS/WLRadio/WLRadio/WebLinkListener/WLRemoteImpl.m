//
//  RemoteControlmpl.cpp
//  WebLinkRadio
//
//  Created by 강신규 on 13. 9. 12..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#include "WLRemotelmpl.h"
#include "etch_log.h"

#import "WLRemoteImplDelegate.h"

static const char* LOG_CATEGORY =  "WLRemote";

// FIXME
static __weak id<WLRemoteImplDelegate> _delegate;

void RemoteImplDelegateSet(__weak id<WLRemoteImplDelegate> delegate)
{
    _delegate = delegate;
}

void* weblink_remote_control_impl(void* thisx, weblink_RemoteControl* control)
{
    void* ret = NULL;
   
    ETCH_LOG(LOG_CATEGORY, ETCH_LOG_DEBUG, "weblink_remote_control_impl (%d)", control->value);
    
    switch (control->value) {
        case RemoteControl_VOLUME_UP:
            [_delegate remoteImplDidControl:WLRemoteControlTypeVolumeUp];
            break;
        case RemoteControl_VOLUME_DOWN:
            [_delegate remoteImplDidControl:WLRemoteControlTypeVolumeDown];
            break;
        case RemoteControl_PLAY:
            [_delegate remoteImplDidControl:WLRemoteControlTypePlay];
            break;
        case RemoteControl_PAUSE:
            [_delegate remoteImplDidControl:WLRemoteControlTypePause];
            break;
        case RemoteControl_NEXT:
            [_delegate remoteImplDidControl:WLRemoteControlTypeNext];
            break;
        case RemoteControl_PREV:
            [_delegate remoteImplDidControl:WLRemoteControlTypePrev];
            break;
        default:
            ETCH_LOG(LOG_CATEGORY, ETCH_LOG_ERROR, "weblink_remote_control_impl: UNKNOWN (%d)", control->value);
            break;
    }
    
    etch_object_destroy(control);
    
    return ret;
}
