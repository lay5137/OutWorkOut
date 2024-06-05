package com.example.workout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RoadRunningActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Button startButton, stopButton;
    private TextView timerText;

    private long startTime, timeInMilliseconds = 0;
    private Handler timerHandler = new Handler();
    private boolean isRunning = false;
    private float totalDistance = 0;
    private LatLng lastLocation = null;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_running);

        db = new DatabaseHelper(this);

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        timerText = findViewById(R.id.timer_text);

        startButton.setOnClickListener(v -> startRunning());
        stopButton.setOnClickListener(v -> stopRunning());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void startRunning() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);
        isRunning = true;
        totalDistance = 0;
        lastLocation = null;
    }

    private void stopRunning() {
        isRunning = false;
        timerHandler.removeCallbacks(updateTimerThread);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (lastLocation != null) {
                            float[] results = new float[1];
                            android.location.Location.distanceBetween(
                                    lastLocation.latitude, lastLocation.longitude,
                                    currentLocation.latitude, currentLocation.longitude,
                                    results);
                            totalDistance += results[0];
                        }
                    }
                });

        long elapsedTime = SystemClock.uptimeMillis() - startTime;
        int seconds = (int) (elapsedTime / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        String time = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        db.addExerciseRecord(currentDate, "Running", (int) totalDistance); // 거리 저장
        db.addExerciseRecord(currentDate, "Running Time", seconds); // 시간 저장

        Toast.makeText(this, "Recorded: " + time + ", Distance: " + totalDistance + " meters", Toast.LENGTH_SHORT).show();
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (timeInMilliseconds / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerText.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(android.location.Location location) {
                if (isRunning && lastLocation != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    float[] results = new float[1];
                    android.location.Location.distanceBetween(
                            lastLocation.latitude, lastLocation.longitude,
                            currentLatLng.latitude, currentLatLng.longitude,
                            results);
                    totalDistance += results[0];
                    lastLocation = currentLatLng;
                    mMap.addPolyline(new PolylineOptions().add(currentLatLng).width(5).color(ContextCompat.getColor(RoadRunningActivity.this, R.color.colorAccent)));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, location -> {
                                if (location != null) {
                                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
