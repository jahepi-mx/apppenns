package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.dialogs.ProgressDialog;
import pennsylvania.jahepi.com.apppenns.entities.Product;

/**
 * Created by jahepi on 09/03/16.
 * Asynchronous task for downloading products
 */
public class ProductSync extends AsyncTask<Void, ProductSync.DownloadInfo, Boolean> implements View.OnClickListener {

    private static final String TAG = "ProductSync";
    private static ProductSync self;

    private ProgressDialog dialog;
    private Context context;
    private DownloadInfo downloadInfo;
    private FragmentManager manager;

    private ProductSync(Context context) {
        dialog = new ProgressDialog();
        dialog.setListener(this);
        this.context = context;
        downloadInfo = new DownloadInfo();
    }

    public static ProductSync getInstance(Context context) {
        if (self != null && !self.isCancelled() && (self.getStatus() == Status.RUNNING || self.getStatus() == Status.PENDING)) {
            return self;
        } else {
           self = new ProductSync(context);
        }
        return self;
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }

    public boolean isRunning() {
        return self.getStatus() == Status.RUNNING && !isCancelled();
    }

    @Override
    protected void onPreExecute() {
        dialog.show(manager, TAG);
    }

    @Override
    protected void onProgressUpdate(DownloadInfo... values) {
        try {
            DownloadInfo info = values[0];
            dialog.setStatus(info.name);
            dialog.setTitle(String.format(context.getString(R.string.txt_sync_status), info.percentage + "%"));
            dialog.setProgress(info.percentage);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (!success) {
            Toast.makeText(context, context.getString(R.string.txt_error_product_sync), Toast.LENGTH_LONG).show();
        }
        cancel(true);
        if (dialog.isResumed()) {
            dialog.dismiss();
        }
        context = null;
        manager = null;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return syncProducts();
    }

    private Boolean syncProducts() {
        try {
            CustomApplication application = (CustomApplication) context;
            String url = CustomApplication.SERVICE_URL + "intranet/android/getProducts/" + application.getUser().getId();

            URL urlRef = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlRef.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            StringBuilder jsonStr = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                jsonStr = jsonStr.append(line);
            }

            application.deleteProducts();
            JSONObject jObject = new JSONObject(jsonStr.toString());
            JSONArray products = jObject.getJSONArray("products");

            for (int i = 0; i < products.length() && !isCancelled(); i++) {

                JSONObject jsonProduct = products.getJSONObject(i);
                String name = jsonProduct.getString("name");

                Product product = new Product();
                product.setId(jsonProduct.getString("id"));
                product.setName(name);
                product.setUser(application.getUser());
                product.setModifiedDate(jsonProduct.getString("date"));

                if (application.saveProduct(product)) {
                    Log.d(TAG, "syncProducts inserted: " + product.getName());
                } else {
                    Log.d(TAG, "Could not save product " + product.getName());
                }

                float percentage = (float) i / (float) products.length() * 100;
                downloadInfo.percentage = (int) percentage;
                downloadInfo.name = name;
                publishProgress(downloadInfo);
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        cancel(true);
        dialog.dismiss();
        context = null;
        manager = null;
    }

    public static class DownloadInfo {
        int percentage;
        String name;
    }
}
