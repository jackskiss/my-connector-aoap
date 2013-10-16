//
//  WLRemoteDelegate.h
//  WLRadio
//
//  Created by 강신규 on 13. 9. 25..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum WLRemoteControlType {
    WLRemoteControlTypeVolumeUp,
    WLRemoteControlTypeVolumeDown,
    WLRemoteControlTypePlay,
    WLRemoteControlTypePause,
    WLRemoteControlTypeNext,
    WLRemoteControlTypePrev,
} WLRemoteControlType;

@protocol WLRemoteImplDelegate <NSObject>

- (void)remoteImplDidControl:(WLRemoteControlType)contol;

@end

// FIXME
void RemoteImplDelegateSet(__weak id<WLRemoteImplDelegate> delegate);
