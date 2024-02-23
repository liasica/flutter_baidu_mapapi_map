//
//  BranchIcon.m
//  auroraride
//
//  Created by liasica on 2023/12/29.
//  Copyright © 2023 auroraride. All rights reserved.
//
//  Generated by PaintCode
//  http://www.paintcodeapp.com
//

#import "BranchIcon.h"


@implementation BranchIcon

/// 72V电池
NSString * const BatteryModel72V = @"72V";

/// 60V电池
NSString * const BatteryModel60V = @"60V";

#pragma mark Initialization

+ (void)initialize
{
}

#pragma mark Drawing Methods

/// 参考
/// 添加自定义字体 https://codewithchris.com/common-mistakes-with-adding-custom-fonts-to-your-ios-app/
/// https://stackoverflow.com/questions/35919795/objective-c-draw-an-image
/// https://www.jianshu.com/p/77a5dbed7da2
/// https://stackoverflow.com/a/5866619/4160831
/// https://stackoverflow.com/a/6823536/4160831
/// https://stackoverflow.com/a/9829205/4160831
/// Core Graphic 使用 https://tbfungeek.github.io/2019/08/06/Core-graphic-%E4%BD%BF%E7%94%A8/index.html
/// https://juejin.cn/post/6894530597851496455
/// https://www.cnblogs.com/HypeCheng/articles/4159049.html
/// https://furnacedigital.blogspot.com/2010/12/quartz-2d.html
+ (UIImage *) draw:(int)number scale:(double)scale batteryModel:(NSString *)batteryModel
{
    double width = DESIGISIZE.width * scale;
    double height = DESIGISIZE.height * scale;
    
    CGSize size = CGSizeMake(width, height);
    
    // 1.开启图形上下文，并将ImageContext放置到栈顶
    UIGraphicsBeginImageContextWithOptions(size, NO, 0);
    
    // 2.绘制图形
    [self drawWithFrame: CGRectMake(0, 0, width, height) number:number batteryModel:batteryModel resizing: BranchIconResizingBehaviorStretch];
    
    // 3.从上下文中获取图片
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    
    // 4.关闭图形上下文
    UIGraphicsEndImageContext();
    
    return image;
}

