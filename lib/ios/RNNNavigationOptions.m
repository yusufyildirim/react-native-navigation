#import "RNNNavigationOptions.h"
#import <React/RCTConvert.h>

@implementation RNNNavigationOptions


-(instancetype)initWithDict:(NSDictionary *)navigationOptions {
	self = [super init];
	self.topBarBackgroundColor = [navigationOptions objectForKey:@"topBarBackgroundColor"];
	self.statusBarHidden = [navigationOptions objectForKey:@"statusBarHidden"];
	self.title = [navigationOptions objectForKey:@"title"];
	self.topBarTextColor = [navigationOptions objectForKey:@"topBarTextColor"];
	self.screenBackgroundColor = [navigationOptions objectForKey:@"screenBackgroundColor"];
	self.topBarTextFontFamily = [navigationOptions objectForKey:@"topBarTextFontFamily"];
	self.topBarHidden = [navigationOptions objectForKey:@"topBarHidden"];
	return self;
}

-(void)setOptionsDynamically:(NSDictionary *)dynamicOptions {
	for(id key in dynamicOptions) {
		[self setValue:[dynamicOptions objectForKey:key] forKey:key];
	}
}

-(void)apply:(UIViewController*)viewController{
	if (self.topBarBackgroundColor) {
		UIColor* backgroundColor = [RCTConvert UIColor:self.topBarBackgroundColor];
		viewController.navigationController.navigationBar.barTintColor = backgroundColor;
	} else {
		viewController.navigationController.navigationBar.barTintColor = nil;
	}
	
	if (self.title) {
		viewController.navigationItem.title = self.title;
	}
	
	if (self.topBarTextColor) {
		UIColor* textColor = [RCTConvert UIColor:self.topBarTextColor];
		NSMutableDictionary* navigationBarTitleTextAttributes = [NSMutableDictionary dictionaryWithDictionary:@{NSForegroundColorAttributeName: textColor}];
		if (self.topBarTextFontFamily) {
			[navigationBarTitleTextAttributes addEntriesFromDictionary:@{NSFontAttributeName: [UIFont fontWithName:self.topBarTextFontFamily size:20]}];
		}
		viewController.navigationController.navigationBar.titleTextAttributes = navigationBarTitleTextAttributes;
	} else if (self.topBarTextFontFamily){
		viewController.navigationController.navigationBar.titleTextAttributes = @{NSFontAttributeName: [UIFont fontWithName:self.topBarTextFontFamily size:20]};
	}
	
	if (self.screenBackgroundColor) {
		UIColor* screenColor = [RCTConvert UIColor:self.screenBackgroundColor];
		viewController.view.backgroundColor = screenColor;
	}
	
	if (self.topBarHidden){
		if ([self.topBarHidden boolValue]) {
			[viewController.navigationController setNavigationBarHidden:YES animated:YES];
		} else {
			[viewController.navigationController setNavigationBarHidden:NO animated:YES];
		}
	}
}



@end
