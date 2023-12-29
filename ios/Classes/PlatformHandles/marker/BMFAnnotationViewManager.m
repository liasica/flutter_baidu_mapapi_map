//
//  BMFAnnotationViewManager.m
//  flutter_baidu_mapapi_map
//
//  Created by Zhang,Baojin on 2020/11/12.
//

#import "BMFAnnotationViewManager.h"

#import <flutter_baidu_mapapi_base/BMFMapModels.h>
#import <flutter_baidu_mapapi_base/UIColor+BMFString.h>

#import "BMFAnnotation.h"
#import "BMFFileManager.h"
#import "BMFPinAnnotationView.h"
#import "BranchIcon.h"

@implementation BMFAnnotationViewManager

#pragma mark - annotationView
+ (BMFAnnotationModel *)annotationModelfromAnnotionView:(BMKAnnotationView *)view {
    return (BMFAnnotationModel *)((BMKPointAnnotation *)view.annotation).flutterModel;
}
/// 根据anntation生成对应的View
+ (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id<BMKAnnotation>)annotation {
    if ([annotation isKindOfClass:[BMKPointAnnotation class]]) {
        BMFAnnotationModel *model = (BMFAnnotationModel *)((BMKPointAnnotation *)annotation).flutterModel;
        NSString *identifier = model.identifier ? model.identifier : NSStringFromClass([BMKPointAnnotation class]);
        BMFPinAnnotationView *annotationView = (BMFPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:identifier];
        
        if (!annotationView) {
            annotationView = [[BMFPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];
        }
        

        if (model.branchIcon) {
            NSString *batteryModel = [model.branchIcon objectForKey:@"batteryModel"];
            NSNumber* number = [model.branchIcon valueForKey:@"number"];
            NSNumber* scale = [model.branchIcon valueForKey:@"scale"];
            // double scale = model.branchIcon.scale;
            // BranchIcon *branchIcon = (BranchIcon *) model.branchIcon;
            // BranchIcon *branchIcon = [BranchIcon create:@"60V" number:10 scale:0.55f];
            // annotationView.image = [branchIcon draw];
            UIImage *image = [BranchIcon draw:number.intValue scale:scale.doubleValue batteryModel:batteryModel];
            annotationView.image = image;
        }
        else if (model.iconData) {
            UIImage *image = [UIImage imageWithData:((FlutterStandardTypedData *)model.iconData).data];
            annotationView.image = image;
        }
        else if (model.icon) {
            annotationView.image = [UIImage imageWithContentsOfFile:[[BMFFileManager defaultCenter] pathForFlutterImageName:model.icon]];
        }
        
        if (model.centerOffset) {
            annotationView.centerOffset = [model.centerOffset toCGPoint];
        }
        
        if (model.rotate > 0) {
            [annotationView setRotation:model.rotate];
        }
        annotationView.canShowCallout = model.canShowCallout;
        annotationView.selected = model.selected;
        annotationView.draggable = model.draggable;
        annotationView.enabled = model.enabled;
        annotationView.enabled3D = model.enabled3D;
        annotationView.hidePaopaoWhenDrag = model.hidePaopaoWhenDrag;
        annotationView.hidePaopaoWhenDragOthers = model.hidePaopaoWhenDragOthers;
        annotationView.hidePaopaoWhenSelectOthers = model.hidePaopaoWhenSelectOthers;
        annotationView.hidePaopaoWhenDoubleTapOnMap = model.hidePaopaoWhenDoubleTapOnMap;
        annotationView.hidePaopaoWhenTwoFingersTapOnMap = model.hidePaopaoWhenTwoFingersTapOnMap;
        annotationView.displayPriority = (float)model.displayPriority;
        annotationView.isOpenCollisionDetection = model.isOpenCollisionDetection;
        annotationView.collisionDetectionPriority = model.collisionDetectionPriority;
        annotationView.isForceDisplay = model.isForceDisplay;
        annotationView.isOpenCollisionDetectionWithMapPOI = model.isOpenCollisionDetectionWithMapPOI;
        annotationView.isOpenCollisionDetectionWithPaoPaoView = model.isOpenCollisionDetectionWithPaoPaoView;
        return annotationView;
    }
    return nil;
}

@end
