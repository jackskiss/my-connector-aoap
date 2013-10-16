//
//  WLRadioStationStore.h
//  WLRadio
//
//  Created by 강신규 on 13. 9. 23..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#import <Foundation/Foundation.h>

@class WLRadioStation;

@interface WLRadioStationStore : NSObject
{
    NSMutableArray *_allStations;
}

+ (WLRadioStationStore *)sharedStore;

- (NSArray *)allStations;

@end
