package com.example.hansaanuradhawickramanayake.memerobleplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        if(intent.getIntExtra("placeIndex", 0 ) == 0){

            // Zoom on user's location

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    centerMapOnLocation(location, "Your Location");
                }



                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                centerMapOnLocation(lastKnownLocation, "Your Location");
            }

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else {

            Location savedLocation = new Location(LocationManager.GPS_PROVIDER);
            savedLocation.setLatitude(MainActivity.locationList.get(intent.getIntExtra("placeIndex", 0)).latitude);
            savedLocation.setLongitude(MainActivity.locationList.get(intent.getIntExtra("placeIndex", 0)).longitude);

            centerMapOnLocation(savedLocation, MainActivity.places.get(intent.getIntExtra("placeIndex", 0)));
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                startListening();

            }
        }
    }


    public void startListening(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 , 0 , locationListener);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            centerMapOnLocation(lastKnownLocation, "Your Location");
        }
    }

    public void centerMapOnLocation(Location location, String title){

        if (location != null) {
            LatLng usersLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(usersLocation).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLocation, 5));
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";

        try {

            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);


            if (addressList != null && addressList.size() > 0){


                if (addressList.get(0).getThoroughfare() != null){

                    address += addressList.get(0).getThoroughfare() + ", ";
                }

                if (addressList.get(0).getLocality() != null){

                    address += addressList.get(0).getLocality() + ", ";
                }

                if (addressList.get(0).getSubAdminArea() != null){

                    address += addressList.get(0).getSubAdminArea() + ", ";
                }

                if (addressList.get(0).getAdminArea() != null){

                    address += addressList.get(0).getAdminArea() + ", ";
                }

                if (addressList.get(0).getCountryName() != null){

                    address += addressList.get(0).getCountryName();
                }

            }


        } catch (Exception e) {

            e.printStackTrace();

        }

        if (address.equals("")){

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address += simpleDateFormat.format(new Date());
        }


        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        MainActivity.places.add(address);
        MainActivity.locationList.add(latLng);

        MainActivity.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Location saved!", Toast.LENGTH_SHORT).show();

    }
}

