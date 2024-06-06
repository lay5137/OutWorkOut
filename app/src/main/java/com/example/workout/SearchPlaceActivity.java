package com.example.workout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class SearchPlaceActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "SearchPlaceActivity";
    private GoogleMap mMap;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        dbHelper = new DatabaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 천안의 좌표 설정
        LatLng cheonan = new LatLng(36.8150, 127.1139);
        mMap.addMarker(new MarkerOptions().position(cheonan).title("천안"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cheonan, 15));

        // 지도 UI 설정 활성화
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // 저장된 철봉 위치를 불러와 지도에 표시
        loadSavedMarkers();

        // 지도 클릭 리스너 설정
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng point) {
                showAddLocationDialog(point);
            }
        });
    }

    private void showAddLocationDialog(final LatLng point) {
        // 다이얼로그 레이아웃을 인플레이트합니다
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_location, null);

        final EditText editTextDescription = dialogView.findViewById(R.id.editText_description);
        Button buttonSave = dialogView.findViewById(R.id.button_save);

        // 다이얼로그 생성
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("철봉 위치 등록")
                .create();

        // 저장 버튼 클릭 리스너 설정
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = editTextDescription.getText().toString();
                if (!description.isEmpty()) {
                    // 철봉 위치를 지도에 마커로 추가
                    mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .title("철봉 위치")
                            .snippet(description)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    // 철봉 위치를 데이터베이스에 저장
                    dbHelper.addBarLocation(point.latitude, point.longitude, description);
                    Log.d(TAG, "Location saved: " + point.latitude + ", " + point.longitude + " - " + description);
                    dialog.dismiss();
                    Toast.makeText(SearchPlaceActivity.this, "철봉 위치가 저장되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchPlaceActivity.this, "설명을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void loadSavedMarkers() {
        ArrayList<MarkerData> markers = dbHelper.getBarLocations();
        Log.d(TAG, "Loaded markers: " + markers.size());
        if (markers != null && !markers.isEmpty()) {
            for (MarkerData marker : markers) {
                LatLng position = new LatLng(marker.getLatitude(), marker.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title("철봉 위치")
                        .snippet(marker.getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                Log.d(TAG, "Marker added: " + marker.getLatitude() + ", " + marker.getLongitude() + " - " + marker.getDescription());
            }
        }
    }
}

