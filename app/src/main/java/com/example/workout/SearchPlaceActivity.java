package com.example.workout;

import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchPlaceActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

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
                    mMap.addMarker(new MarkerOptions().position(point).title("철봉 위치").snippet(description));
                    dialog.dismiss();
                    Toast.makeText(SearchPlaceActivity.this, "철봉 위치가 저장되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchPlaceActivity.this, "설명을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
}
