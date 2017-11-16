package com.example.nishant.mapsdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by nishant on 13/11/17.
 */

public class MainMapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private static final int SETTINGS_RESULT_CODE = 124;
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mainActivityMap);
        supportMapFragment.getMapAsync(MainMapsActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        printInfoLog("OnResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        printInfoLog("OnPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        printInfoLog("OnRestart");
        if (isLocationPermissionGranted()) {
            checkIsLocationEnabled();
        } else {
            showPermissionExplanationDialog();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        printInfoLog("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printInfoLog("OnDestroy");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mGoogleMap = googleMap;
            if (!isLocationPermissionGranted()) {
                showPermissionExplanationDialog();
            } else {
                checkIsLocationEnabled();
            }
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(MainMapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void checkIsLocationEnabled() {
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showPopUpIsLocationEnabled("Your device location seems to be disabled, Please enable it to proceed.");
        } else {
            // everything is done now proceed
            initUserLocationDemoNow();
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app requires your permission to use your device current location.\nHere you can customize your dialog.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainMapsActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                })
                .create()
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        checkIsLocationEnabled();
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    boolean result = ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION);
                    if (result) {
                        showPermissionExplanationDialog();
                    } else {
                        showAcceptPermissionsDialog();
                    }
                }
            }
        }
    }

    private void showAcceptPermissionsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("Go to app settings to allow these permissions.")
                .setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        MainMapsActivity.this.finish();
                    }
                })
                .create()
                .show();
    }

    private void showPopUpIsLocationEnabled(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_RESULT_CODE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        MainMapsActivity.this.finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SETTINGS_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    checkIsLocationEnabled();
                }
        }
    }

    private void initUserLocationDemoNow() {
        if (mLocationManager != null) {
            String provider = mLocationManager.getBestProvider(new Criteria(), false);
            boolean result = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (result) {
                Location location = getDeviceCurrentLocation();
                if (location != null) {
                    printInfoLog("Current location: " + location.getLatitude() + "' " + location.getLongitude());
                    setUpMarkerOnMap(location);

                }
            }
        }
    }

    private void setUpMarkerOnMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(latLng).title("Nishant's location");
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //customizeMarker(marker);
        setMarkerColor(marker);
        // adding marker
        mGoogleMap.addMarker(marker);
        enableGPSBtnOnMap();
        enableZoomControlsOnMap();
        setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setRotateGesturesOnMap();
    }

    private void setMarkerColor(MarkerOptions marker) {
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
    }

    private void enableGPSBtnOnMap() {
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void enableZoomControlsOnMap() {
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void setMapType(int type) {
        mGoogleMap.setMapType(type);
    }

    private void setRotateGesturesOnMap() {
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
    }

    public Location getDeviceCurrentLocation() {
        try {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // getting GPS status
            if (mLocationManager != null) {
                isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            // getting network status
            if (mLocationManager != null) {
                isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            if (isGPSEnabled && isNetworkEnabled) {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (mLocationManager != null) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (mLocationManager != null) {
                            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void printInfoLog(String msg) {
        Log.i("Maps_Info", msg);
    }

    private void printDebugLog(String msg) {
        Log.i("Maps_Debug", msg);
    }

    @Override
    public void onLocationChanged(Location location) {
        printInfoLog("onLocationChanged current location: " + location.getLatitude() + "' " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
