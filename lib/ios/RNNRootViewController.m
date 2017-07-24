
#import "RNNRootViewController.h"
#import "RNNNavigationController.h"

@interface RNNRootViewController()
@property NSString* containerId;
@property NSString* containerName;
@property RNNEventEmitter *eventEmitter;
@property NSUInteger supportedOrientations;
@end

@implementation RNNRootViewController

-(instancetype)initWithNode:(RNNLayoutNode*)node rootViewCreator:(id<RNNRootViewCreator>)creator eventEmitter:(RNNEventEmitter*)eventEmitter {
	self = [super init];
	self.containerId = node.nodeId;
	self.containerName = node.data[@"name"];
	self.eventEmitter = eventEmitter;
	self.supportedOrientations = node.supportedOrientations;
	self.view = [creator createRootView:self.containerName rootViewId:self.containerId];
	
	[[NSNotificationCenter defaultCenter] addObserver:self
											 selector:@selector(onJsReload)
												 name:RCTJavaScriptWillStartLoadingNotification
											   object:nil];
	
	self.navigationItem.title = node.data[@"navigationOptions"][@"title"];
	
	return self;
}

-(void)viewDidAppear:(BOOL)animated {
	[super viewDidAppear:animated];
	[self.eventEmitter sendContainerStart:self.containerId];
}

-(void)viewDidDisappear:(BOOL)animated {
	[super viewDidDisappear:animated];
	[self.eventEmitter sendContainerStop:self.containerId];
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
	return self.supportedOrientations;
}

/**
 *	fix for #877, #878
 */
-(void)onJsReload {
	[self cleanReactLeftovers];
}

/**
 * fix for #880
 */
-(void)dealloc {
	[self cleanReactLeftovers];
}

-(void)cleanReactLeftovers {
	[[NSNotificationCenter defaultCenter] removeObserver:self];
	[[NSNotificationCenter defaultCenter] removeObserver:self.view];
	self.view = nil;
}

@end
