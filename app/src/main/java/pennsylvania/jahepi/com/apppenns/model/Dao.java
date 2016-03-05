package pennsylvania.jahepi.com.apppenns.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.entities.Coord;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.entities.User;
import pennsylvania.jahepi.com.apppenns.model.database.Database;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class Dao {

    private final static String TAG = "Dao";
    private Database db;

    public Dao(Context context) {
        db = new Database(context);
        Log.d(TAG, "Data Access Object initialized.");
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<User>();
        Cursor cursor = db.getAllOrderBy(Database.USERS_TABLE, null, "name ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setEmail(cursor.getString(1));
                user.setPassword(cursor.getString(2));
                user.setName(cursor.getString(3));
                user.setModifiedDate(cursor.getString(4));
                user.setActive(cursor.getInt(5) != 0);
                users.add(user);
            }
            cursor.close();
        }
        return users;
    }

    public User getUser(String email, String password) {
        Cursor cursor = db.get(Database.USERS_TABLE, String.format("email='%s' AND password='%s' AND active='1'", email, password));
        if (cursor != null) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setEmail(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setName(cursor.getString(3));
            user.setModifiedDate(cursor.getString(4));
            user.setActive(cursor.getInt(5) != 0);
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUser(int userId) {
        Cursor cursor = db.get(Database.USERS_TABLE, String.format("id='%s'", userId));
        if (cursor != null) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setEmail(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setName(cursor.getString(3));
            user.setModifiedDate(cursor.getString(4));
            user.setActive(cursor.getInt(5) != 0);
            cursor.close();
            return user;
        }
        return null;
    }

    public boolean saveUser(User user) {
        if (user != null) {
            ContentValues values = new ContentValues();
            values.put("email", user.getEmail());
            values.put("password", user.getPassword());
            values.put("name", user.getName());
            values.put("active", user.isActive() ? 1 : 0);
            values.put("date", user.getModifiedDateString());

            User userDB = getUser(user.getId());
            if (userDB != null) {
                if (userDB.isGreaterDate(user)) {
                    db.update(Database.USERS_TABLE, values, String.format("id='%s'", user.getId()));
                }
            } else {
                values.put("id", user.getId());
                db.insert(Database.USERS_TABLE, values);
            }
            return true;
        }
        return false;
    }

    public ArrayList<Message> getMessages(int userId) {
        ArrayList<Message> messages = new ArrayList<Message>();
        Cursor cursor = db.getAllOrderBy(Database.MESSAGES_TABLE, String.format("from_user='%s' OR to_user='%s'", userId, userId), "id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Message message = new Message();
                message.setId(cursor.getInt(0));
                message.setFrom(getUser(cursor.getInt(1)));
                message.setTo(getUser(cursor.getInt(2)));
                message.setMessage(cursor.getString(3));
                message.setModifiedDate(cursor.getString(4));
                message.setDelivered(cursor.getInt(5) == 1);
                message.setRead(cursor.getInt(6) == 1);
                message.setSend(cursor.getInt(7) == 1);
                messages.add(message);
            }
            cursor.close();
        }
        return messages;
    }

    public ArrayList<Message> getMessagesRead(int userId) {
        ArrayList<Message> messages = new ArrayList<Message>();
        Cursor cursor = db.getAllOrderBy(Database.MESSAGES_TABLE, String.format("to_user='%s' AND read=1 AND read_sync=0", userId), "id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Message message = new Message();
                message.setId(cursor.getInt(0));
                message.setFrom(getUser(cursor.getInt(1)));
                message.setTo(getUser(cursor.getInt(2)));
                message.setMessage(cursor.getString(3));
                message.setModifiedDate(cursor.getString(4));
                message.setDelivered(cursor.getInt(5) == 1);
                message.setRead(cursor.getInt(6) == 1);
                message.setSend(cursor.getInt(7) == 1);
                messages.add(message);
            }
            cursor.close();
        }
        return messages;
    }

    public ArrayList<Message> getNewMessages(int userId) {
        ArrayList<Message> messages = new ArrayList<Message>();
        Cursor cursor = db.getAllOrderBy(Database.MESSAGES_TABLE, String.format("from_user='%s' AND send=0", userId), "id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Message message = new Message();
                message.setId(cursor.getInt(0));
                message.setFrom(getUser(cursor.getInt(1)));
                message.setTo(getUser(cursor.getInt(2)));
                message.setMessage(cursor.getString(3));
                message.setModifiedDate(cursor.getString(4));
                message.setDelivered(cursor.getInt(5) == 1);
                message.setRead(cursor.getInt(6) == 1);
                message.setSend(cursor.getInt(7) == 1);
                messages.add(message);
            }
            cursor.close();
        }
        return messages;
    }

    public Message getMessage(Message msg) {
        Cursor cursor = db.get(Database.MESSAGES_TABLE, String.format("date='%s' AND from_user='%s' AND to_user='%s'", msg.getModifiedDateString(), msg.getFrom().getId(), msg.getTo().getId()));
        if (cursor != null) {
            Message message = new Message();
            message.setId(cursor.getInt(0));
            message.setFrom(getUser(cursor.getInt(1)));
            message.setTo(getUser(cursor.getInt(2)));
            message.setMessage(cursor.getString(3));
            message.setModifiedDate(cursor.getString(4));
            message.setDelivered(cursor.getInt(5) == 1);
            message.setRead(cursor.getInt(6) == 1);
            message.setSend(cursor.getInt(7) == 1);
            cursor.close();
            return message;
        }
        return null;
    }

    public boolean saveMessage(Message message) {
        if (message != null) {
            ContentValues values = new ContentValues();
            values.put("from_user", message.getFrom().getId());
            values.put("to_user", message.getTo().getId());
            values.put("message", message.getMessage());
            values.put("date", message.getModifiedDateString());
            values.put("delivered", message.isDelivered() ? 1 : 0);
            values.put("read", message.isRead() ? 1 : 0);
            values.put("send", message.isSend() ? 1 : 0);
            values.put("read_sync", message.isReadSync() ? 1 : 0);

            Message messageDB = getMessage(message);
            if (messageDB != null) {
                if (messageDB.isGreaterDate(message)) {
                    db.update(Database.MESSAGES_TABLE, values, String.format("date='%s' AND from_user='%s' AND to_user='%s'", message.getModifiedDateString(), message.getFrom().getId(), message.getTo().getId()));
                }
            } else {
                Log.d(TAG, message.toString());
                db.insert(Database.MESSAGES_TABLE, values);
            }
            return true;
        }
        return false;
    }

    public boolean updateMessageField(Message message, String field, String value) {
        if (message != null) {
            ContentValues values = new ContentValues();
            values.put(field, value);
            return db.update(Database.MESSAGES_TABLE, values, String.format("to_user='%s' AND from_user='%s' AND date='%s'", message.getTo().getId(), message.getFrom().getId(), message.getModifiedDateString()));
        }
        return false;
    }

    public int getNoReadMessagesTotal(int userId) {
        Cursor cursor = db.getNoReadMessagesTotal(userId);
        if (cursor != null) {
            cursor.moveToFirst();
            int n = cursor.getInt(0);
            cursor.close();
            return n;
        }
        return 0;
    }

    public ArrayList<Task> getNewTasks(int userId) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        Cursor cursor = db.getAllOrderBy(Database.TASKS_TABLE, String.format("user='%s' AND send=0", userId), "id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Task task = new Task();
                task.setId(cursor.getInt(0));
                task.setUser(getUser(cursor.getInt(1)));
                task.setClient(cursor.getString(2));
                task.setDescription(cursor.getString(3));
                task.setModifiedDate(cursor.getString(4));
                Coord checkIn = new Coord();
                checkIn.setLatitude(cursor.getDouble(5));
                checkIn.setLongitude(cursor.getDouble(6));
                Coord checkOut = new Coord();
                checkOut.setLatitude(cursor.getDouble(7));
                checkOut.setLongitude(cursor.getDouble(8));
                task.setCheckIn(checkIn);
                task.setCheckOut(checkOut);
                tasks.add(task);
            }
            cursor.close();
        }
        return tasks;
    }

    public Task getTask(Task taskParam) {
        Cursor cursor = db.get(Database.TASKS_TABLE, String.format("id='%s' AND user='%s'", taskParam.getId(), taskParam.getUser().getId()));
        if (cursor != null) {
            Task task = new Task();
            task.setId(cursor.getInt(0));
            task.setUser(getUser(cursor.getInt(1)));
            task.setClient(cursor.getString(2));
            task.setDescription(cursor.getString(3));
            task.setModifiedDate(cursor.getString(4));
            Coord checkIn = new Coord();
            checkIn.setLatitude(cursor.getDouble(5));
            checkIn.setLongitude(cursor.getDouble(6));
            Coord checkOut = new Coord();
            checkOut.setLatitude(cursor.getDouble(7));
            checkOut.setLongitude(cursor.getDouble(8));
            task.setCheckIn(checkIn);
            task.setCheckOut(checkOut);
            cursor.close();
            return task;
        }
        return null;
    }

    public boolean saveTask(Task task) {
        if (task != null) {
            ContentValues values = new ContentValues();
            values.put("user", task.getUser().getId());
            values.put("client", task.getClient());
            values.put("description", task.getDescription());
            values.put("date", task.getModifiedDateString());
            values.put("in_lat", task.getCheckIn().getLatitude());
            values.put("in_lon", task.getCheckIn().getLongitude());
            values.put("out_lat", task.getCheckOut().getLatitude());
            values.put("out_lon", task.getCheckOut().getLongitude());
            values.put("send", task.isSend() ? 1 : 0);

            Task taskDB = getTask(task);
            if (taskDB != null) {
                if (taskDB.isGreaterDate(task)) {
                    db.update(Database.TASKS_TABLE, values, String.format("id='%s' AND user='%s'", task.getId(), task.getUser().getId()));
                }
            } else {
                Log.d(TAG, task.toString());
                db.insert(Database.TASKS_TABLE, values);
            }
            return true;
        }
        return false;
    }

    public boolean updateTaskAsSend(Task task) {
        if (task != null) {
            ContentValues values = new ContentValues();
            values.put("send", "1");
            return db.update(Database.TASKS_TABLE, values, String.format("id='%s' AND user='%s'", task.getId(), task.getUser().getId()));
        }
        return false;
    }
}
