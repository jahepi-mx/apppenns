package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import pennsylvania.jahepi.com.apppenns.dialogs.ProgressDialog;
import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Client;

/**
 * Created by jahepi on 09/03/16.
 */
public class ClientSync extends AsyncTask<Void, Integer, Void> implements View.OnClickListener {

    private static final String TAG = "ClientSync";

    private ProgressDialog dialog;
    private Context context;

    public ClientSync(Context context) {
        dialog = new ProgressDialog();
        dialog.setListener(this);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        if (!dialog.isAdded()) {
            dialog.show(((Activity) context).getFragmentManager(), TAG);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        cancel(true);
        dialog.dismiss();
        context = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        syncClients();
        return null;
    }

    private void syncClients() {
        try {
            CustomApplication application = (CustomApplication) ((Activity) context).getApplication();
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
            for (int i = 0; i < clients.length() && !isCancelled(); i++) {

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
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.setStatus(name);
                            dialog.setTitle(String.format(context.getString(R.string.txt_sync_status), (int) percentage + "%"));
                            dialog.setProgress((int) percentage);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    }
                });
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, context.getString(R.string.txt_error_client_sync), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
            @Override
    public void onClick(View v) {
        cancel(true);
        dialog.dismiss();
        context = null;
    }
}
