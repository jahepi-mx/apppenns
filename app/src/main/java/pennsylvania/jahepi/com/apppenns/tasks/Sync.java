package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class Sync extends Service {

    private static final String TAG = "SyncService";

    public static long INTERVAL = 30000;
    private static final int SUCCESS = 1;

    private CustomApplication application;
    private boolean active;
    private HttpClient httpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        application = (CustomApplication) getApplication();
        active = false;
        httpClient = new DefaultHttpClient();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       super.onStartCommand(intent, flags, startId);
        if (!active) {
            active = true;
            application.setSyncActive(active);
            run();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        active = false;
        application.setSyncActive(active);
    }

    private void run() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                if (application.isLogged()) {
                    getMessages();
                    getReadMessages();
                    syncNewMessages();
                    syncReadMessages();
                    syncNewTasks();
                }
                syncUsers();
                active = false;
                application.setSyncActive(active);
            }
        });
        thread.start();
    }

    private void syncUsers() {

        String url = CustomApplication.SERVICE_URL + "intranet/android/getUsers";

        try {
            HttpPost postRequest = new HttpPost(url);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String sResponse = null;
            StringBuilder jsonStr = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                jsonStr = jsonStr.append(sResponse);
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
                user.setModifiedDate(json.getString("date"));
                user.setActive(json.getInt("active") != 0);

                if (application.saveUser(user)) {
                    Log.d(TAG, "syncUsers inserted: " + user.getName());
                } else {
                    Log.d(TAG, "syncUsers not inserted: " + user.getName());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        Log.d(TAG, "syncUsers finalized");
    }

    private void getMessages() {
        String url = CustomApplication.SERVICE_URL + "intranet/android/getMessages/" + application.getUser().getId();
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            HttpPost postRequest = new HttpPost(url);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String sResponse = null;
            StringBuilder jsonStr = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                jsonStr = jsonStr.append(sResponse);
            }

            JSONObject jObject = new JSONObject(jsonStr.toString());
            JSONArray jsonMessages = jObject.getJSONArray("messages");

            for (int i = 0; i < jsonMessages.length(); i++) {
                JSONObject json = jsonMessages.getJSONObject(i);
                Message message = new Message();
                message.setMessage(json.getString("message"));
                message.setFrom(application.getUser(json.getInt("from")));
                message.setTo(application.getUser(json.getInt("to")));
                message.setModifiedDate(json.getString("date"));
                message.setActive(true);
                message.setRead(false);
                message.setDelivered(true);
                message.setSend(true);

                if (application.saveMessage(message)) {
                    Log.d(TAG, "getMessages inserted: " + message.getMessage());
                } else {
                    Log.d(TAG, "getMessages not inserted: " + message.getMessage());
                }
                if (updateSendMessage(message)) {
                    messages.add(message);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        if (messages.size() > 0) {
            application.notifyNewMessages(messages);
        }
        Log.d(TAG, "getMessages finalized");
    }

    private boolean updateSendMessage(Message message) {
        String date = "";
        try {
            date = URLEncoder.encode(message.getModifiedDateString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateSyncMessage/" + message.getTo().getId() + "/" + message.getFrom().getId() + "/" + date;
        try {
            HttpPost postRequest = new HttpPost(url);
            httpClient.execute(postRequest);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        Log.d(TAG, "updateSyncMessage finalized");
        return false;
    }

    private void getReadMessages() {
        String url = CustomApplication.SERVICE_URL + "intranet/android/getReadMessages/" + application.getUser().getId();
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            HttpPost postRequest = new HttpPost(url);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String sResponse = null;
            StringBuilder jsonStr = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                jsonStr = jsonStr.append(sResponse);
            }

            JSONObject jObject = new JSONObject(jsonStr.toString());
            JSONArray jsonMessages = jObject.getJSONArray("messages");

            for (int i = 0; i < jsonMessages.length(); i++) {
                JSONObject json = jsonMessages.getJSONObject(i);
                Message message = new Message();
                message.setMessage(json.getString("message"));
                message.setFrom(application.getUser(json.getInt("from")));
                message.setTo(application.getUser(json.getInt("to")));
                message.setModifiedDate(json.getString("date"));
                if (updateDeliveredMessage(message)) {
                    if (application.updateMessageField(message, "delivered", "1")) {
                        message.setDelivered(true);
                        messages.add(message);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        if (messages.size() > 0) {
            application.notifyReadMessages(messages);
        }
        Log.d(TAG, "getReadMessages finalized");
    }

    private boolean updateDeliveredMessage(Message message) {
        String date = "";
        try {
            date = URLEncoder.encode(message.getModifiedDateString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateSyncReadMessage/" + message.getTo().getId() + "/" + message.getFrom().getId() + "/" + date;
        try {
            HttpPost postRequest = new HttpPost(url);
            httpClient.execute(postRequest);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        Log.d(TAG, "updateReadSyncMessage finalized");
        return false;
    }

    private void syncNewMessages() {
        ArrayList<Message> notifyMessages = new ArrayList<Message>();
        ArrayList<Message> messages = application.getNewMessages();
        Iterator<Message> iterator = messages.iterator();
        String url = CustomApplication.SERVICE_URL + "intranet/android/newMessage";
        while (iterator.hasNext()) {
            try {
                Message message = (Message) iterator.next();
                HttpPost postRequest = new HttpPost(url);
                MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                post.addPart("message", new StringBody(message.getMessage(), Charset.forName(HTTP.UTF_8)));
                post.addPart("to", new StringBody(Integer.toString(message.getTo().getId())));
                post.addPart("from", new StringBody(Integer.toString(message.getFrom().getId())));
                post.addPart("date", new StringBody(message.getModifiedDateString()));
                postRequest.setEntity(post);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String sResponse = null;
                StringBuilder jsonStr = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    jsonStr = jsonStr.append(sResponse);
                }

                JSONObject jObject = new JSONObject(jsonStr.toString());
                String messageStr = jObject.getString("message");
                int code = jObject.getInt("code");

                if (code == SUCCESS) {
                    if (application.updateMessageField(message, "send", "1")) {
                        message.setSend(true);
                        notifyMessages.add(message);
                        Log.d(TAG, "syncNewMessages inserted: " + message.getMessage());
                    } else {
                        Log.d(TAG, "syncNewMessages not inserted: " + message.getMessage());
                    }
                } else {
                    Log.d(TAG, "syncNewMessages code invalid");
                }

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }

        if (notifyMessages.size() > 0) {
            application.notifySendMessages(messages);
        }
        Log.d(TAG, "syncNewMessages finalized");
    }

    private void syncNewTasks() {
        ArrayList<Task> notifyTasks = new ArrayList<Task>();
        ArrayList<Task> tasks = application.getNewTasks();
        Iterator<Task> iterator = tasks.iterator();
        String url = CustomApplication.SERVICE_URL + "intranet/android/newTask";
        while (iterator.hasNext()) {
            try {
                Task task = (Task) iterator.next();
                HttpPost postRequest = new HttpPost(url);
                MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                post.addPart("id", new StringBody(Integer.toString(task.getId())));
                post.addPart("user", new StringBody(Integer.toString(task.getUser().getId())));
                post.addPart("client", new StringBody(task.getClient()));
                post.addPart("description", new StringBody(task.getDescription()));
                post.addPart("register_date", new StringBody(task.getDate()));
                post.addPart("checkin_lat", new StringBody(Double.toString(task.getCheckInCoord().getLatitude())));
                post.addPart("checkin_lon", new StringBody(Double.toString(task.getCheckInCoord().getLongitude())));
                post.addPart("checkout_lat", new StringBody(Double.toString(task.getCheckOutCoord().getLatitude())));
                post.addPart("checkout_lon", new StringBody(Double.toString(task.getCheckOutCoord().getLongitude())));
                post.addPart("check_in", new StringBody(Boolean.toString(task.isCheckin())));
                post.addPart("check_out", new StringBody(Boolean.toString(task.isCheckout())));
                post.addPart("checkin_date", new StringBody(task.getCheckInDate()));
                post.addPart("checkout_date", new StringBody(task.getCheckOutDate()));
                post.addPart("conclusion", new StringBody(task.getConclusion()));
                postRequest.setEntity(post);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String sResponse = null;
                StringBuilder jsonStr = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    jsonStr = jsonStr.append(sResponse);
                }

                JSONObject jObject = new JSONObject(jsonStr.toString());
                String messageStr = jObject.getString("message");
                int code = jObject.getInt("code");

                if (code == SUCCESS) {
                    if (application.updateTaskAsSend(task)) {
                        task.setSend(true);
                        notifyTasks.add(task);
                        Log.d(TAG, "syncNewTasks inserted: " + task.getClient());
                    } else {
                        Log.d(TAG, "syncNewTasks not inserted: " + task.getClient());
                    }
                } else {
                    Log.d(TAG, "syncNewTasks code invalid");
                }

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
        if (notifyTasks.size() > 0) {
            application.notifySendTasks(tasks);
        }
        Log.d(TAG, "syncNewTasks finalized");
    }

    private void syncReadMessages() {
        ArrayList<Message> messages = application.getMessagesRead();
        Iterator<Message> iterator = messages.iterator();
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateReadMessage";
        while (iterator.hasNext()) {
            try {
                Message message = (Message) iterator.next();
                HttpPost postRequest = new HttpPost(url);
                MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                post.addPart("message", new StringBody(message.getMessage(), Charset.forName(HTTP.UTF_8)));
                post.addPart("to", new StringBody(Integer.toString(message.getTo().getId())));
                post.addPart("from", new StringBody(Integer.toString(message.getFrom().getId())));
                post.addPart("date", new StringBody(message.getModifiedDateString()));
                postRequest.setEntity(post);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String sResponse = null;
                StringBuilder jsonStr = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    jsonStr = jsonStr.append(sResponse);
                }

                JSONObject jObject = new JSONObject(jsonStr.toString());
                String messageStr = jObject.getString("message");
                int code = jObject.getInt("code");

                if (code == SUCCESS) {
                    if (application.updateMessageField(message, "read_sync", "1")) {
                        Log.d(TAG, "syncReadMessages inserted: " + message.getMessage());
                    } else {
                        Log.d(TAG, "syncReadMessages not inserted: " + message.getMessage());
                    }
                } else {
                    Log.d(TAG, "syncReadMessages code invalid");
                }

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
        Log.d(TAG, "syncReadMessages finalized");
    }

    public boolean isActive() {
        return active;
    }
}
