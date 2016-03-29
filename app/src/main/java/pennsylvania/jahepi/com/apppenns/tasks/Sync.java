package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.entities.Type;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class Sync extends Service {

    private static final String TAG = "SyncService";

    public static long INTERVAL = 1 * 30 * 1000;
    private static final int SUCCESS = 1;

    private CustomApplication application;
    private boolean active;

    @Override
    public void onCreate() {
        super.onCreate();
        application = (CustomApplication) getApplication();
        active = false;
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
                    syncFiles();
                    getMessages();
                    getReadMessages();
                    syncNewMessages();
                    syncReadMessages();
                    syncNewTasks();
                    syncTypes();
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
                user.setGroup(json.getString("group"));
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

    private void syncTypes() {
        String url = CustomApplication.SERVICE_URL + "intranet/android/getActivityTypes";
        try {
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
            JSONArray types = jObject.getJSONArray("activity_types");

            for (int i = 0; i < types.length(); i++) {
                JSONObject json = types.getJSONObject(i);
                Type type = new Type();
                type.setId(json.getInt("id"));
                type.setName(json.getString("name"));
                type.setModifiedDate(json.getString("date"));
                type.setActive(json.getInt("active") != 0);

                if (application.saveType(type)) {
                    Log.d(TAG, "syncTypes inserted: " + type.getName());
                } else {
                    Log.d(TAG, "syncTypes not inserted: " + type.getName());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        Log.d(TAG, "syncTypes finalized");
    }

    private void getMessages() {
        String url = null;
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            url = CustomApplication.SERVICE_URL + "intranet/android/getMessages/" + application.getUser().getId();
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

                JSONArray jsonAttachments = json.getJSONArray("attachments");
                for (int e = 0; e < jsonAttachments.length(); e++) {
                    JSONObject jsonAttachment = jsonAttachments.getJSONObject(e);
                    Attachment attachment = new Attachment();
                    Attachment.File file = new Attachment.File();
                    file.setName(jsonAttachment.getString("name"));
                    file.setPath(jsonAttachment.getString("path"));
                    file.setMime(jsonAttachment.getString("mime"));
                    file.setModifiedDate(jsonAttachment.getString("date"));
                    file.setSend(true);
                    attachment.setFile(file);
                    message.addAttachment(attachment);
                }

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
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateSyncMessage/" + message.getTo().getId() + "/" + message.getFrom().getId() + "/" + message.getModifiedDateString();
        try {
            URL urlRef = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlRef.openConnection();
            connection.setDoInput(true);
            connection.connect();
            connection.getInputStream();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        Log.d(TAG, "updateSyncMessage finalized");
        return false;
    }

    private void getReadMessages() {
        String url = null;
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            url = CustomApplication.SERVICE_URL + "intranet/android/getReadMessages/" + application.getUser().getId();
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
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateSyncReadMessage/" + message.getTo().getId() + "/" + message.getFrom().getId() + "/" + message.getModifiedDateString();
        try {
            URL urlRef = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlRef.openConnection();
            connection.setDoInput(true);
            connection.connect();
            connection.getInputStream();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        Log.d(TAG, "updateReadSyncMessage finalized");
        return false;
    }

    private void syncNewMessages() {
        ArrayList<Message> notifyMessages = new ArrayList<Message>();
        ArrayList<Message> messages = null;
        try {
            messages = application.getNewMessages();
            Iterator<Message> iterator = messages.iterator();
            String url = CustomApplication.SERVICE_URL + "intranet/android/newMessage";
            while (iterator.hasNext()) {
                Message message = (Message) iterator.next();
                MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                post.addPart("message", new StringBody(message.getMessage()));
                post.addPart("to", new StringBody(Integer.toString(message.getTo().getId())));
                post.addPart("from", new StringBody(Integer.toString(message.getFrom().getId())));
                post.addPart("date", new StringBody(message.getModifiedDateString()));

                Iterator<Attachment> attachmentIterator = message.getAttachmentsIterator();
                while (attachmentIterator.hasNext()) {
                    Attachment attachment = attachmentIterator.next();
                    post.addPart("attachment_name[]", new StringBody(attachment.getFile().getName()));
                    post.addPart("attachment_path[]", new StringBody(attachment.getFile().getPath()));
                    post.addPart("attachment_mime[]", new StringBody(attachment.getFile().getMime()));
                    post.addPart("attachment_date[]", new StringBody(attachment.getFile().getModifiedDateString()));
                }

                URL urlRef = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlRef.openConnection();
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.addRequestProperty("Content-length", post.getContentLength() + "");
                connection.addRequestProperty(post.getContentType().getName(), post.getContentType().getValue());

                OutputStream os = connection.getOutputStream();
                post.writeTo(connection.getOutputStream());
                os.close();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuilder jsonStr = new StringBuilder();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    jsonStr = jsonStr.append(line);
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

            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        if (notifyMessages.size() > 0) {
            application.notifySendMessages(messages);
        }
        Log.d(TAG, "syncNewMessages finalized");
    }

    private void syncFiles() {
        try {
            ArrayList<Attachment.File> files = application.getNotSendFiles();
            Iterator<Attachment.File> iterator = files.iterator();
            String url = CustomApplication.SERVICE_URL + "intranet/android/uploadFile";
            while (iterator.hasNext()) {
                Attachment.File file = iterator.next();
                MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                File fileRef = new File(file.getPathNoName(), file.getName());
                post.addPart("file", new FileBody(fileRef));

                URL urlRef = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlRef.openConnection();
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.addRequestProperty("Content-length", post.getContentLength() + "");
                connection.addRequestProperty(post.getContentType().getName(), post.getContentType().getValue());
                int code = 0;

                try {
                    OutputStream os = connection.getOutputStream();
                    post.writeTo(connection.getOutputStream());
                    os.close();
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    StringBuilder jsonStr = new StringBuilder();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        jsonStr = jsonStr.append(line);
                    }

                    JSONObject jObject = new JSONObject(jsonStr.toString());
                    String messageStr = jObject.getString("message");
                    code = jObject.getInt("code");
                } catch (FileNotFoundException exp) {
                    code = SUCCESS;
                }

                if (code == SUCCESS) {
                    if (application.updateFileAsSend(file)) {
                        Log.d(TAG, "syncFiles inserted: " + file.getName());
                    } else {
                        Log.d(TAG, "syncFiles not inserted: " + file.getName());
                    }
                } else {
                    Log.d(TAG, "syncFiles code invalid");
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        Log.d(TAG, "syncFiles finalized");
    }

    private void syncNewTasks() {
        ArrayList<Task> notifyTasks = new ArrayList<Task>();
        ArrayList<Task> tasks = null;
        try {
            tasks = application.getNewTasks();
            Iterator<Task> iterator = tasks.iterator();
            String url = CustomApplication.SERVICE_URL + "intranet/android/newTask";
            while (iterator.hasNext()) {
                Task task = (Task) iterator.next();
                MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                post.addPart("id", new StringBody(Integer.toString(task.getId())));
                post.addPart("user", new StringBody(Integer.toString(task.getUser().getId())));
                post.addPart("address", new StringBody(Integer.toString(task.getAddress().getId())));
                post.addPart("type", new StringBody(Integer.toString(task.getType().getId())));
                post.addPart("description", new StringBody(task.getDescription()));
                post.addPart("register_date", new StringBody(task.getDate()));
                post.addPart("start_time", new StringBody(task.getStartTime()));
                post.addPart("end_time", new StringBody(task.getEndTime()));
                post.addPart("checkin_lat", new StringBody(Double.toString(task.getCheckInCoord().getLatitude())));
                post.addPart("checkin_lon", new StringBody(Double.toString(task.getCheckInCoord().getLongitude())));
                post.addPart("checkout_lat", new StringBody(Double.toString(task.getCheckOutCoord().getLatitude())));
                post.addPart("checkout_lon", new StringBody(Double.toString(task.getCheckOutCoord().getLongitude())));
                post.addPart("check_in", new StringBody(Boolean.toString(task.isCheckin())));
                post.addPart("check_out", new StringBody(Boolean.toString(task.isCheckout())));
                post.addPart("checkin_date", new StringBody(task.getCheckInDate()));
                post.addPart("checkout_date", new StringBody(task.getCheckOutDate()));
                post.addPart("conclusion", new StringBody(task.getConclusion()));
                post.addPart("emails", new StringBody(task.getEmails()));
                post.addPart("cancelled", new StringBody(task.isCancelled() ? "1" : "0"));

                Iterator<Attachment> attachmentIterator = task.getAttachmentsIterator();
                while (attachmentIterator.hasNext()) {
                    Attachment attachment = attachmentIterator.next();
                    post.addPart("attachment_name[]", new StringBody(attachment.getFile().getName()));
                    post.addPart("attachment_path[]", new StringBody(attachment.getFile().getPath()));
                    post.addPart("attachment_mime[]", new StringBody(attachment.getFile().getMime()));
                    post.addPart("attachment_date[]", new StringBody(attachment.getFile().getModifiedDateString()));
                }

                URL urlRef = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlRef.openConnection();
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.addRequestProperty("Content-length", post.getContentLength() + "");
                connection.addRequestProperty(post.getContentType().getName(), post.getContentType().getValue());

                OutputStream os = connection.getOutputStream();
                post.writeTo(connection.getOutputStream());
                os.close();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuilder jsonStr = new StringBuilder();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    jsonStr = jsonStr.append(line);
                }

                JSONObject jObject = new JSONObject(jsonStr.toString());
                String messageStr = jObject.getString("message");
                int code = jObject.getInt("code");

                if (code == SUCCESS) {
                    if (application.updateTaskAsSend(task)) {
                        task.setSend(true);
                        notifyTasks.add(task);
                        Log.d(TAG, "syncNewTasks inserted: " + task.getClient().getName());
                    } else {
                        Log.d(TAG, "syncNewTasks not inserted: " + task.getClient().getName());
                    }
                } else {
                    Log.d(TAG, "syncNewTasks code invalid");
                }

            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        if (notifyTasks.size() > 0) {
            application.notifySendTasks(tasks);
        }
        Log.d(TAG, "syncNewTasks finalized");
    }

    private void syncReadMessages() {
        try {
            ArrayList<Message> messages = application.getMessagesRead();
            Iterator<Message> iterator = messages.iterator();
            String url = CustomApplication.SERVICE_URL + "intranet/android/updateReadMessage";
            while (iterator.hasNext()) {
                Message message = (Message) iterator.next();
                MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                post.addPart("message", new StringBody(message.getMessage()));
                post.addPart("to", new StringBody(Integer.toString(message.getTo().getId())));
                post.addPart("from", new StringBody(Integer.toString(message.getFrom().getId())));
                post.addPart("date", new StringBody(message.getModifiedDateString()));

                URL urlRef = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlRef.openConnection();
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.addRequestProperty("Content-length", post.getContentLength() + "");
                connection.addRequestProperty(post.getContentType().getName(), post.getContentType().getValue());

                OutputStream os = connection.getOutputStream();
                post.writeTo(connection.getOutputStream());
                os.close();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuilder jsonStr = new StringBuilder();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    jsonStr = jsonStr.append(line);
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

            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        Log.d(TAG, "syncReadMessages finalized");
    }

    public boolean isActive() {
        return active;
    }
}
