//
//  BMFAnnotationHandles.m
//  flutter_baidu_mapapi_map
//
//  Created by zhangbaojin on 2020/2/11.
//

#import <flutter_baidu_mapapi_base/NSObject+BMFVerify.h>
#import <flutter_baidu_mapapi_base/UIColor+BMFString.h>
#import <flutter_baidu_mapapi_base/BMFMapModels.h>
#import <flutter_baidu_mapapi_base/BMFDefine.h>

#import "BMFAnnotationHandles.h"
#import "BMFMapView.h"
#import "BMFAnnotationMethodConst.h"
#import "BMFFileManager.h"
#import "BMFAnnotation.h"
#import "BMFEdgeInsets.h"

@interface BMFAnnotationHandles ()
{
    NSDictionary<NSString *, NSString *> *_handles;
}
@end
@implementation BMFAnnotationHandles

static BMFAnnotationHandles *_instance = nil;
+ (instancetype)defalutCenter {
    return [[BMFAnnotationHandles alloc] init];
}

+ (instancetype)allocWithZone:(struct _NSZone *)zone {
     @synchronized(self) { // 同步
        if (!_instance) {
            _instance = [super allocWithZone:zone];
        }
    }
    return _instance;
}
 
- (instancetype)copyWithZone:(struct _NSZone *)zone {
    return _instance;
}

- (instancetype)mutableCopyWithZone:(nullable NSZone *)zone {
    return _instance;
}

- (NSDictionary<NSString *, NSString *> *)annotationHandles {
    if (!_handles) {
        _handles = @{
            kBMFMapAddMarkerMethod: NSStringFromClass([BMFAddAnnotation class]),
            kBMFMapAddMarkersMethod: NSStringFromClass([BMFAddAnnotations class]),
            kBMFMapRemoveMarkerMethod: NSStringFromClass([BMFRemoveAnnotation class]),
            kBMFMapRemoveMarkersMethod: NSStringFromClass([BMFRemoveAnnotations class]),
            kBMFMapCleanAllMarkersMethod: NSStringFromClass([BMFCleanAllAnnotations class]),
            kBMFMapShowMarkersMethod: NSStringFromClass([BMFShowAnnotations class]),
            kBMFMapSelectMarkerMethod: NSStringFromClass([BMFSelectAnnotation class]),
            kBMFMapDeselectMarkerMethod: NSStringFromClass([BMFDeselectAnnotation class]),
            kBMFMapUpdateMarkerMemberMethod: NSStringFromClass([BMFUpdateAnnotation class])
        };
    }
    return _handles;
}

@end

#pragma mark - marker

@implementation BMFAddAnnotation

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    BMKPointAnnotation *annotation = [BMKPointAnnotation overlayWithDictionary:call.arguments];
    if (annotation) {
        [_mapView addAnnotation:annotation];
        
        BMKPinAnnotationView *view = (BMKPinAnnotationView *)[_mapView viewForAnnotation:annotation];
        double rotation = [[call.arguments safeObjectForKey:@"rotate"] doubleValue];
        if (rotation > 0) {
            setRotation(view, rotation);
        }
        result(@YES);
    } else {
        result(@NO);
    }
}

@end

@implementation BMFAddAnnotations

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments) {
        result(@NO);
        return;
    }
    
    NSMutableArray *annotations = @[].mutableCopy;
    for (NSDictionary *dic in (NSArray *)call.arguments) {
        BMKPointAnnotation *an = [BMKPointAnnotation overlayWithDictionary:dic];
        [annotations addObject:an];
    }
    // TODO: 批量添加时旋转处理
    [_mapView addAnnotations:annotations];
    result(@YES);
}

@end


@implementation BMFRemoveAnnotation

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments || ![call.arguments safeObjectForKey:@"id"]) {
        result(@NO);
        return;
    }
    NSString *ID = [call.arguments safeObjectForKey:@"id"];
    __weak __typeof__(_mapView) weakMapView = _mapView;
    [_mapView.annotations enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([ID isEqualToString:((BMFAnnotationModel *)((BMKPointAnnotation *) obj).flutterModel).Id]) {
            [weakMapView removeAnnotation:obj];
            result(@YES);
            *stop = YES;
        }
    }];
    
}

@end


@implementation BMFRemoveAnnotations

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments) {
        result(@NO);
        return;
    }
    
    __block NSMutableArray <BMKPointAnnotation*> *annotations = @[].mutableCopy;
    for (NSDictionary *dic in (NSArray *)call.arguments) {
        [_mapView.annotations enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([[dic safeObjectForKey:@"id"] isEqualToString:((BMFAnnotationModel *)((BMKPointAnnotation *) obj).flutterModel).Id]) {
                [annotations addObject:obj];
                *stop = YES;
            }
        }];
    }
    [_mapView removeAnnotations:annotations];
    result(@YES);
}

@end

@implementation BMFCleanAllAnnotations

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    [_mapView removeAnnotations:_mapView.annotations];
    result(@YES);
}

@end


