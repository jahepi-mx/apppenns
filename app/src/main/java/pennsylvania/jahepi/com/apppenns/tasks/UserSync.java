package pennsylvania.jahepi.com.apppenns.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.entities.TaskActivity;
import pennsylvania.jahepi.com.apppenns.entities.Type;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 05/08/2016.
 * Asynchronous task for updating user data from server
 */
public class UserSync extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "UserSync";

    private Context context;

    public UserSync(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (!isCancelled()) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            syncTaskActivities();
            getUsers();
        }
        return null;
    }

    private void syncTaskActivities() {

        try {
            CustomApplication application = (CustomApplication) context;
            String url = CustomApplication.SERVICE_URL + "intranet/android/getTaskActivities/" + application.getUser().getId();
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

            application.deleteActivities();
            JSONObject jObject = new JSONObject(jsonStr.toString());
            JSONArray activities = jObject.getJSONArray("taskActivities");

            for (int i = 0; i < activities.length(); i++) {
                JSONObject json = activities.getJSONObject(i);
                TaskActivity taskActivity = new TaskActivity();
                taskActivity.setId(json.getInt("id"));
                taskActivity.setName(json.getString("name"));
                taskActivity.setUser(application.getUser());
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
    }

    private void getUsers() {
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

            for (int i = 0; i < users.length(); i++) {
                JSONObject json = users.getJSONObject(i);
                User user = new User();
                user.setId(json.getInt("id"));
                user.setEmail(json.getString("email"));
                user.setPassword(json.getString("password"));
                user.setName(json.getString("username"));

                String groups = json.getString("group");
                if (groups != null) {
                    String[] groupsArray = groups.split(",");
                    for (int u = 0; u < groupsArray.length; u++) {
                        String groupName = groupsArray[u];
                        if (!groupName.equals("")) {
                            user.addGroup(groupName);
                        }
                    }
                }

                user.setModifiedDate(json.getString("date"));
                user.setActive(json.getInt("active") != 0);

                if (application.saveUser(user)) {
                    Log.d(TAG, "syncUsers inserted: " + user.getName());
                } else {
                    Log.d(TAG, "syncUsers not inserted: " + user.getName());
                }
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
