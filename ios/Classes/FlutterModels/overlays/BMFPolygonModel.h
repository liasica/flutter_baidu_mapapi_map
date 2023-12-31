//
//  BMFPolygonModel.h
//  flutter_baidu_mapapi_map
//
//  Created by zhangbaojin on 2020/2/27.
//

#import <flutter_baidu_mapapi_base/BMFModel.h>

@class BMFCoordinate;
@class BMFHollowShapeModel;
NS_ASSUME_NONNULL_BEGIN

@interface BMFPolygonModel : BMFModel

/// flutter层polygon的唯一id(用于区别哪个polygon)
@property (nonatomic, copy) NSString *Id;

/// 经纬度数组
@property (nonatomic, copy) NSArray<BMFCoordinate *> *coordinates;

/// 镂空数组
@property (nonatomic, copy) NSArray<BMFHollowShapeModel *> *hollowShapes;

/// 线宽
@property (nonatomic, assign) int width;

/// 颜色16进制strokeColor
@property (nonatomic, copy) NSString *strokeColor;

/// 颜色16进制fillColor
@property (nonatomic, copy) NSString *fillColor;

/// 虚线类型
@property (nonatomic, assign) int lineDashType;

/// 是否可点击
@property (nonatomic, assign) BOOL clickable;

@end

NS_ASSUME_NONNULL_END
