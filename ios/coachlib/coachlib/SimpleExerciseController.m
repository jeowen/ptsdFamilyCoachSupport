//
//  SimpleExerciseController.m
//  iStressLess
//


//

#import "SimpleExerciseController.h"
#import "ManageSymptomsNavController.h"
#import "heartbeat.h"

@implementation SimpleExerciseController

-(void)configureFromContent {
    [super configureFromContent];
    NSString *content = [[self content] valueForKey:@"displayName"];
    [heartbeat
     logEvent:@"ExerciseBegin"
     withParameters:[NSDictionary dictionaryWithObjectsAndKeys:content, @"value", nil]];
}

@end
