package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Client;

/**
 * Created by jahepi on 09/03/16.
 */
public class ClientSync extends AsyncTask<Void, Integer, Void> implements View.OnClickListener {

    private static final String TAG = "ClientSync";

    private ProgressDialog dialog;
    private Activity activity;

    public ClientSync(Activity activity) {
        dialog = new ProgressDialog();
        dialog.setListener(this);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog.show(activity.getFragmentManager(), TAG);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        cancel(true);
        dialog.dismiss();
        activity = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        syncClients();
        return null;
    }

    private void syncClients() {
        try {
            CustomApplication application = (CustomApplication) activity.getApplication();
            String url = CustomApplication.SERVICE_URL + "intranet/android/getClients/" + application.getUser().getId();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            postRequest.setEntity(post);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String sResponse = null;
            StringBuilder jsonStr = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                jsonStr = jsonStr.append(sResponse);
            }

            JSONObject jObject = new JSONObject(jsonStr.toString());
            JSONArray clients = jObject.getJSONArray("clients");
            for(int i = 0; i < clients.length() && !isCancelled(); i++) {

                JSONObject jsonClients = clients.getJSONObject(i);
                final String name = jsonClients.getString("name");

                Client client = new Client();
                client.setId(jsonClients.getInt("id"));
                client.setName(name);
                client.setUser(application.getUser());
                client.setKepler(jsonClients.getString("kepler"));
                client.setModifiedDate(jsonClients.getString("date"));
                client.setActive(jsonClients.getInt("active") == 1);

                if (application.saveClient(client)) {
                    JSONArray addresses = jsonClients.getJSONArray("addresses");
                    for (int e = 0; e < addresses.length() && !isCancelled(); e++) {
                        JSONObject jsonAddresses = addresses.getJSONObject(e);
                        Address address = new Address();
                        address.setId(jsonAddresses.getInt("id"));
                        address.setClient(client);
                        address.setAddress(jsonAddresses.getString("address"));
                        address.getCoord().setLatitude(jsonAddresses.getDouble("latitude"));
                        address.getCoord().setLongitude(jsonAddresses.getDouble("longitude"));
                        address.setActive(jsonAddresses.getInt("active") == 1);
                        address.setModifiedDate(jsonAddresses.getString("date"));
                        if (!application.saveAddress(address)) {
                            Log.d(TAG, "Could not save address " + address.getAddress());
                        }
                    }
                } else {
                    Log.d(TAG, "Could not save client " + client.getName());
                }

                final float percentage = (float) i / (float) clients.length() * 100;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setStatus(name);
                        dialog.setTitle(String.format(activity.getString(R.string.txt_sync_status), (int) percentage + "%"));
                        dialog.setProgress((int) percentage);
                    }
                });
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        cancel(true);
        dialog.dismiss();
        activity = null;
    }

    public static class ProgressDialog extends DialogFragment {

        private ProgressBar progressBar;
        private Button cancelBtn;
        private TextView statusTextView;
        private View.OnClickListener listener;

        public ProgressDialog() {
            super();
            setCancelable(false);
        }

        public void setTitle(String title ) {
            getDialog().setTitle(title);
        }

        public void setStatus(String status) {
            statusTextView.setText(status);
        }

        public void setProgress(final int progress) {
            progressBar.setProgress(progress);
        }

        public void setListener(View.OnClickListener listener) {
            this.listener = listener;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.progress_dialog, container);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
            statusTextView = (TextView) view.findViewById(R.id.statusTextView);
            cancelBtn.setOnClickListener(listener);
            return view;
        }
    }
}
