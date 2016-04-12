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
 */
public class Gps implements LocationListener {

    private final static String TAG = "Gps";
    private final static int INTERVAL = 15 * 60 * 1000;
    private final static int MIN_TIME = 1 * 10 * 1000;
    private final static int MIN_DISTANCE = 20;

    private CustomApplication application;
    private LocationManager locationManager;
    private Handler handler;
    private double latitude;
    private double longitude;
    private boolean isEnabled;

    public Gps(CustomApplication application) {
        this.application = application;
        handler = new Handler();
        locationManager = (LocationManager) this.application.getSystemService(Context.LOCATION_SERVICE);
        isEnabled  = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private class GpsThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
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
        int res = application.getApplicationContext().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
        GpsThread thread = new GpsThread();
        thread.start();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void onProviderEnabled(String provider) {
        isEnabled = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        isEnabled = false;
    }
}
