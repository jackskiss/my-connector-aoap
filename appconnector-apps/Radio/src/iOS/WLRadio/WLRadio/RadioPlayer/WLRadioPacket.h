#import <UIKit/UIKit.h>
#include <AudioToolbox/AudioToolbox.h>

@interface WLRadioPacket : NSObject

@property (nonatomic) AudioStreamPacketDescription description;
@property (nonatomic, strong) NSData *data;

@end
