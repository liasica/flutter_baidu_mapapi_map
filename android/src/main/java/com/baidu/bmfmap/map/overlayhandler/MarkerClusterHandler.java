package com.baidu.bmfmap.map.overlayhandler;

import static com.baidu.bmfmap.utils.Constants.MethodProtocol.ClusterProtocol.CLUSTER_CLICK_ITEM_METHOD;
import static com.baidu.bmfmap.utils.Constants.MethodProtocol.ClusterProtocol.CLUSTER_CLICK_METHOD;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.bmfmap.BMFMapController;
import com.baidu.bmfmap.cluster.clustering.Cluster;
import com.baidu.bmfmap.cluster.clustering.ClusterItem;
import com.baidu.bmfmap.cluster.clustering.ClusterManager;
import com.baidu.bmfmap.map.MapListener;
import com.baidu.bmfmap.utils.Constants;
import com.baidu.bmfmap.utils.Env;
import com.baidu.bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MarkerClusterHandler extends OverlayHandler implements
        ClusterManager.OnClusterClickListener<MarkerClusterHandler.MyItem>,
        ClusterManager.OnClusterItemClickListener<MarkerClusterHandler.MyItem> {

    private static final String TAG = "MarkerClusterHandler";

    private ClusterManager mClusterManager;

    public MarkerClusterHandler(BMFMapController bmfMapController) {
        super(bmfMapController);
        mClusterManager = new ClusterManager<MyItem>(bmfMapController.getContext(), mBaiduMap);
        MapListener mapListener = bmfMapController.getMapListener();
        if (null != mapListener) {
            // 设置maker点击时的响应
            mapListener.setOnClusterMarkerClickListener(mClusterManager);
            // 设置地图监听，当地图状态发生改变时，进行点聚合运算
            mapListener.setOnClusterMapStatusChangeListener(mClusterManager);

            mClusterManager.setOnClusterClickListener(this);
            mClusterManager.setOnClusterItemClickListener(this);
        }
    }

    @Override
    public void handlerMethodCall(MethodCall call, MethodChannel.Result result) {
        if (null == call) {
            result.success(false);
            return;
        }

        String methodId = call.method;
        if (TextUtils.isEmpty(methodId)) {
            result.success(false);
            return;
        }

        boolean ret = false;
        switch (methodId) {
            case Constants.MethodProtocol.ClusterProtocol.SET_CLUSTER_MARKER_COORDINATE_METHOD:
                ret = addClusters(call);
                break;
            case Constants.MethodProtocol.ClusterProtocol.SET_MAX_DISTANCE_ZOOM_METHOD:
                ret = setMaxDistanceZoom(call);
                break;
            case Constants.MethodProtocol.ClusterProtocol.CLEAN_CLUSTER_METHOD:
                ret = cleanCluster(call);
                break;
            case Constants.MethodProtocol.ClusterProtocol.UPDATE_CLUSTERS_METHOD:
                ret = updateCluster(call);
                break;
            default:
                break;
        }

        result.success(ret);
    }


    private boolean updateCluster(MethodCall call) {
        if (null == call || mClusterManager == null) {
            return false;
        }

        mClusterManager.clearItems();
        boolean ret = addClusters(call);
        if (ret) {
            mClusterManager.cluster();
        }

        return ret;
    }

    private boolean cleanCluster(MethodCall call) {
        if (null == call || mClusterManager == null) {
            return false;
        }

        mClusterManager.clearItems();
        mClusterManager.cluster();
        return true;
    }

    private boolean setMaxDistanceZoom(MethodCall call) {
        if (null == call || mClusterManager == null) {
            return false;
        }

        Map<String, Object> argument = call.arguments();
        if (null == argument) {
            return false;
        }

        if (!argument.containsKey("maxDistanceInDP")) {
            return false;
        }

        Integer maxDistanceInDP = (Integer) argument.get("maxDistanceInDP");
        if (null == maxDistanceInDP) {
            return false;
        }

        mClusterManager.setMaxDistanceZoom(maxDistanceInDP);
        return true;
    }

    private boolean addClusters(MethodCall call) {

        if (Env.DEBUG) {
            Log.d(TAG, "addClusters enter");
        }

        if (null == call || mClusterManager == null) {
            return false;
        }

        Map<String, Object> argument = call.arguments();
        if (null == argument) {
            return false;
        }

//        String id = new TypeConverter<String>().getValue(argument, "id");
//        if (TextUtils.isEmpty(id)) {
//            return false;
//        }

        if (!argument.containsKey("clusterInfos")) {
            return false;
        }

        List<Object> clusterInfos = (List<Object>) argument.get("clusterInfos");

        List<MyItem> items = new ArrayList<>();
        Iterator itr = clusterInfos.iterator();
        while (itr.hasNext()) {
            Map<String, Object> clusterInfo = (Map<String, Object>) itr.next();
            if (clusterInfo == null) {
                continue;
            }
            Map<String, Object> coordinate = (Map<String, Object>) clusterInfo.get("coordinate");
            LatLng latLng = FlutterDataConveter.mapToLatlng(coordinate);
            if (latLng == null) {
                continue;
            }

            String icon = null;
            byte[] iconData = null;

            if (clusterInfo.containsKey("icon")) {
                icon = (String) clusterInfo.get("icon");
            }

            if (clusterInfo.containsKey("iconData")) {
                iconData = (byte[]) clusterInfo.get("iconData");
            }

            BitmapDescriptor bitmapDescriptor = null;
            if (!TextUtils.isEmpty(icon)) {
                bitmapDescriptor =
                        BitmapDescriptorFactory.fromAsset("flutter_assets/" + icon);
            } else {
                if (null == iconData || iconData.length <= 0) {
                    return false;
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
                if (bitmap == null) {
                    return false;
                }

                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            }

            if (null == bitmapDescriptor) {
                return false;
            }

            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(icon)) {
                bundle.putString("icon", icon);
            }
            if ((iconData != null && iconData.length > 0)) {
                bundle.putByteArray("iconData", iconData);
            }

            items.add(new MyItem(latLng, bitmapDescriptor, bundle));
        }

        if (null == items || items.size() == 0) {
            if (Env.DEBUG) {
                Log.d(TAG, "items is null");
            }
            return false;
        }

        mClusterManager.addItems(items);
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<MyItem> cluster) {
        if (mMapController == null) {
            return false;
        }

        HashMap<String, Object> clusterMap = new HashMap<>();
        List<HashMap<String, Object>> clusterInfoList = new ArrayList<>();
        MethodChannel methodChannel = mMapController.getMethodChannel();
        if (cluster == null || methodChannel == null) {
            return false;
        }

        for (MyItem item : cluster.getItems()) {
            LatLng position = item.getPosition();
            Bundle bundle = item.getExtras();
            if (position == null || bundle == null) {
                continue;
            }

            HashMap<String, Object> clusterInfo = getClusterInfo(position, bundle, false);
            clusterInfoList.add(clusterInfo);
        }

        clusterMap.put("clusterInfoList", clusterInfoList);
        clusterMap.put("size", cluster.getSize());

        methodChannel.invokeMethod(CLUSTER_CLICK_METHOD, clusterMap, new MethodChannel.Result() {
            @Override
            public void success(@Nullable Object result) {
                if (Env.DEBUG) {
                    Log.d(TAG, "onClusterClick methodChannel is success: ");
                }
            }

            @Override
            public void error(@NonNull String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {
                if (Env.DEBUG) {
                    Log.d(TAG, "onClusterClick error: " +" errorCode : " +
                            ""+ errorCode + " errorMessage: " + errorMessage);
                }
            }

            @Override
            public void notImplemented() {

            }
        });
        return true;
    }

    @Override
    public boolean onClusterItemClick(MyItem item) {
        if (mMapController == null) {
            return false;
        }
        MethodChannel methodChannel = mMapController.getMethodChannel();
        if (item == null || methodChannel == null) {
            return false;
        }

        LatLng position = item.getPosition();
        Bundle bundle = item.getExtras();
        if (position == null || bundle == null) {
            return false;
        }

        HashMap<String, Object> clusterInfoMap = getClusterInfo(position, bundle, true);
        if (clusterInfoMap == null) {
            return false;
        }

        methodChannel.invokeMethod(CLUSTER_CLICK_ITEM_METHOD, clusterInfoMap, new MethodChannel.Result() {
            @Override
            public void success(@Nullable Object result) {
                if (Env.DEBUG) {
                    Log.d(TAG, "onClusterItemClick methodChannel is success: ");
                }
            }

            @Override
            public void error(@NonNull String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {
                if (Env.DEBUG) {
                    Log.d(TAG, "onClusterItemClick error: " +" errorCode : " +
                            ""+ errorCode + " errorMessage: " + errorMessage);
                }
            }

            @Override
            public void notImplemented() {

            }
        });

        return true;
    }


    private HashMap<String, Object> getClusterInfo(LatLng position, Bundle bundle, boolean isItem) {
        HashMap<String, Object> clusterInfoMap = new HashMap<>();
        HashMap<String, Object> itemMap = new HashMap<>();
        HashMap<String, Double> coord = new HashMap<>();
        coord.put("latitude", position.latitude);
        coord.put("longitude", position.longitude);
        itemMap.put("coordinate", coord);

        String icon = bundle.getString("icon");
        byte[] data = bundle.getByteArray("iconData");
        if (TextUtils.isEmpty(icon) && (data == null || data.length <= 0)) {
            return null;
        }
        if (!TextUtils.isEmpty(icon)) {
            itemMap.put("icon", icon);
        }
        HashMap<String, Object> iconData = new HashMap<>();
        if (data != null && data.length > 0) {
            iconData.put("data", data);
            itemMap.put("iconData", iconData);
        }

        if (!isItem) {
            return itemMap;
        }

        clusterInfoMap.put("clusterInfo", itemMap);
        return clusterInfoMap;
    }

    public class MyItem implements ClusterItem {

        private final LatLng mPosition;
        private final BitmapDescriptor mIcon;

        private Bundle mExtraInfo;

        private MyItem(LatLng latLng, BitmapDescriptor bitmapDescriptor, Bundle extraInfo) {
            mPosition = latLng;
            mIcon = bitmapDescriptor;
            mExtraInfo = extraInfo;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            return mIcon;
        }

        @Override
        public Bundle getExtras() {
            return mExtraInfo;
        }
    }
}
