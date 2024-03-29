package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.components.CalendarBridge;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Client;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Notification;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.entities.Type;
import pennsylvania.jahepi.com.apppenns.entities.Ubication;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 24/02/2016.
 * Main service for updating data from server every certain amount of time.
 */
public class Sync extends Service {

    private static final String TAG = "SyncService";

    public static long INTERVAL = 1 * 60 * 1000;
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
                if (application.getLastUser() != null) {
                    getNotifications();
                    getMessages();
                    getReadMessages();
                    syncNewMessages();
                    syncReadMessages();
                    getTaks();
                    syncNewTasks();
                    syncTypes();
                }
                //syncUsers();
                syncFiles();
                syncNewUbications();
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

        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        Log.d(TAG, "syncUsers finalized");
    }

    private void syncTypes() {
        String url = CustomApplication.SERVICE_URL + "intranet/android/getTypes";
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
            JSONArray types = jObject.getJSONArray("types");

            for (int i = 0; i < types.length(); i++) {
                JSONObject json = types.getJSONObject(i);
                Type type = new Type();
                type.setId(json.getInt("id"));
                type.setName(json.getString("name"));
                type.setColor(json.getString("color"));
                type.setCategory(json.getString("category"));
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

    private void getNotifications() {
        String url = null;
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        try {
            url = CustomApplication.SERVICE_URL + "intranet/android/getNotifications/" + application.getLastUser().getId();
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
            JSONArray jsonMessages = jObject.getJSONArray("notifications");

            for (int i = 0; i < jsonMessages.length(); i++) {
                JSONObject json = jsonMessages.getJSONObject(i);
                Notification notification = new Notification();
                notification.setId(json.getInt("id"));
                notification.setFrom(application.getUser(json.getInt("from_user")));
                notification.setTo(application.getUser(json.getInt("to_user")));
                notification.setEventDate(json.getString("date"));
                notification.setNotification(json.getString("text"));
                notification.setFingerprint(json.getString("fingerprint"));
                notification.setMinutes(json.getInt("minutes"));
                notification.setModifiedDate(json.getString("mod_date"));
                notification.setActive(json.getInt("active") == 1);

                if (notification.isValid()) {
                    Notification dbNotification = application.getNotification(notification.getId());
                    if (dbNotification != null) {
                        application.removeEvent(dbNotification.getEventId());
                    }
                    if (notification.isActive()) {
                        int secondNotification = Util.getMinutesDiff(CalendarBridge.START_TIME, notification.getEventTime());
                        long calendarEventId = application.addEvent(notification.getEventDate(), notification.getEventDate(), notification.getFrom().getName(), notification.getNotification(), notification.getMinutes(), secondNotification);
                        notification.setEventId((int) calendarEventId);
                    }
                    if (application.saveNotification(notification)) {
                        try {
                            Task task = application.getTaskByFingerPrint(notification.getFingerprint());
                            String client = "";
                            String date = "";
                            if (task != null) {
                                Client clientObj = task.getClient();
                                date = task.getDate();
                                if (clientObj.getName() != null) {
                                    client = clientObj.getName();
                                }
                            }
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            Util.sendNotification(this, notificationManager, notification.getId(), String.format(getString(R.string.txt_activity_notification), notification.getFrom().getName()), String.format(getString(R.string.txt_activity_notification_message), client, date, notification.getNotification()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "getNotifications inserted: " + notification.getNotification());
                    } else {
                        Log.d(TAG, "getNotifications not inserted: " + notification.getNotification());
                    }
                    if (updateSendNotification(notification)) {
                        notifications.add(notification);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        if (notifications.size() > 0) {
            // application.notifyNewMessages(messages);
        }
        Log.d(TAG, "getNotifications finalized");
    }

    private boolean updateSendNotification(Notification notification) {
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateSyncNotification/" + notification.getId();
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
        Log.d(TAG, "updateSyncNotification finalized");
        return false;
    }

    private void getMessages() {
        String url = null;
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            url = CustomApplication.SERVICE_URL + "intranet/android/getMessages/" + application.getLastUser().getId();
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
                message.setType(application.getType(json.getInt("type")));
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

                if (message.isValid()) {
                    if (application.saveMessage(message)) {
                        Log.d(TAG, "getMessages inserted: " + message.getMessage());
                    } else {
                        Log.d(TAG, "getMessages not inserted: " + message.getMessage());
                    }
                    if (updateSendMessage(message)) {
                        messages.add(message);
                    }
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
        String date = message.getModifiedDateString().replaceAll(" ", "");
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateSyncMessage/" + message.getTo().getId() + "/" + message.getFrom().getId() + "/" + date;
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
            url = CustomApplication.SERVICE_URL + "intranet/android/getReadMessages/" + application.getLastUser().getId();
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
                message.setType(application.getType(json.getInt("type")));
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
        String date = message.getModifiedDateString().replaceAll(" ", "");
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateSyncReadMessage/" + message.getTo().getId() + "/" + message.getFrom().getId() + "/" + date;
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
                post.addPart("message", new StringBody(URLEncoder.encode(message.getMessage(), "UTF-8")));
                post.addPart("to", new StringBody(Integer.toString(message.getTo().getId())));
                post.addPart("from", new StringBody(Integer.toString(message.getFrom().getId())));
                post.addPart("type", new StringBody(Integer.toString(message.getType().getId())));
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
            Log.d(TAG, e.getMessage() == null ? "Not defined" : e.getMessage());
        }

        if (notifyMessages.size() > 0) {
            application.notifySendMessages(messages);
        }
        Log.d(TAG, "syncNewMessages finalized");
    }

    private void syncNewUbications() {
        ArrayList<Ubication> ubications = null;
        try {
            ubications = application.getNewUbications();
            Iterator<Ubication> iterator = ubications.iterator();
            String url = CustomApplication.SERVICE_URL + "intranet/android/newUbication";
            while (iterator.hasNext()) {
                Ubication ubication = (Ubication) iterator.next();
                MultipartEntity post = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                post.addPart("id", new StringBody(Integer.toString(ubication.getId())));
                post.addPart("user", new StringBody(Integer.toString(ubication.getUser().getId())));
                post.addPart("latitude", new StringBody(Double.toString(ubication.getCoord().getLatitude())));
                post.addPart("longitude", new StringBody(Double.toString(ubication.getCoord().getLongitude())));
                post.addPart("date", new StringBody(ubication.getModifiedDateString()));

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
                    if (application.updateUbicationAsSend(ubication)) {
                        Log.d(TAG, "syncNewUbications inserted: " + ubication.getId());
                    } else {
                        Log.d(TAG, "syncNewUbications not inserted: " + ubication.getId());
                    }
                } else {
                    Log.d(TAG, "syncNewUbications code invalid");
                }

            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage() == null ? "Not defined" : e.getMessage());
        }

        Log.d(TAG, "syncNewUbications finalized");
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
                ContentBody fileData = new FileBody(fileRef);

                try {
                    if (fileRef.getName().toLowerCase().endsWith("jpg") || fileRef.getName().toLowerCase().endsWith("jpeg")) {
                        String filePath = fileRef.getAbsolutePath();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                        double width = bitmap.getWidth();
                        double height = bitmap.getHeight();
                        if (width > 1024) {
                            double ratio = width / height;
                            width = 1024;
                            height = width / ratio;
                            bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                        }
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                        InputStream is = new ByteArrayInputStream(bos.toByteArray());
                        fileData = new InputStreamBody(is, "image/jpeg", file.getName());
                    }
                } catch (Exception e) {

                }

                post.addPart("file", fileData);

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
            Log.d(TAG, e.getMessage() == null ? "Not defined" : e.getMessage());
        }
        Log.d(TAG, "syncFiles finalized");
    }

    private void getTaks() {
        String url = null;
        ArrayList<Task> tasks = new ArrayList<Task>();
        try {
            url = CustomApplication.SERVICE_URL + "intranet/android/getTasks/" + application.getLastUser().getId();
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
            JSONArray jsonTasks = jObject.getJSONArray("tasks");

            for (int i = 0; i < jsonTasks.length(); i++) {
                JSONObject json = jsonTasks.getJSONObject(i);
                Task task = new Task();
                task.setUser(application.getUser(json.getInt("user")));
                task.setAddress(application.getAddress(json.getInt("address"), json.getInt("user")));
                task.setDescription(json.getString("description"));
                task.setDate(json.getString("date"));
                task.setModifiedDate(json.getString("mod_date"));
                task.setType(application.getType(json.getInt("type")));
                task.setStartTime(json.getString("start_time"));
                task.setEndTime(json.getString("end_time"));
                task.setFingerprint(json.getString("fingerprint"));
                task.setUpdateAllState(false);
                task.setSend(true);

                if (task.isValid()) {
                    Task dbTask = application.getTask(task);
                    if (dbTask != null) {
                        application.removeEvent(dbTask.getEventId());
                    }
                    int secondNotification = Util.getMinutesDiff(CalendarBridge.START_TIME, task.getStartTime());
                    long calendarEventId = application.addEvent(task.getStartDateTime(), task.getEndDateTime(), task.getClient().getName(), task.getDescription(), CalendarBridge.REMIDER_TIME, secondNotification);
                    task.setEventId((int) calendarEventId);
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
                        task.addAttachment(attachment);
                    }

                    if (application.saveTask(task, false)) {
                        Log.d(TAG, "getTasks inserted: " + task.getFingerprint());
                    } else {
                        Log.d(TAG, "getTasks not inserted: " + task.getFingerprint());
                    }
                    if (updateSendTask(task)) {
                        tasks.add(task);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "URL fail: " + url);
        }
        if (tasks.size() > 0) {
            application.notifyNewTasks(tasks);
        }
        Log.d(TAG, "getTasks finalized");
    }

    private boolean updateSendTask(Task task) {
        String fingerprint = task.getFingerprint().replaceAll(" ", "");
        String url = CustomApplication.SERVICE_URL + "intranet/android/updateSyncTask/" + task.getUser().getId() + "/" + fingerprint;
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
        Log.d(TAG, "updateSendTask finalized");
        return false;
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
                post.addPart("date", new StringBody(task.getModifiedDateString()));
                post.addPart("description", new StringBody(URLEncoder.encode(task.getDescription(), "UTF-8")));
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
                post.addPart("conclusion", new StringBody(URLEncoder.encode(task.getConclusion(), "UTF-8")));
                post.addPart("competence_comment", new StringBody(URLEncoder.encode(task.getCompetenceComments(), "UTF-8")));
                post.addPart("activities", new StringBody(URLEncoder.encode(task.getTaskActivitiesText(), "UTF-8")));
                post.addPart("products", new StringBody(URLEncoder.encode(task.getTaskProductsText(), "UTF-8")));
                post.addPart("emails", new StringBody(task.getEmails()));
                post.addPart("fingerprint", new StringBody(task.getFingerprint()));
                post.addPart("cancelled", new StringBody(task.isCancelled() ? "1" : "0"));
                post.addPart("status", new StringBody(task.getStatus()));
                Task parentTask = task.getParentTask();
                if (parentTask != null) {
                    if (parentTask.getFingerprint() != null) {
                        post.addPart("parent_fingerprint", new StringBody(parentTask.getFingerprint()));
                    } else {
                        post.addPart("parent_fingerprint", new StringBody(""));
                    }
                }

                Iterator<Attachment> attachmentIterator = task.getAttachmentsIterator();
                while (attachmentIterator.hasNext()) {
                    Attachment attachment = attachmentIterator.next();
                    post.addPart("attachment_name[]", new StringBody(attachment.getFile().getName()));
                    post.addPart("attachment_path[]", new StringBody(attachment.getFile().getPath()));
                    post.addPart("attachment_mime[]", new StringBody(attachment.getFile().getMime()));
                    post.addPart("attachment_date[]", new StringBody(attachment.getFile().getModifiedDateString()));
                    post.addPart("attachment_from_conclusion[]", new StringBody(attachment.isFromConclusion() ? "1" : "0"));
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
            Log.d(TAG, e.getMessage() == null ? "Not defined" : e.getMessage());
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
                post.addPart("type", new StringBody(Integer.toString(message.getType().getId())));
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
            Log.d(TAG, e.getMessage() == null ? "Not defined" : e.getMessage());
        }
        Log.d(TAG, "syncReadMessages finalized");
    }

    public boolean isActive() {
        return active;
    }
}
