#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface RNNNavigationOptions : NSObject

@property (nonatomic, strong) NSNumber* topBarBackgroundColor;
@property (nonatomic, strong) NSNumber* topBarTextColor;
@property (nonatomic, strong) NSNumber* statusBarHidden;
@property (nonatomic, strong) NSString* title;
@property (nonatomic, strong) NSNumber* screenBackgroundColor;
@property (nonatomic, strong) NSString* topBarTextFontFamily;
@property (nonatomic, strong) NSNumber* topBarHidden;
@property (nonatomic, strong) NSNumber* topBarHideOnScroll;
@property (nonatomic, strong) NSNumber* topBarButtonColor;
<<<<<<< HEAD
@property (nonatomic, strong) NSNumber* topBarTranslucent;
=======
@property (nonatomic, strong) NSString* setTabBadge;
>>>>>>> bc6234ecc511aac84ef67cd65b0b87d76eec4d44


-(instancetype)init;
-(instancetype)initWithDict:(NSDictionary *)navigationOptions;

-(void)applyOn:(UIViewController*)viewController;
-(void)mergeWith:(NSDictionary*)otherOptions;

@end

