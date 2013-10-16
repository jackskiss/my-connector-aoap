//
//  WLRadioStationStore.m
//  WLRadio
//
//  Created by 강신규 on 13. 9. 23..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#import "WLRadioStationStore.h"
#import "WLRadioStation.h"

@implementation WLRadioStationStore

+ (WLRadioStationStore *)sharedStore
{
    static WLRadioStationStore *sharedStore = nil;
    
    if (!sharedStore)
        sharedStore = [[super allocWithZone:nil] init];
    
    return sharedStore;
}

+ (id)allocWithZone:(struct _NSZone *)zone
{
    return [self sharedStore];
}

- (id)init
{
    self = [super init];
    if (self) {
        _allStations = [[NSMutableArray alloc] init];
        
        // load radio stations from RadioStations.json
        NSString *jsonFilePath = [[NSBundle mainBundle] pathForResource:@"RadioStations" ofType:@"json"];
        if (jsonFilePath) {
            NSData *jsonData = [NSData dataWithContentsOfFile:jsonFilePath];
            NSError *jsonParsingError = nil;
            NSArray *stationArray = [NSJSONSerialization JSONObjectWithData:jsonData options:0 error:&jsonParsingError];
            if (stationArray) {
                for (NSDictionary *item in stationArray) {
                    WLRadioStation *station = [[WLRadioStation alloc] init];
                    
                    [station setTitle:[item objectForKey:@"title"]];
                    [station setGenre:[item objectForKey:@"genre"]];
                    [station setUrl:[item objectForKey:@"url"]];
                    
                    [_allStations addObject:station];
                }
            }
        }
    }
    
    return self;
}

- (NSArray *)allStations
{
    return _allStations;
}

@end
