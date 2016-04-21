package pennsylvania.jahepi.com.apppenns;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.nullwire.trace.ExceptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import pennsylvania.jahepi.com.apppenns.components.CalendarBridge;
import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Client;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Notification;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.entities.Type;
import pennsylvania.jahepi.com.apppenns.entities.Ubication;
import pennsylvania.jahepi.com.apppenns.entities.User;
import pennsylvania.jahepi.com.apppenns.model.Dao;
import pennsylvania.jahepi.com.apppenns.services.Gps;
import pennsylvania.jahepi.com.apppenns.tasks.Sync;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class CustomApplication extends Application {

    public final static double VERSION = 1.0;
    public final static String SERVICE_URL = "http://portal.pennsylvania.com.mx/";
    public final static String EXCEPTION_HANDLER_URL = "http://portal.pennsylvania.com.mx/log/exception.php";
    public final static File SDCARD_PATH = Environment.getExternalStorageDirectory();
    public final static String TAG = "CustomApplication";
    public final static String PREFS_NAME = "com.pennsylvania.jahepi.preferences";
    public final static String GENERIC_INTENT = "GENERIC_INTENT";
    public final static String ADDITIONAL_GENERIC_INTENT = "ADDITIONAL_GENERIC_INTENT";
    public final static String PREF_USER_EMAIL = "PREF_USER_EMAIL";
    public final static String PREF_USER_ID = "PREF_USER_ID";
    public final static String PREF_LAST_USER_ID = "PREF_LAST_USER_ID";

    private boolean logged;
    private boolean syncActive;
    private Gps gps;
    private Dao dao;
    private CalendarBridge calendarBridge;
    private User user;
    private String androidId;
    private ArrayList<ApplicationNotifierListener> appNotifierListeners;

    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionHandler.register(this, EXCEPTION_HANDLER_URL);
        logged = false;
        androidId = Util.getAndroidId(this);
        gps = new Gps(this);
        dao = new Dao(getApplicationContext());
        gps.start();
        calendarBridge = new CalendarBridge(this);
        appNotifierListeners = new ArrayList<ApplicationNotifierListener>();
        startSync();
    }

    private void startSync() {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, Sync.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), Sync.INTERVAL, pintent);
    }

    public String getStoredUserEmail() {
        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PREF_USER_EMAIL, "");
    }

    public int getStoredUserId() {
        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(PREF_USER_ID, 0);
    }

    public int getStoredLastUserId() {
        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(PREF_LAST_USER_ID, 0);
    }

    public boolean login(String email, String password) {
        password = Util.SHA1(password);
        User user = dao.getUser(email, password);
        logged = (user != null);
        if (logged) {
            this.user = user;
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_USER_EMAIL, user.getEmail());
            editor.putInt(PREF_USER_ID, user.getId());
            editor.putInt(PREF_LAST_USER_ID, user.getId());
            editor.commit();
        }
        return logged;
    }

    public void logout() {
        user = null;
        logged = false;
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREF_USER_ID);
        editor.apply();
    }

    public long addEvent(String startDateTime, String endDateTime, String title, String description) {
        return calendarBridge.addEvent(startDateTime, endDateTime, title, description);
    }

    public boolean removeEvent(int id) {
        return calendarBridge.removeEvent(id);
    }

    public void startCalendar(Context context, String date) {
        calendarBridge.startCalendar(context, date);
    }

    public User getUser() {
        if (user == null) {
            user = dao.getUser(getStoredUserId());
        }
        return user;
    }

    public boolean saveUser(User user) {
        return dao.saveUser(user);
    }

    public boolean isSyncActive() {
        return true;
    }

    public boolean isLogged() {
        if (getUser() == null) {
            logout();
        } else {
            logged = true;
        }
        return logged;
    }

    public String getAndroidId() {
        return androidId;
    }

    public double getLatitude() {
        return gps.getLatitude();
    }

    public double getLongitude() {
        return gps.getLongitude();
    }

    public boolean isGpsEnabled() {
        return gps.isEnabled();
    }

    public void setSyncActive(boolean syncActive) {
        this.syncActive = syncActive;
    }

    public int getNoReadMessagesTotal() {
        return dao.getNoReadMessagesTotal(getUser().getId());
    }

    public User getUser(int userId) {
        return dao.getUser(userId);
    }

    public ArrayList<User> getUsers() {
        return dao.getUsers();
    }

    public Notification getNotification(int id) {
        return dao.getNotification(id);
    }

    public boolean saveNotification(Notification notification) {
        return dao.saveNotification(notification);
    }

    public boolean saveMessage(Message message) {
        return dao.saveMessage(message);
    }

    public ArrayList<Message> getMessages(String date) {
        return dao.getMessages(getUser().getId(), date);
    }

    public Task getTask(Task task) {
        return dao.getTask(task);
    }

    public ArrayList<Task> getNewTasks() {
        return dao.getNewTasks(getUser().getId());
    }

    public ArrayList<Task> getTasks(String date) {
        return dao.getTasks(getUser().getId(), date);
    }

    public boolean saveTask(Task task) {
        return dao.saveTask(task);
    }

    public boolean updateTaskAsSend(Task task) {
        return dao.updateTaskAsSend(task);
    }

    public boolean saveType(Type type) {
        return dao.saveType(type);
    }

    public ArrayList<Type> getTypes(String category) {
        return dao.getTypes(category);
    }

    public Type getType(int typeId) {
        return dao.getType(typeId);
    }

    public boolean saveFile(Attachment.File file) {
        return dao.saveFile(file);
    }

    public ArrayList<Attachment.File> getNotSendFiles() {
        return dao.getNotSendFiles();
    }

    public boolean updateFileAsSend(Attachment.File file) {
        return dao.updateFileAsSend(file);
    }

    public void notifyNewMessages(ArrayList<Message> messages) {
        for (ApplicationNotifierListener listener : appNotifierListeners) {
            if (listener != null) {
                listener.onNewMessages(messages);
            }
        }
    }

    public void notifySendMessages(ArrayList<Message> messages) {
        for (ApplicationNotifierListener listener : appNotifierListeners) {
            if (listener != null) {
                listener.onMessagesSend(messages);
            }
        }
    }

    public void notifyReadMessages(ArrayList<Message> messages) {
        for (ApplicationNotifierListener listener : appNotifierListeners) {
            if (listener != null) {
                listener.onMessagesRead(messages);
            }
        }
    }

    public void notifyNewTasks(ArrayList<Task> tasks) {
        for (ApplicationNotifierListener listener : appNotifierListeners) {
            if (listener != null) {
                listener.onNewTasks(tasks);
            }
        }
    }

    public void notifySendTasks(ArrayList<Task> tasks) {
        for (ApplicationNotifierListener listener : appNotifierListeners) {
            if (listener != null) {
                listener.onTasksSend(tasks);
            }
        }
    }

    public void onChangeLocation(double latitude, double longitude) {
        int userId = getStoredLastUserId();
        if (userId > 0) {
            Ubication ubication = new Ubication();
            User user = new User();
            user.setId(userId);
            ubication.setUser(user);
            ubication.setSend(false);
            ubication.setCoord(latitude, longitude);
            ubication.setModifiedDate(Util.getDateTime());
            dao.saveUbication(ubication);
        }
        for (ApplicationNotifierListener listener : appNotifierListeners) {
            if (listener != null) {
                listener.onChangeLocation(latitude, longitude);
            }
        }
    }

    public ArrayList<Message> getMessagesRead() {
        return dao.getMessagesRead(getUser().getId());
    }

    public ArrayList<Ubication> getNewUbications() {
        return dao.getNewUbications();
    }

    public boolean updateUbicationAsSend(Ubication ubication) {
        return dao.updateUbicationAsSend(ubication);
    }

    public ArrayList<Message> getNewMessages() {
        return dao.getNewMessages(getUser().getId());
    }

    public boolean updateMessageField(Message message, String field, String value) {
        return dao.updateMessageField(message, field, value);
    }

    public boolean saveClient(Client client) {
        return dao.saveClient(client);
    }

    public boolean saveAddress(Address address) {
        return dao.saveAddress(address);
    }

    public ArrayList<Client> getClients(String name) {
        return dao.getClients(getUser().getId(), name);
    }

    public ArrayList<String> getUserEmails(String keyword) {
        return dao.getUserEmails(keyword);
    }

    public Address getAddress(int addressId, int userId) {
        return dao.getAddress(addressId, userId);
    }

    public ArrayList<Address> getAddresses(Client client) {
        return dao.getAddresses(client);
    }

    public void addMessageNotifierListener(ApplicationNotifierListener appNotifierListener) {
        if (!appNotifierListeners.contains(appNotifierListener)) {
            appNotifierListeners.add(appNotifierListener);
        }
    }

    public void removeMessageNotifierListener(ApplicationNotifierListener appNotifierListener) {
        appNotifierListeners.remove(appNotifierListener);
    }

    public interface ApplicationNotifierListener {
        public void onNewMessages(ArrayList<Message> messages);
        public void onMessagesSend(ArrayList<Message> messages);
        public void onMessagesRead(ArrayList<Message> messages);
        public void onTasksSend(ArrayList<Task> tasks);
        public void onNewTasks(ArrayList<Task> tasks);
        public void onChangeLocation(double latitude, double longitude);
    }
}
