
#import "TimeElapsedBetweenPCLAssessmentsEvent.h"

extern NSDateFormatter *isoFormatter;

@implementation TimeElapsedBetweenPCLAssessmentsEvent

@synthesize timeElapsedBetweenPCLAssessments;

- (id)init {
    self = [super init];
    if (self) {
    	eventID = 19;
    }
    
    return self;
}

+ (void)logWithTimeElapsedBetweenPCLAssessments:(long long)timeElapsedBetweenPCLAssessments {
	msgpack_packer *pk = [[EventLog logger] startEvent];
	if (!pk) return;
    msgpack_pack_array(pk, 3);
    msgpack_pack_int(pk, 19);
    msgpack_pack_long_long(pk, [[NSDate date] timeIntervalSince1970] * 1000LL);
    msgpack_pack_long_long(pk, timeElapsedBetweenPCLAssessments);
	[[EventLog logger] endEvent];
}

- (void)pack:(msgpack_packer*)pk {
    msgpack_pack_array(pk, 3);
    msgpack_pack_int(pk, eventID);
    msgpack_pack_long_long(pk, timestamp);
    msgpack_pack_long_long(pk, timeElapsedBetweenPCLAssessments);
}

- (void)unpack:(msgpack_object_array*)array {
	[super unpack:array];

    msgpack_object *obj;

    obj = &array->ptr[2];
	timeElapsedBetweenPCLAssessments = obj->via.i64;
}

- (NSString*)ohmageSurveyID {
    return @"timeElapsedBetweenPCLAssessmentsProbe";
}

- (void)addAttributesForOhmageJSON:(NSMutableArray*)list {
	NSMutableDictionary *dict;

    dict = [[NSMutableDictionary alloc] init];
    [dict setObject:@"timeElapsedBetweenPCLAssessments" forKey:@"prompt_id"];
	[dict setObject:(timeElapsedBetweenPCLAssessments==-1 ? @"NOT_DISPLAYED" : [NSNumber numberWithLongLong:timeElapsedBetweenPCLAssessments]) forKey:@"value"];
	[list addObject:dict];
	[dict release];
	
}

- (void)dealloc {
	[super dealloc];
}

@end
