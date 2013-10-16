//
//  WLRadioDelegate.h
//  WLRadio
//
//  Created by 강신규 on 13. 9. 25..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol WLRadioImplDelegate <NSObject>

- (void)radioImplDidSelectStation:(int)stationId;

@end

// FIXME
void RadioImplDelegateSet(__weak id<WLRadioImplDelegate> delegate);