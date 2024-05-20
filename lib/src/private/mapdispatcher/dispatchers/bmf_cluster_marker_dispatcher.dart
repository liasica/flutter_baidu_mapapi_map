import 'package:flutter/services.dart';
import 'package:flutter_baidu_mapapi_base/flutter_baidu_mapapi_base.dart';
import 'package:flutter_baidu_mapapi_map/src/private/mapdispatcher/bmf_map_method_id.dart';

class BMFClusterMarkerDispatcher {
  Future<bool> setClusterCoordinates(
      MethodChannel _mapChannel, List<BMFClusterInfo> clusterInfos) async {
    ArgumentError.checkNotNull(_mapChannel, "_mapChannel");
    ArgumentError.checkNotNull(clusterInfos, "clusterInfos");

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFClusterMarkerMethodId.kSetClusterMarkerCoordinateMethod,
          {
            'clusterInfos':
                clusterInfos.map((clusterInfo) => clusterInfo.toMap()).toList()
          } as dynamic)) as bool;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString());
    }
    return result;
  }

  Future<bool> setClusterMaxDistanceInDP(
      MethodChannel _mapChannel, int maxDistanceInDP) async {
    ArgumentError.checkNotNull(_mapChannel, "_mapChannel");

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFClusterMarkerMethodId.kSetMaxDistanceZoomMethod,
          {'maxDistanceInDP': maxDistanceInDP >= 1 ? maxDistanceInDP : 100}
              as dynamic)) as bool;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString());
    }
    return result;
  }

  Future<bool> cleanCluster(MethodChannel _mapChannel) async {
    ArgumentError.checkNotNull(_mapChannel, "_mapChannel");

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFClusterMarkerMethodId.kCleanCluster, {} as dynamic)) as bool;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString());
    }
    return result;
  }

  Future<List<BMFClusterInfo?>> getClusterOnZoomLevel(
      MethodChannel _mapChannel, int zoomLevel) async {
    ArgumentError.checkNotNull(_mapChannel, "_mapChannel");
    ArgumentError.checkNotNull(zoomLevel, "zoomLevel");

    dynamic result;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFClusterMarkerMethodId.kGetClusterOnZoomLevelMethod,
          {'zoomLevel': zoomLevel}));
    } on PlatformException catch (e) {
      BMFLog.e(e.toString());
    }
    List<BMFClusterInfo?> clusters = [];
    if (null != result) {
      for (Map cluster in result) {
        clusters.add(BMFClusterInfo.fromMap(cluster));
      }
    }
    return clusters;
  }

  Future<bool> updateClusters(
      MethodChannel _mapChannel, List<BMFClusterInfo> clusterInfos) async {
    ArgumentError.checkNotNull(_mapChannel, "_mapChannel");
    ArgumentError.checkNotNull(clusterInfos, "clusterInfos");

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFClusterMarkerMethodId.kUpdateClustersMethod,
          {
            'clusterInfos':
                clusterInfos.map((clusterInfo) => clusterInfo.toMap()).toList()
          } as dynamic)) as bool;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString());
    }
    return result;
  }

  Future<bool> refreshClusters(
      MethodChannel _mapChannel, List<BMFClusterInfo> clusterInfos) async {
    ArgumentError.checkNotNull(_mapChannel, "_mapChannel");
    ArgumentError.checkNotNull(clusterInfos, "clusterInfos");

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFClusterMarkerMethodId.kRefreshClustersMethod,
          {
            'clusterInfos':
                clusterInfos.map((clusterInfo) => clusterInfo.toMap()).toList()
          } as dynamic)) as bool;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString());
    }
    return result;
  }
}
