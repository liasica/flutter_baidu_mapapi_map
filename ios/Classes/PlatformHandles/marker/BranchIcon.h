//
//  BranchIcon.h
//  auroraride
//
//  Created by liasica on 2023/12/29.
//  Copyright © 2023 auroraride. All rights reserved.
//
//  Generated by PaintCode
//  http://www.paintcodeapp.com
//

#import <UIKit/UIKit.h>

static CGSize const DESIGISIZE = {90, 104};

typedef NSString *BatteryModel NS_STRING_ENUM;

FOUNDATION_EXPORT BatteryModel const BatteryModel72V;
FOUNDATION_EXPORT BatteryModel const BatteryModel60V;

typedef NS_ENUM(NSInteger, BranchIconResizingBehavior)
{
    BranchIconResizingBehaviorAspectFit, //!< The content is proportionally resized to fit into the target rectangle.
    BranchIconResizingBehaviorAspectFill, //!< The content is proportionally resized to completely fill the target rectangle.
    BranchIconResizingBehaviorStretch, //!< The content is stretched to match the entire target rectangle.
    BranchIconResizingBehaviorCenter, //!< The content is centered in the target rectangle, but it is NOT resized.

};

extern CGRect BranchIconResizingBehaviorApply(BranchIconResizingBehavior behavior, CGRect rect, CGRect target);


@interface BranchIcon : NSObject

// Drawing Methods
+ (UIImage *) draw:(int)number scale:(double)scale batteryModel:(NSString *)batteryModel;
+ (void)drawWithFrame: (CGRect)targetFrame number:(int)number batteryModel:(NSString *)batteryModel resizing: (BranchIconResizingBehavior)resizing;

@end
