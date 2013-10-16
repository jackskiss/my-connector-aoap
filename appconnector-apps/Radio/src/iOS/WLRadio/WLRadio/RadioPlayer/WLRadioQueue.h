#import <UIKit/UIKit.h>


@interface WLRadioQueue : NSObject
{
	NSMutableArray *_itemArray;
}

- (id)returnAndRemoveOldest;
- (void)addItem:(id)item;
- (int)size;
- (void)empty;
- (id)peak;

@end
