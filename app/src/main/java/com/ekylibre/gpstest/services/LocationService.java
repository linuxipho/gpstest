package com.ekylibre.gpstest.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


@SuppressLint("LogNotTimber")
public class LocationService extends Service {

    private static final String TAG = "LoationService";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private LocationManager locationManager = null;

    private class LocationListener implements android.location.LocationListener {

        Location location;

        public LocationListener(String provider) {
            Log.i(TAG, String.format("LocationListener %s", provider));
            location = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, String.format("onLocationChanged: %s", location));
            this.location.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, String.format("onProviderDisabled: %s", provider));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, String.format("onProviderEnabled: %s", provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, String.format("onStatusChanged: %s", provider));
        }
    }

    LocationListener locationListener = new LocationListener(LocationManager.GPS_PROVIDER);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG,"fail to request location update, ignore --> " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (Exception ex) {
                Log.e(TAG,"fail to remove location listners, ignore" + ex.getMessage());
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}