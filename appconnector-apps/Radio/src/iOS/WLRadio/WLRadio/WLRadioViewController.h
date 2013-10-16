//
//  WLRadioViewController.h
//  WLRadio
//
//  Created by 강신규 on 13. 9. 23..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "WLRadioImplDelegate.h"
#import "WLRemoteImplDelegate.h"

@class WLRadioPlayer;
@class WLRadioStation;

@interface WLRadioViewController : UIViewController <UITableViewDelegate, WLRadioImplDelegate, WLRemoteImplDelegate, NSNetServiceDelegate>
{
    WLRadioPlayer *_radio;
    WLRadioStation *_playingStation;
    NSNetService *_netService;
}

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIButton *playOrStopButton;
@property (weak, nonatomic) IBOutlet UILabel *wifiAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *debugLabel;

- (IBAction)playOrStop:(id)sender;

- (void)loadMainView;
- (void)updateTitle:(NSString*)title;
- (void)updateGain:(float)gain;
- (void)updatePlay:(BOOL)play;
- (void)updateBuffering: (BOOL)buffering;

@end
