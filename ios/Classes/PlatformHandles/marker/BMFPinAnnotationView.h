//
//  BMFPinAnnotationView.h
//  flutter_baidu_mapapi_map
//
//  Created by liasica on 2023/12/27.
//

#import <BaiduMapAPI_Map/BMKMapComponent.h>

NS_ASSUME_NONNULL_BEGIN

/// 继承大头针，实现旋转效果
@interface BMFPinAnnotationView : BMKPinAnnotationView

{
@private 
    /// 旋转角度
    double _rotation;
}

/// 获取旋转角度
-(double)rotation;

/// 设置旋转
-(void)setRotation:(double)rotation;

/// 原生设置锚点
-(void)setAnchorPoint:(CGPoint)anchorPoint rotation:(double)rotation forView:(UIView *)view;

@end

NS_ASSUME_NONNULL_END
