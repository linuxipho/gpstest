package com.ekylibre.gpstest;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.ekylibre.gpstest.database.AppDatabase;
import com.ekylibre.gpstest.services.LocationService;


import com.mapbox.geojson.BoundingBox;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.turf.TurfJoins;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private Button startStopButton;

    private TextView timeTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView speedTextView;
    private TextView accuracyTextView;

    private Intent serviceIntent;
    private AppDatabase database;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceIntent = new Intent(this, LocationService.class);
        database = AppDatabase.getInstance(this);

        mainLayout = findViewById(R.id.main_layout);
        timeTextView = findViewById(R.id.time);
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);
        speedTextView = findViewById(R.id.speed);
        accuracyTextView = findViewById(R.id.accuracy);
        startStopButton = findViewById(R.id.run_service_button);

        startStopButton.setOnClickListener(v -> {
            if (isServiceRunning())
                stopLocationService();
            else
                startLocationService();
        });

        /////////////////////////
        // Point in Polygon test
        /////////////////////////
        String polyJson = "{'TYPE': 'Polygon', 'coordinates':[[" +
                "[3.871908187866211, 43.6287407970948], " +
                "[3.872852325439453, 43.6284845248581], " +
                "[3.873040080070496, 43.6283253248881], " +
                "[3.873109817504883, 43.6281389438997], " +
                "[3.872578740119934, 43.6271099551232], " +
                "[3.872063755989075, 43.6271021891032], " +
                "[3.871935009956360, 43.6272575093126], " +
                "[3.871344923973084, 43.6276652229529], " +
                "[3.871103525161743, 43.6282981443633], " +
                "[3.871908187866211, 43.6287407970947]]]" +
                "}";

//        List<LatLng> polygon = new ArrayList<>();
//        polygon.add(new LatLng(43.62874079709478, 3.8719081878662114));
//        polygon.add(new LatLng(43.6284845248581, 3.8728523254394536));
//        polygon.add(new LatLng(43.62874079709478, 3.873040080070496));
//        polygon.add(new LatLng(43.62874079709478, 3.8719081878662114));
//        polygon.add(new LatLng(43.62874079709478, 3.8719081878662114));
//        polygon.add(new LatLng(43.62874079709478, 3.8719081878662114));
//        polygon.add(new LatLng(43.62874079709478, 3.8719081878662114));
//        polygon.add(new LatLng(43.62874079709478, 3.8719081878662114));
//        polygon.add(new LatLng(43.62874079709478, 3.8719081878662114));
//        polygon.add(new LatLng(43.62874079709478, 3.8719081878662114));

        Polygon polygon = Polygon.fromJson(polyJson);
        Log.e("GPS", "Polygon --> " + polygon);


//        TurfJoins.inside(Point.fromLngLat();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startStopButton.setText(isServiceRunning() ? R.string.stop_service : R.string.run_service);

        // Attache the observer
        database.dao().getLastPoint().observeForever(position -> {
            timeTextView.setText(timeFormat.format(position.time));
            latitudeTextView.setText(String.format(Locale.FRANCE, "%.6f °", position.lat));
            longitudeTextView.setText(String.format(Locale.FRANCE, "%.6f °", position.lon));
            speedTextView.setText(String.format(Locale.FRANCE, "%.1f km/h", position.speed * 3.6));
            accuracyTextView.setText(String.format(Locale.FRANCE, "%s m", position.accuracy));
        });
    }

    private void startLocationService() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startService(serviceIntent);
            startStopButton.setText(R.string.stop_service);
        }
    }

    private void stopLocationService() {
        stopService(serviceIntent);
        startStopButton.setText(R.string.run_service);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationService();
                else
                    Snackbar.make(mainLayout, "Permission denied", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
