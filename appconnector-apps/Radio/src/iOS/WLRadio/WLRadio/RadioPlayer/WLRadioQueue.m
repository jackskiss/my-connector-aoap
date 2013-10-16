#import "WLRadioQueue.h"


@implementation WLRadioQueue

-(id) init
{
	self = [super init];
    if (self)
		_itemArray = [NSMutableArray arrayWithCapacity:0];
	
	return self;
}

-(id)returnAndRemoveOldest
{
	id anObject = nil;
	
	anObject = [_itemArray lastObject];
	if (anObject)
	{
//		[anObject retain];
		[_itemArray removeLastObject];
	}
	
	return anObject;  
}

-(id)peak
{
	return [_itemArray lastObject];
}

-(void)addItem:(id)anItem
{
	[_itemArray insertObject:anItem atIndex:0];
}

-(int)size
{
	return [_itemArray count];
}

-(void)empty
{
	[_itemArray removeAllObjects];
}

@end
