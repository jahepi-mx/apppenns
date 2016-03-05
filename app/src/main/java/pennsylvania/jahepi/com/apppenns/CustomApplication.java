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
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Task;
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
    public final static String PREF_USER_EMAIL = "PREF_USER_EMAIL";

    private boolean logged;
    private boolean syncActive;
    private Gps gps;
    private Dao dao;
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
        dao = new Dao(this.getApplicationContext());
        gps.start();
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

    public String getUserEmail() {
        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PREF_USER_EMAIL, "");
    }

    public boolean login(String email, String password) {
        password = Util.SHA1(password);
        User user = dao.getUser(email, password);
        if (user != null) {
            this.user = user;
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_USER_EMAIL, user.getEmail());
            editor.commit();
        }
        logged = (user != null);
        return logged;
    }

    public void logout() {
        user = null;
        logged = false;
    }

    public User getUser() {
        return user;
    }

    public boolean saveUser(User user) {
        return dao.saveUser(user);
    }

    public boolean isSyncActive() {
        return true;
    }

    public boolean isLogged() {
        if (user == null) {
            logged = false;
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

    public void setSyncActive(boolean syncActive) {
        this.syncActive = syncActive;
    }

    public int getNoReadMessagesTotal() {
        return dao.getNoReadMessagesTotal(user.getId());
    }

    public User getUser(int userId) {
        return dao.getUser(userId);
    }

    public ArrayList<User> getUsers() {
        return dao.getUsers();
    }

    public boolean saveMessage(Message message) {
        return dao.saveMessage(message);
    }

    public ArrayList<Message> getMessages() {
        return dao.getMessages(user.getId());
    }

    public ArrayList<Task> getNewTasks() {
        return dao.getNewTasks(user.getId());
    }

    public boolean saveTask(Task task) {
        return dao.saveTask(task);
    }

    public boolean updateTaskAsSend(Task task) {
        return dao.updateTaskAsSend(task);
    }

    public void notifyNewMessages(ArrayList<Message> messages) {
        Iterator<ApplicationNotifierListener> iterator = appNotifierListeners.iterator();
        while (iterator.hasNext()) {
            ApplicationNotifierListener listener = iterator.next();
            if (listener != null) {
                listener.onNewMessages(messages);
            }
        }
    }

    public void notifySendMessages(ArrayList<Message> messages) {
        Iterator<ApplicationNotifierListener> iterator = appNotifierListeners.iterator();
        while (iterator.hasNext()) {
            ApplicationNotifierListener listener = iterator.next();
            if (listener != null) {
                listener.onMessagesSend(messages);
            }
        }
    }

    public void notifyReadMessages(ArrayList<Message> messages) {
        Iterator<ApplicationNotifierListener> iterator = appNotifierListeners.iterator();
        while (iterator.hasNext()) {
            ApplicationNotifierListener listener = iterator.next();
            if (listener != null) {
                listener.onMessagesRead(messages);
            }
        }
    }

    public ArrayList<Message> getMessagesRead() {
        return dao.getMessagesRead(user.getId());
    }

    public ArrayList<Message> getNewMessages() {
        return dao.getNewMessages(user.getId());
    }

    public boolean updateMessageField(Message message, String field, String value) {
        return dao.updateMessageField(message, field, value);
    }

    public void addMessageNotifierListener(int index, ApplicationNotifierListener appNotifierListener) {
        this.appNotifierListeners.add(index, appNotifierListener);
    }

    public interface ApplicationNotifierListener {
        public void onNewMessages(ArrayList<Message> messages);
        public void onMessagesSend(ArrayList<Message> messages);
        public void onMessagesRead(ArrayList<Message> messages);
    }
}
