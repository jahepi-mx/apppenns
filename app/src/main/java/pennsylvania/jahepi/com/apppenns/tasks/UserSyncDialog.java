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
import pennsylvania.jahepi.com.apppenns.entities.TaskActivity;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by jahepi on 09/03/16.
 */
public class UserSyncDialog extends AsyncTask<Void, UserSyncDialog.DownloadInfo, Boolean> implements View.OnClickListener {

    private static final String TAG = "UserSyncDialog";
    private static UserSyncDialog self;

    private ProgressDialog dialog;
    private Context context;
    private DownloadInfo downloadInfo;
    private FragmentManager manager;

    private UserSyncDialog(Context context) {
        dialog = new ProgressDialog();
        dialog.setListener(this);
        this.context = context;
        downloadInfo = new DownloadInfo();
    }

    public static UserSyncDialog getInstance(Context context) {
        if (self != null && !self.isCancelled() && (self.getStatus() == Status.RUNNING || self.getStatus() == Status.PENDING)) {
            return self;
        } else {
           self = new UserSyncDialog(context);
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
            Toast.makeText(context, context.getString(R.string.txt_error_user_sync), Toast.LENGTH_LONG).show();
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
        return syncUserData();
    }

    private Boolean syncUserData() {

        try {
            CustomApplication application = (CustomApplication) context;
            String url = CustomApplication.SERVICE_URL + "intranet/android/getTaskActivities";
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

            JSONObject jObject = new JSONObject(jsonStr.toString());
            JSONArray activities = jObject.getJSONArray("taskActivities");

            for (int i = 0; i < activities.length(); i++) {
                JSONObject json = activities.getJSONObject(i);
                TaskActivity taskActivity = new TaskActivity();
                taskActivity.setId(json.getInt("id"));
                taskActivity.setName(json.getString("name"));
                taskActivity.setUserType(json.getInt("userType"));
                taskActivity.setModifiedDate(json.getString("date"));
                taskActivity.setActive(json.getInt("active") != 0);
                if (application.saveTaskActivity(taskActivity)) {
                    Log.d(TAG, "syncTaskActivities inserted: " + taskActivity.getName());
                } else {
                    Log.d(TAG, "syncTaskActivities not inserted: " + taskActivity.getName());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CustomApplication application = (CustomApplication) context;
            String url = CustomApplication.SERVICE_URL + "intranet/android/getUsers";

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

            JSONObject jObject = new JSONObject(jsonStr.toString());
            JSONArray users = jObject.getJSONArray("users");

            for (int i = 0; i < users.length() && !isCancelled(); i++) {
                JSONObject json = users.getJSONObject(i);
                User user = new User();
                user.setId(json.getInt("id"));
                user.setEmail(json.getString("email"));
                user.setPassword(json.getString("password"));
                user.setName(json.getString("username"));
                user.setType(json.getInt("type"));

                String groups = json.getString("group");
                if (groups != null) {
                    String[] groupsArray = groups.split(",");
                    for (int u = 0; u < groupsArray.length && !isCancelled(); u++) {
                        String groupName = groupsArray[u];
                        if (!groupName.equals("")) {
                            user.addGroup(groupName);
                        }
                    }
                }

                user.setModifiedDate(json.getString("date"));
                user.setActive(json.getInt("active") != 0);

                if (application.saveUser(user)) {
                    User loggedUser = application.getUser();
                    if (loggedUser != null && loggedUser.equals(user)) {
                        loggedUser.setType(user.getType());
                        loggedUser.setGroups(user.getGroups());
                    }
                    Log.d(TAG, "syncUsers inserted: " + user.getName());
                }

                float percentage = (float) i / (float) users.length() * 100;
                downloadInfo.percentage = (int) percentage;
                downloadInfo.name = user.getName();
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
