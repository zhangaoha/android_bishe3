package com.example.login.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.ServiceSettings;
import com.example.login.R;


public class FindFragment extends Fragment {
    private View root;
    private MapView mapView;
    private AMap aMap = null;

//    private Button btn_search;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null){
            root = inflater.inflate(R.layout.fragment_find, container, false);
        }
        

//        btn_search = root.findViewById(R.id.btn_search);
        // 定义了一个地图view
        mapView = root.findViewById(R.id.gaode_map);

        mapView.onCreate(savedInstanceState);

        // 初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有授予定位权限，请求权限
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 如果已经有定位权限，初始化地图
            initMap();
        }
        return root;
    }

    private void initMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        myLocationStyle.interval(2000);

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.setMyLocationEnabled(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (aMap != null) {
            aMap.setMyLocationEnabled(true);
        }
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (aMap != null) {
            aMap.setMyLocationEnabled(false);
        }
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}