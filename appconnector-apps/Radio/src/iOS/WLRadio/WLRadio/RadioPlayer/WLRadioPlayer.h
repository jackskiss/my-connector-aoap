#import <UIKit/UIKit.h>
#include <AudioToolbox/AudioToolbox.h>
#include <AudioToolbox/AudioFileStream.h>
#include <AudioToolbox/AudioServices.h>
#include "WLRadioPacket.h"
#include "WLRadioQueue.h"
#include "WLRadioViewController.h"

typedef struct {
    AudioFileStreamID             streamID;
    AudioStreamBasicDescription   mDataFormat;
    AudioQueueRef                 mQueue;
	__unsafe_unretained NSMutableData				  *currentAudio;
    AudioQueueBufferRef           mBuffers[6];
	BOOL						  started;
	BOOL						  paused;
	BOOL						  buffering;
	__unsafe_unretained WLRadioQueue						  *packetQueue;
	int							  totalBytes;
	AudioStreamPacketDescription  descriptions[512];
	AudioQueueBufferRef			  freeBuffers[6];
	float						  currentGain;
	int							  outOfBuffers;
} AQPlayerState;

@class WLRadioViewController;

@interface WLRadioPlayer : NSObject {
	NSString *url;
	NSURLConnection *conn;
	NSMutableData *currentPacket;
	NSString *title;
	NSMutableData *metaData;
	NSDictionary *streamHeaders;
	int icyInterval;
	int metaLength;
	int streamCount;
	int alreadyLoaded;
	BOOL buffering;
	BOOL playing;
	int attemptCount;
	UIAlertView *alert;
	WLRadioViewController *delegate;
	AQPlayerState audioState;
}

//+ (WLRadioPlayer *)sharedPlayer;

- (void)setDeletate:(WLRadioViewController *) viewController;

- (BOOL)connect:(NSString *)loc withGain:(float)gain;
- (void)updateGain:(float)value;
- (void)updatePlay:(BOOL)play;
- (void)pause;
- (void)resume;

@end
