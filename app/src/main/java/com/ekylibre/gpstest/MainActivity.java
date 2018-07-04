package com.ekylibre.gpstest;

import android.Manifest;
import android.app.ActivityManager;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ekylibre.gpstest.database.AppDatabase;
import com.ekylibre.gpstest.database.models.Point;
import com.ekylibre.gpstest.services.LocationService;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private Button startStopButton;

    private Intent serviceIntent;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceIntent = new Intent(this, LocationService.class);
        database = AppDatabase.getInstance(this);

        mainLayout = findViewById(R.id.main_layout);
        latitudeTextView = findViewById(R.id.latitude);
        longitudeTextView = findViewById(R.id.longitude);
        startStopButton = findViewById(R.id.run_service_button);

        startStopButton.setOnClickListener(v -> {
            if (isServiceRunning())
                stopLocationService();
            else
                startLocationService();
        });

        //AndroidViewModel pointViewModel = ViewModelProviders.of(this).get(PointViewModel.class);

        // Create the observer which updates the UI.
        final Observer<Point> pointObserver = position -> {
            latitudeTextView.setText(String.format(Locale.FRANCE, "Latitude : %.5f °", position.lat));
            longitudeTextView.setText(String.format(Locale.FRANCE, "Longitude : %.5f °", position.lon));
        };

        database.dao().getLastPoint().observeForever(pointObserver);
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
