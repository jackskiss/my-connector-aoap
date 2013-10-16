//
//  WLRadioViewController.m
//  WLRadio
//
//  Created by 강신규 on 13. 9. 23..
//  Copyright (c) 2013년 강신규. All rights reserved.
//

#import "WLRadioViewController.h"
#import "WLRadioPlayer.h"
#import "WLRadioStationStore.h"
#import "WLRadioStation.h"

#import <ifaddrs.h>
#import <arpa/inet.h>


@interface WLRadioViewController ()

- (void)updateWifiAddress;
- (void)doRemoteImplDidControl:(NSNumber *)control;
- (void)doRadioImplDidSelectStation:(NSNumber *)stationId;

@end

@implementation WLRadioViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    [self updateWifiAddress];
    
    _radio = [[WLRadioPlayer alloc] init];
    [_radio setDeletate:self];
    
    RemoteImplDelegateSet(self);
    RadioImplDelegateSet(self);
    
    // experimetal
    _netService = [[NSNetService alloc] initWithDomain: @"local."
                                                  type: @"_weblink_radio._tcp."
                                                  name: @"WebLink Radio"
                                                  port: 4001];
    _netService.delegate = self;
    [_netService publish];
}

// experimental : NetService
- (void) netService: (NSNetService *) sender
      didNotPublish: (NSDictionary *) errorDict {
    
    NSLog (@"failed to publish net service: %@", errorDict);
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[[WLRadioStationStore sharedStore] allStations] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"RadioStationCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    WLRadioStation *station = [[[WLRadioStationStore sharedStore] allStations] objectAtIndex:indexPath.row];
    
    NSString * titleText = [NSString stringWithFormat:@"%@ (%@)", [station title], [station genre]];
    [[cell textLabel] setText:titleText];
    [[cell detailTextLabel] setText:[station url]];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *log = [NSString stringWithFormat:@"sec: %ld", (long)indexPath.row];
    [[self debugLabel] setText:log];
}

- (IBAction)playOrStop:(id)sender
{
    if ([sender isSelected]) {
        [sender setSelected:NO];
        [_radio pause];
    } else {
        [sender setSelected:YES];
        
        NSIndexPath *indexPath = [[self tableView] indexPathForSelectedRow];
        WLRadioStation *station = [[[WLRadioStationStore sharedStore] allStations] objectAtIndex:indexPath.row];

        [_radio connect:[station url] withGain:0.5];
    }
}

-(void)updateWifiAddress
{
    NSString *address = @"Wi-Fi not connected";
    struct ifaddrs *interfaces = NULL;
    struct ifaddrs *temp_addr = NULL;
    int success = 0;
    
    // retrieve the current interfaces - returns 0 on success
    success = getifaddrs(&interfaces);
    if (success == 0) {
        // Loop through linked list of interfaces
        temp_addr = interfaces;
        while(temp_addr != NULL) {
            if(temp_addr->ifa_addr->sa_family == AF_INET) {
                // Check if interface is en0 which is the wifi connection on the iPhone
                if([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en0"]) {
                    // Get NSString from C String
                    address = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr)];
                    break;
                }
            }
            temp_addr = temp_addr->ifa_next;
        }
        
        [[self wifiAddressLabel] setText:address];
    }
    
    // Free memory
    freeifaddrs(interfaces);
}

// WebLink Delegate

- (void)remoteImplDidControl:(WLRemoteControlType)control
{
    NSNumber *n = [NSNumber numberWithInt:control];
    [self performSelectorOnMainThread:@selector(doRemoteImplDidControl:) withObject:n waitUntilDone:NO];
}

- (void)doRemoteImplDidControl:(NSNumber *)control
{
    switch ([control intValue]) {
        case WLRemoteControlTypeVolumeUp:
            
            break;
            
        case WLRemoteControlTypeVolumeDown:
            break;

        case WLRemoteControlTypePlay:
        {
            [[self playOrStopButton] setSelected:NO];
            
            NSIndexPath *indexPath = [[self tableView] indexPathForSelectedRow];
            WLRadioStation *station = [[[WLRadioStationStore sharedStore] allStations] objectAtIndex:indexPath.row];
            
            [_radio connect:[station url] withGain:0.5];
        }
            break;
            
        case WLRemoteControlTypePause:
            [[self playOrStopButton] setSelected:NO];
            [_radio pause];
            break;
            
        case WLRemoteControlTypeNext:
        {
            NSInteger rowCount = [[self tableView] numberOfRowsInSection:0];
            NSInteger rowSelected = [[[self tableView] indexPathForSelectedRow] row];
            if ((rowSelected + 1) < rowCount) {
                NSIndexPath *rowToSelect = [NSIndexPath indexPathForRow:(rowSelected + 1) inSection:0];
                [[self tableView] selectRowAtIndexPath:rowToSelect animated:YES scrollPosition:UITableViewScrollPositionNone];
            }
        }
            break;
            
        case WLRemoteControlTypePrev:
        {
            NSInteger rowSelected = [[[self tableView] indexPathForSelectedRow] row];
            if ((rowSelected - 1) >= 0) {
                NSIndexPath *rowToSelect = [NSIndexPath indexPathForRow:(rowSelected - 1) inSection:0];
                [[self tableView] selectRowAtIndexPath:rowToSelect animated:YES scrollPosition:UITableViewScrollPositionNone];
            }
        }
            break;
            
        default:
            break;
    }
}

- (void)radioImplDidSelectStation:(int)stationId;
{
    NSNumber *n = [NSNumber numberWithInt:stationId];
    [self performSelectorOnMainThread:@selector(doRadioImplDidSelectStation:) withObject:n waitUntilDone:NO];
}

- (void)doRadioImplDidSelectStation:(NSNumber *)stationId
{
    // FIXME
    // currently stationId is the same with index of row
    // need to change to more reasonable one
    
    NSIndexPath *rowToSelect = [NSIndexPath indexPathForRow:[stationId intValue] inSection:0];
    [[self tableView] selectRowAtIndexPath:rowToSelect animated:YES scrollPosition:UITableViewScrollPositionNone];
}

// Radio 

- (void)loadMainView
{
    
}

- (void)updateTitle:(NSString*)title
{
    [[self debugLabel] setText:title];
}

- (void)updateGain:(float)gain
{
    
}

- (void)updatePlay:(BOOL)play
{
    
}

- (void)updateBuffering: (BOOL)buffering
{
    
}

@end
