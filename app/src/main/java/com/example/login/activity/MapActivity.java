package com.example.login.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.login.R;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    private AMap aMap = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 定位隐私政策同意
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);

        // 初始化地图
        mapView = findViewById(R.id.gaode_map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        // 请求定位权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 如果已经有定位权限，执行异步任务初始化地图
            new MapInitializerTask().execute();
        }

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 关闭当前 MapActivity
            }
        });
    }

    // 异步任务用于初始化地图
    private class MapInitializerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            initMap();
            return null;
        }
    }

    private void initMap() {
        if (aMap != null) {
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
            myLocationStyle.interval(2000);

            aMap.setMyLocationStyle(myLocationStyle);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            aMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapView.onResume();
            initMap();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (aMap != null) {
            aMap.setMyLocationEnabled(false); // 关闭定位图层
        }
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        try {
            // 在退出时释放地图资源
            mapView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
