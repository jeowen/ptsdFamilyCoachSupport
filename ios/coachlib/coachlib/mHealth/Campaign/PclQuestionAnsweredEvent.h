//
//  Autogenerated... do not edit
//

#import "EventLog.h"
#import "EventRecord.h"

@interface PclQuestionAnsweredEvent : EventRecord {
	long long pclNumberOfQuestionsAnswered;
}

+ (void)logWithPclNumberOfQuestionsAnswered:(long long)pclNumberOfQuestionsAnswered;

@property (nonatomic, assign) long long pclNumberOfQuestionsAnswered;

@end