@implementation BMFSelectAnnotation

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments || ![call.arguments safeObjectForKey:@"id"]) {
        result(@NO);
        return;
    }
    NSString *ID = [call.arguments safeObjectForKey:@"id"];
    __block  BMKPointAnnotation *annotation;
    [_mapView.annotations enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([ID isEqualToString:((BMFAnnotationModel *)((BMKPointAnnotation *) obj).flutterModel).Id]) {
            annotation = (BMKPointAnnotation *) obj;
            *stop = YES;
        }
    }];
    if (!annotation) {
        NSLog(@"根据ID(%@)未找到对应的marker", ID);
        result(@NO);
    }
    [_mapView selectAnnotation:annotation animated:YES];
    result(@YES);
}

@end


@implementation BMFDeselectAnnotation

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments || ![call.arguments safeObjectForKey:@"id"]) {
        result(@NO);
        return;
    }
    NSString *ID = [call.arguments safeObjectForKey:@"id"];
    __block  BMKPointAnnotation *annotation;
    [_mapView.annotations enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([ID isEqualToString:((BMFAnnotationModel *)((BMKPointAnnotation *) obj).flutterModel).Id]) {
            annotation = (BMKPointAnnotation *) obj;
            *stop = YES;
        }
    }];
    if (!annotation) {
        NSLog(@"根据ID(%@)未找到对应的marker", ID);
        result(@NO);
    }
    [_mapView deselectAnnotation:annotation animated:YES];
    result(@YES);
}

@end


@implementation BMFShowAnnotations

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments || !call.arguments[@"markers"] || !call.arguments[@"animated"]) {
        result(@NO);
        return;
    }
    NSArray<NSDictionary *> *annotationsDic = (NSArray<NSDictionary *> *)[call.arguments safeValueForKey:@"markers"];
    BOOL animated = [[call.arguments safeValueForKey:@"animated"] boolValue];
    NSMutableArray<id <BMKAnnotation>> *annotations = [NSMutableArray array];
    for (NSDictionary *dic in annotationsDic) {
        BMKPointAnnotation *annotation = [BMKPointAnnotation overlayWithDictionary:dic];
        [annotations addObject:annotation];
    }
    if ([call.arguments safeValueForKey:@"insets"]) {
        BMFEdgeInsets *edge = [BMFEdgeInsets bmf_modelWith:call.arguments[@"insets"]];
        UIEdgeInsets e = [edge toUIEdgeInsets];
        [_mapView showAnnotations:[annotations copy] padding:e animated:animated];
    } else {
        [_mapView showAnnotations:[annotations copy] animated:animated];
    }
    
    result(@YES);
}

@end

@implementation BMFUpdateAnnotation

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments || ![call.arguments safeObjectForKey:@"id"]) {
        result(@NO);
        return;
    }
    NSString *ID = [call.arguments safeObjectForKey:@"id"];
    __block  BMKPointAnnotation *annotation;
    //    __weak __typeof__(_mapView) weakMapView = _mapView;
    [_mapView.annotations enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([ID isEqualToString:((BMFAnnotationModel *)((BMKPointAnnotation *) obj).flutterModel).Id]) {
            annotation = (BMKPointAnnotation *) obj;
            *stop = YES;
        }
    }];
    if (!annotation) {
        NSLog(@"根据ID(%@)未找到对应的marker", ID);
        result(@NO);
    }
    
    NSString *member = [call.arguments safeObjectForKey:@"member"];
    NSLog(@"ios - Member = %@", member);
    
    if ([member isEqualToString:@"title"]) {
        annotation.title = [call.arguments safeObjectForKey:@"value"];
        result(@YES);
    }
    else if ([member isEqualToString:@"subtitle"]) {
        annotation.subtitle = [call.arguments safeObjectForKey:@"value"];
        result(@YES);
    }
    else if ([member isEqualToString:@"position"]) {
        BMFCoordinate *coord = [BMFCoordinate bmf_modelWith:[call.arguments safeObjectForKey:@"value"]];
        annotation.coordinate = [coord toCLLocationCoordinate2D];
        result(@YES);
    }
    else if ([member isEqualToString:@"isLockedToScreen"]) {
        annotation.isLockedToScreen = [[call.arguments safeObjectForKey:@"value"] boolValue];
        if (annotation.isLockedToScreen) {
            annotation.screenPointToLock = [[BMFMapPoint bmf_modelWith:[call.arguments safeObjectForKey:@"screenPointToLock"]] toCGPoint];
        }
        [_mapView setMapStatus:_mapView.getMapStatus];
        result(@YES);
    }
    else if ([member isEqualToString:@"icon"]) {
        BMKPinAnnotationView *view = (BMKPinAnnotationView *)[_mapView viewForAnnotation:annotation];
        view.image = [UIImage imageWithContentsOfFile:[[BMFFileManager defaultCenter] pathForFlutterImageName:[call.arguments safeObjectForKey:@"value"]]];
        result(@YES);
    }
    else if ([member isEqualToString:@"iconData"]) {
        BMKPinAnnotationView *view = (BMKPinAnnotationView *)[_mapView viewForAnnotation:annotation];
        id value = [call.arguments safeObjectForKey:@"value"];
        UIImage *image = [UIImage imageWithData:((FlutterStandardTypedData *)value).data];
        view.image = image;
        result(@YES);
    }
    else if ([member isEqualToString:@"centerOffset"]) {
        BMKPinAnnotationView *view = (BMKPinAnnotationView *)[_mapView viewForAnnotation:annotation];
        BMFMapPoint *point = [BMFMapPoint bmf_modelWith:[call.arguments safeObjectForKey:@"value"]];
        view.centerOffset = [point toCGPoint];
        result(@YES);
    }
    else if ([member isEqualToString:@"enabled3D"]) {
        BMKPinAnnotationView *view = (BMKPinAnnotationView *)[_mapView viewForAnnotation:annotation];
        BOOL value = [[call.arguments safeObjectForKey:@"value"] boolValue];
        view.enabled3D = value;
        result(@YES);
    }
    else if ([member isEqualToString:@"enabled"]) {
        BMKPinAnnotationView *view = (BMKPinAnnotationView *)[_mapView viewForAnnotation:annotation];
        BOOL value = [[call.arguments safeObjectForKey:@"value"] boolValue];
        view.enabled = value;
        result(@YES);
    }
    else if ([member isEqualToString:@"draggable"]) {
        BMKPinAnnotationView *view = (BMKPinAnnotationView *)[_mapView viewForAnnotation:annotation];
        BOOL value = [[call.arguments safeObjectForKey:@"value"] boolValue];
        view.draggable = value;
        result(@YES);
    }
    else if ([member isEqualToString:@"rotate"]) {
        // 增加的改动: +旋转角度
        BMKPinAnnotationView *view = (BMKPinAnnotationView *)[_mapView viewForAnnotation:annotation];
        double value = [[call.arguments safeObjectForKey:@"value"] doubleValue];
        setRotation(view, value);
        result(@YES);
    }
    else {
        NSLog(@"ios - 暂不支持设置%@", member);
        result(@YES);
    }
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

