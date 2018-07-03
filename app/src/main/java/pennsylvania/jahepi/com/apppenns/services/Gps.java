package pennsylvania.jahepi.com.apppenns.services;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import pennsylvania.jahepi.com.apppenns.CustomApplication;

/**
 * Created by javier.hernandez on 24/02/2016.
 * Gps class service, it gets the current device location every certain amount time.
 */
public class Gps implements LocationListener {

    private final static String TAG = "Gps";
    private final static int INTERVAL = 15 * 60 * 1000;
    private final static int MIN_TIME = 10 * 60 * 1000;
    private final static int MIN_DISTANCE = 20;

    private CustomApplication application;
    private LocationManager locationManager;
    private Handler handler;
    private double latitude;
    private double longitude;
    private boolean isEnabled;
    private boolean networkProviderEnabled;
    private boolean gpsProviderEnabled;
    private GpsThread thread;

    public Gps(CustomApplication application) {
        this.application = application;
        handler = new Handler();
        locationManager = (LocationManager) this.application.getSystemService(Context.LOCATION_SERVICE);
        gpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isEnabled = gpsProviderEnabled || networkProviderEnabled;
    }

    private class GpsThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Gps.this.start();
                }
            });
        }
    }

    public void start() {
        int res = application.getApplicationContext().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
            gpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (networkProviderEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            } else if (gpsProviderEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            }
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        application.onChangeLocation(latitude, longitude);

        /*
        int res = application.getApplicationContext().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
        */
        if (thread == null || !thread.isAlive()) {
            thread = new GpsThread();
            thread.start();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void onProviderEnabled(String provider) {
        try {
            if (thread != null) {
                thread.interrupt();
            }
        } catch (Exception e) {

        }
        isEnabled = true;
        start();
    }

    @Override
    public void onProviderDisabled(String provider) {
        try {
            if (thread != null) {
                thread.interrupt();
            }
        } catch (Exception e) {

        }
        isEnabled = false;
    }
}
