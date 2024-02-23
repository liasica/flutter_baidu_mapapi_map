//
//  BMFPinAnnotationView.m
//  flutter_baidu_mapapi_map
//
//  Created by liasica on 2023/12/27.
//

#import "BMFPinAnnotationView.h"

@implementation BMFPinAnnotationView

-(double)rotation {
    return _rotation;
}

/// 旋转
/// TODO: <优化>是否需要使用UIView wrap？
/// 因为百度地图SDK设置了centerOffset导致只能通过view.centerOffset去进行转换，无法使用原生转换（会被覆盖）
/// 因此此处使用transition
-(void)setRotation:(double)rotation {
    // 中心点
    float cx = self.frame.size.width * 0.5;
    float cy = self.frame.size.height * 0.5;
    
    // 原坐标点
    float x1 = cx - super.centerOffset.x;
    float y1 = cy - super.centerOffset.y;
    
    float angle = rotation * M_PI / 180;
    
    // 计算角度旋转偏移
    float x = (x1 - cx) * cos(angle) - (y1 - cy) * sin(angle) + cx;
    float y = (x1 - cx) * sin(angle) + (y1 - cy) * cos(angle) + cy;
    
    // [UIView animateWithDuration:3 animations:^{
    //     view.layer.opacity = 0.6;
    //     CGAffineTransform transform = CGAffineTransformMakeRotation(rotation * M_PI / 180);
    //     transform = CGAffineTransformConcat(transform, CGAffineTransformMakeTranslation(x1 - x, y1 - y));
    //     view.transform = transform;
    // }];
    
    CGAffineTransform transform = CGAffineTransformMakeRotation(rotation * M_PI / 180);
    transform = CGAffineTransformConcat(transform, CGAffineTransformMakeTranslation(x1 - x, y1 - y));
    self.transform = transform;
    _rotation = rotation;
}

/// 原生设置锚点
/// 参考: https://stackoverflow.com/a/26815851/4160831
-(void)setAnchorPoint:(CGPoint)anchorPoint rotation:(double)rotation forView:(UIView *)view {
    view.translatesAutoresizingMaskIntoConstraints = true;
    
    CGPoint newPoint = CGPointMake(view.bounds.size.width * anchorPoint.x,
                                   view.bounds.size.height * anchorPoint.y);
    CGPoint oldPoint = CGPointMake(view.bounds.size.width * view.layer.anchorPoint.x,
                                   view.bounds.size.height * view.layer.anchorPoint.y);

    newPoint = CGPointApplyAffineTransform(newPoint, view.transform);
    oldPoint = CGPointApplyAffineTransform(oldPoint, view.transform);

    CGPoint position = view.layer.position;

    position.x -= oldPoint.x;
    position.x += newPoint.x;

    position.y -= oldPoint.y;
    position.y += newPoint.y;
    
    view.layer.position = position;
    view.layer.anchorPoint = anchorPoint;
    
    // [UIView animateWithDuration:3 animations:^{
    //     view.transform = CGAffineTransformMakeRotation(rotation * M_PI / 180);
    // }];
}

// rotation in radians
UIImage *rotatedImage(UIImage *image, double rotation) {
    // Calculate Destination Size
    CGAffineTransform t = CGAffineTransformMakeRotation(rotation * M_PI / 180);
    CGRect sizeRect = (CGRect) {.size = image.size};
    CGRect destRect = CGRectApplyAffineTransform(sizeRect, t);
    CGSize destinationSize = destRect.size;
    
    NSLog(@"图片大小: %f × %f", image.size.width, image.size.height);
        
    // Draw image
    UIGraphicsBeginImageContext(destinationSize);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextTranslateCTM(context, destinationSize.width / 2.0f, destinationSize.height / 2.0f);
    CGContextRotateCTM(context, rotation);
    [image drawInRect:CGRectMake(-image.size.width / 2.0f, -image.size.height / 2.0f, image.size.width, image.size.height)];
    
    // Save image
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}

CGFloat DegreesToRadians(CGFloat degrees) {
    return degrees * M_PI / 180;
}

CGFloat RadiansToDegrees(CGFloat radians) {
    return radians * 180 / M_PI;
}

@end