/// 设置旋转
/// TODO: <BUG> 初始化旋转后，updateRotate会导致错位
/// 因为百度地图SDK设置了centerOffset导致只能通过view.centerOffset去进行转换，无法使用原生转换（会被覆盖）
/// 注意：百度地图SDK的y轴方向和原生相反
void setRotation(BMKPinAnnotationView *view, double rotation) {
    // CGPoint anchorPoint = CGPointMake(0.5 + view.centerOffset.x / view.image.size.width, 1 + view.centerOffset.y / view.image.size.height / 2);
    // CGPoint oldOrigin = view.frame.origin;
    // CGPoint centerOffset = view.centerOffset;
    // 
    // view.layer.anchorPoint = anchorPoint;
    // 
    // CGPoint newOrigin = view.frame.origin;
    // CGPoint transition;
    // 
    // transition.x = newOrigin.x - oldOrigin.x;
    // transition.y = newOrigin.y - oldOrigin.y;
    // 
    // view.centerOffset = CGPointMake(centerOffset.x - transition.x, centerOffset.y - transition.y);
    
    
    // 计算偏移
    // 中心点
    float x2 = view.frame.size.width * 0.5;
    float y2 = view.frame.size.height * 0.5;
    
    // 原坐标点
    float x1 = x2 + view.centerOffset.x;
    float y1 = y2 + view.centerOffset.y;
    
    float angle = rotation * M_PI / 180;
    
    float x = (x1 - x2) * cos(angle) - (y1 - y2) * sin(angle) + x2;
    float y = (x1 - x2) * sin(angle) + (y1 - y2) * cos(angle) + y2;
    
    NSLog(@"[DEBUG] size = %f, %f (%f, %f)", view.frame.size.width, view.frame.size.height, x2, y2);
    NSLog(@"[DEBUG] x1 = %f, y1 = %f", x1, y1);
    NSLog(@"[DEBUG] x2 = %f, y2 = %f", x2, y2);
    NSLog(@"[DEBUG] x  = %f, y  = %f", x, y);
    
    [UIView animateWithDuration:3 animations:^{
        view.layer.opacity = 0.6;
        // view.transform = CGAffineTransformMakeRotation(rotation * M_PI / 180);
        CGAffineTransform transform = CGAffineTransformMakeRotation(rotation * M_PI / 180);
        transform = CGAffineTransformConcat(transform, CGAffineTransformMakeTranslation(x / 2, y / 2));
        view.transform = transform;
    }];
    
    // view.transform = CGAffineTransformMakeRotation(rotation * M_PI / 180);
    // CGAffineTransformConcat
    // view.transform = CGAffineTransformRotate(view.transform, rotation * M_PI / 180);
    
    // [UIView animateWithDuration:3 animations:^{
    //     view.transform = CGAffineTransformMakeRotation(rotation * M_PI / 180);
    // }];
    return;
}