+ (void)drawWithFrame: (CGRect)targetFrame number:(int)number batteryModel:(NSString *)batteryModel resizing: (BranchIconResizingBehavior)resizing
{
    //// General Declarations
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    //// Resize to Target Frame
    CGContextSaveGState(context);
    CGRect resizedFrame = BranchIconResizingBehaviorApply(resizing, CGRectMake(0, 0, 90, 104), targetFrame);
    CGContextTranslateCTM(context, resizedFrame.origin.x, resizedFrame.origin.y);
    CGContextScaleCTM(context, resizedFrame.size.width / 90, resizedFrame.size.height / 104);
    CGFloat resizedShadowScale = MIN(resizedFrame.size.width / 90, resizedFrame.size.height / 104);
    
    //// Color Declarations
    UIColor* wrapperColor = [UIColor colorWithRed: 1 green: 1 blue: 1 alpha: 1];
    UIColor* v72Color = [UIColor colorWithRed: 0 green: 0.875 blue: 0.792 alpha: 1];
    UIColor* v60Color = [UIColor colorWithRed: 0 green: 0.749 blue: 1 alpha: 1];
    UIColor* ovalColor = [UIColor colorWithRed: 0.18 green: 0.208 blue: 0.247 alpha: 1];
    UIColor* shadowColor = [UIColor colorWithRed: 0.063 green: 0.114 blue: 0.204 alpha: 0.063];
    
    UIColor* batteryColor = v60Color;
    if ([[batteryModel uppercaseString] isEqual:BatteryModel72V]) {
        batteryColor = v72Color;
    }
    
    //// Shadow Declarations
    NSShadow* shadow = [[NSShadow alloc] init];
    shadow.shadowColor = shadowColor;
    shadow.shadowOffset = CGSizeMake(0, 4);
    shadow.shadowBlurRadius = 8;
    
    //// IconGroup
    {
        //// Bezier Drawing
        UIBezierPath* bezierPath = [UIBezierPath bezierPath];
        [bezierPath moveToPoint: CGPointMake(71.16, 14.68)];
        [bezierPath addCurveToPoint: CGPointMake(18.84, 14.68) controlPoint1: CGPointMake(56.72, 0.44) controlPoint2: CGPointMake(33.28, 0.44)];
        [bezierPath addCurveToPoint: CGPointMake(12.88, 58.56) controlPoint1: CGPointMake(6.77, 26.57) controlPoint2: CGPointMake(4.78, 44.64)];
        [bezierPath addCurveToPoint: CGPointMake(18.84, 66.23) controlPoint1: CGPointMake(14.47, 61.3) controlPoint2: CGPointMake(16.46, 63.88)];
        [bezierPath addLineToPoint: CGPointMake(45, 92)];
        [bezierPath addLineToPoint: CGPointMake(71.16, 66.23)];
        [bezierPath addCurveToPoint: CGPointMake(77.12, 58.56) controlPoint1: CGPointMake(73.54, 63.88) controlPoint2: CGPointMake(75.53, 61.3)];
        [bezierPath addCurveToPoint: CGPointMake(71.16, 14.68) controlPoint1: CGPointMake(85.22, 44.64) controlPoint2: CGPointMake(83.23, 26.57)];
        [bezierPath closePath];
        CGContextSaveGState(context);
        CGContextSetShadowWithColor(context,
                                    CGSizeMake(shadow.shadowOffset.width * resizedShadowScale, shadow.shadowOffset.height * resizedShadowScale),
                                    shadow.shadowBlurRadius * resizedShadowScale,
                                    [shadow.shadowColor CGColor]);
        [wrapperColor setFill];
        [bezierPath fill];
        CGContextRestoreGState(context);
        
        
        
        //// Circle Drawing
        UIBezierPath* circlePath = [UIBezierPath bezierPathWithOvalInRect: CGRectMake(12, 8, 66, 66)];
        [batteryColor setFill];
        [circlePath fill];
        
        
        //// Oval Drawing
        UIBezierPath* ovalPath = [UIBezierPath bezierPath];
        [ovalPath moveToPoint: CGPointMake(75.16, 54.4)];
        [ovalPath addCurveToPoint: CGPointMake(45, 74) controlPoint1: CGPointMake(70.02, 65.95) controlPoint2: CGPointMake(58.45, 74)];
        [ovalPath addCurveToPoint: CGPointMake(14.84, 54.4) controlPoint1: CGPointMake(31.55, 74) controlPoint2: CGPointMake(19.98, 65.95)];
        [ovalPath addCurveToPoint: CGPointMake(45, 50) controlPoint1: CGPointMake(23.54, 51.62) controlPoint2: CGPointMake(33.89, 50)];
        [ovalPath addCurveToPoint: CGPointMake(75.16, 54.4) controlPoint1: CGPointMake(56.11, 50) controlPoint2: CGPointMake(66.46, 51.62)];
        [ovalPath closePath];
        [ovalColor setFill];
        [ovalPath fill];
    }
    
    //// Battery Model Drawing
    CGRect batteryModelRect = CGRectMake(27, 56, 36, 14);
    {
        NSString* textContent = batteryModel;
        NSMutableParagraphStyle* batteryModelStyle = [[NSMutableParagraphStyle alloc] init];
        batteryModelStyle.alignment = NSTextAlignmentCenter;
        NSDictionary* batteryModelFontAttributes = @{NSFontAttributeName: [UIFont fontWithName: @"LucidaGrande" size: 16], NSForegroundColorAttributeName: UIColor.whiteColor, NSParagraphStyleAttributeName: batteryModelStyle};
        
        CGFloat batteryModelTextHeight = [textContent boundingRectWithSize: CGSizeMake(batteryModelRect.size.width, INFINITY) options: NSStringDrawingUsesLineFragmentOrigin attributes: batteryModelFontAttributes context: nil].size.height;
        CGContextSaveGState(context);
        CGContextClipToRect(context, batteryModelRect);
        [textContent drawInRect: CGRectMake(CGRectGetMinX(batteryModelRect), CGRectGetMinY(batteryModelRect) + (batteryModelRect.size.height - batteryModelTextHeight) / 2, batteryModelRect.size.width, batteryModelTextHeight) withAttributes: batteryModelFontAttributes];
        CGContextRestoreGState(context);
    }
    
    
    //// Number Drawing
    CGRect numberRect = CGRectMake(12, 14, 66, 36);
    {
        NSString* textContent = [NSString stringWithFormat:@"%d", number];
        NSMutableParagraphStyle* numberStyle = [[NSMutableParagraphStyle alloc] init];
        numberStyle.alignment = NSTextAlignmentCenter;
        NSDictionary* numberFontAttributes = @{NSFontAttributeName: [UIFont fontWithName: @"LucidaGrande" size: 30], NSForegroundColorAttributeName: UIColor.whiteColor, NSParagraphStyleAttributeName: numberStyle};
        
        CGFloat numberTextHeight = [textContent boundingRectWithSize: CGSizeMake(numberRect.size.width, INFINITY) options: NSStringDrawingUsesLineFragmentOrigin attributes: numberFontAttributes context: nil].size.height;
        CGContextSaveGState(context);
        CGContextClipToRect(context, numberRect);
        [textContent drawInRect: CGRectMake(CGRectGetMinX(numberRect), CGRectGetMinY(numberRect) + (numberRect.size.height - numberTextHeight) / 2, numberRect.size.width, numberTextHeight) withAttributes: numberFontAttributes];
        CGContextRestoreGState(context);
    }
    
    CGContextRestoreGState(context);
    
}

@end



CGRect BranchIconResizingBehaviorApply(BranchIconResizingBehavior behavior, CGRect rect, CGRect target)
{
    if (CGRectEqualToRect(rect, target) || CGRectEqualToRect(target, CGRectZero))
        return rect;
    
    CGSize scales = CGSizeZero;
    scales.width = ABS(target.size.width / rect.size.width);
    scales.height = ABS(target.size.height / rect.size.height);
    
    switch (behavior)
    {
        case BranchIconResizingBehaviorAspectFit:
        {
            scales.width = MIN(scales.width, scales.height);
            scales.height = scales.width;
            break;
        }
        case BranchIconResizingBehaviorAspectFill:
        {
            scales.width = MAX(scales.width, scales.height);
            scales.height = scales.width;
            break;
        }
        case BranchIconResizingBehaviorStretch:
            break;
        case BranchIconResizingBehaviorCenter:
        {
            scales.width = 1;
            scales.height = 1;
            break;
        }
    }
    
    CGRect result = CGRectStandardize(rect);
    result.size.width *= scales.width;
    result.size.height *= scales.height;
    result.origin.x = target.origin.x + (target.size.width - result.size.width) / 2;
    result.origin.y = target.origin.y + (target.size.height - result.size.height) / 2;
    return result;
}
