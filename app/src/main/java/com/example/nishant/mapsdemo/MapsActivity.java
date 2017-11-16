package com.example.nishant.mapsdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private Location mLastKnownLocation;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            checkIsLocationEnabled();
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            boolean result = ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (result) {
                new AlertDialog.Builder(this)
                        .setTitle("Permissions Required")
                        .setMessage("Location permission required to use.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        //mLocationManager.requestLocationUpdates("", 400, 1, (LocationListener) MapsActivity.this);
                        checkIsLocationEnabled();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MapsActivity.this, "Permissions denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void checkIsLocationEnabled() {
        LocationManager mLocationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            if (mLocationManager != null) {
                gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception ex) {
            printDebugLog(ex.getMessage());
        }
        try {
            if (mLocationManager != null) {
                network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception ex) {
            printDebugLog(ex.getMessage());
        }
        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setMessage("Enable GPS and Location to use the app.");
            dialog.setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    MapsActivity.this.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    paramDialogInterface.dismiss();
                }
            });
            dialog.show();
        }
    }


    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initializeMap() {
        if (mMap == null) {
            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(MapsActivity.this);
        } else {
            printInfoLog("Map was not null.");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // check if map is created successfully or not
        if (googleMap == null) {
            printInfoLog("Sorry! unable to create map.");
        } else {
            mMap = googleMap;
            printInfoLog("Map is ready to use.");
            addMarkerOnMap();
        }
    }

    private void addMarkerOnMap() {
        // create marker
        LatLng latLng = new LatLng(28.45, 77.001);
        MarkerOptions marker = new MarkerOptions().position(latLng).title("Nishant");
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(6).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //customizeMarker(marker);
        setMarkerColor(marker);
        // adding marker
        mMap.addMarker(marker);
    }

    private void customizeMarker(MarkerOptions marker) {
        /*
         * fromAsset(String assetName) – Loading from assets folder
         * fromBitmap (Bitmap image) – Loading bitmap image
         * fromFile (String path) – Loading from file
         * fromResource (int resourceId) – Loading from drawable resource
         */
        marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
    }

    private void setMarkerColor(MarkerOptions marker) {
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
    }

    private void printInfoLog(String msg) {
        Log.i("Maps_Info", msg);
    }

    private void printDebugLog(String msg) {
        Log.i("Maps_Debug", msg);
    }
}
