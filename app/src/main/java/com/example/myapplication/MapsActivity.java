package com.example.myapplication;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private List<LatLng> pathPoints = new ArrayList<>();
    private float totalDistance = 0;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Google Map과 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startTime = System.currentTimeMillis();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 위치 권한 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);

        // 마지막 위치 가져오기
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                startLocationUpdates();
            }
        });
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);  // 10초마다 업데이트
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    // 위치 변경 시 콜백
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            for (Location location : locationResult.getLocations()) {
                LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // 경로 업데이트
                pathPoints.add(newLatLng);
                drawPath();

                // 이동 거리 계산
                if (pathPoints.size() > 1) {
                    LatLng lastPoint = pathPoints.get(pathPoints.size() - 2);
                    Location lastLocation = new Location("");
                    lastLocation.setLatitude(lastPoint.latitude);
                    lastLocation.setLongitude(lastPoint.longitude);
                    totalDistance += lastLocation.distanceTo(location);
                }
            }
        }
    };

    private void drawPath() {
        PolylineOptions polylineOptions = new PolylineOptions().addAll(pathPoints).color(Color.BLUE).width(5);
        mMap.addPolyline(polylineOptions);
    }
}

if (pathPoints.size() > 1) {
LatLng lastPoint = pathPoints.get(pathPoints.size() - 2);
Location lastLocation = new Location("");
    lastLocation.setLatitude(lastPoint.latitude);
    lastLocation.setLongitude(lastPoint.longitude);
totalDistance += lastLocation.distanceTo(location);
}

long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;  // 초 단위
