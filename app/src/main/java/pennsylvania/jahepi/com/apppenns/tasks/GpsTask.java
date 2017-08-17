package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.dialogs.ProgressDialog;

/**
 * Created by jahepi on 04/04/16.
 */
public class GpsTask extends AsyncTask<Void, Void, Void> implements LocationListener, View.OnClickListener {

    private static final String TAG = "GpsTask";
    private final static int MIN_DISTANCE = 10;
    private final static int MIN_TIME = 1000;
    private static GpsTask self;

    private Context context;
    private ProgressDialog progressDialog;
    private FragmentManager manager;
    private LocationManager locationManager;
    private double latitude, longitude;
    private boolean running;
    private GpsTaskListener listener;

    public GpsTask(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog();
        progressDialog.setListener(this);
    }

    public static GpsTask getInstance(Context context) {
        if (self != null && !self.isCancelled() && (self.getStatus() == Status.RUNNING || self.getStatus() == Status.PENDING)) {
            return self;
        } else {
            self = new GpsTask(context);
        }
        return self;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        int res = context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
        if (res == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean networkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean gpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (networkProviderEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            } else if (gpsProviderEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            }
            running = true;
            progressDialog.show(manager, TAG);
            progressDialog.hideProgressBar();
        } else {
            running = false;
            this.listener.error("No tiene el pemiso de ubicación habilitado.");
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        publishProgress();
        while (running && !isCancelled()) {
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (running) {
            progressDialog.setTitle(context.getString(R.string.txt_task_gps_title));
            progressDialog.setStatus(context.getString(R.string.txt_task_gps_status));
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (latitude != 0 && longitude != 0) {
            listener.success(latitude, longitude);
        } else {
            listener.error("No se pudo registrar la actividad, intentelo nuevamente");
        }
        clear();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        running = false;
    }

    private void clear() {
        if (locationManager != null) {
            int res = context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
            if (res == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(this);
            }
        }
        if (progressDialog.isResumed()) {
            progressDialog.dismiss();
        }
        cancel(true);
        context = null;
        manager = null;
        listener = null;
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

    @Override
    public void onClick(View v) {
        clear();
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }

    public boolean isRunning() {
        return self.getStatus() == Status.RUNNING && !isCancelled();
    }

    public void setListener(GpsTaskListener listener) {
        this.listener = listener;
    }

    public static interface GpsTaskListener {
        public void success(double latitude, double longitude);
        public void error(String message);
    }
}
