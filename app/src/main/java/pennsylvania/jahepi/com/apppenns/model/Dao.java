package pennsylvania.jahepi.com.apppenns.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Client;
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
        Cursor cursor = db.getTasks(String.format("WHERE tasks.user='%s' AND tasks.send=0", userId), "ORDER BY tasks.id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Task task = mapTask(cursor);
                tasks.add(task);
            }
            cursor.close();
        }
        return tasks;
    }

    public ArrayList<Task> getTasks(int userId, String date) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        Cursor cursor = db.getTasks(String.format("WHERE tasks.user='%s' AND tasks.register_date='%s'", userId, date), "ORDER BY tasks.id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Task task = mapTask(cursor);
                tasks.add(task);
            }
            cursor.close();
        }
        return tasks;
    }

    public Task getTask(Task taskParam) {
        Cursor cursor = db.getTasks(String.format("WHERE tasks.id='%s' AND tasks.user='%s'", taskParam.getId(), taskParam.getUser().getId()), "");
        if (cursor != null) {
            Task task = null;
            if (cursor.moveToNext()) {
                task = mapTask(cursor);
            }
            cursor.close();
            return task;
        }
        return null;
    }

    private Task mapTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getInt(0));
        task.setUser(getUser(cursor.getInt(1)));

        Address address = new Address();
        address.setId(cursor.getInt(2));
        address.setAddress(cursor.getString(17));
        address.getCoord().setLatitude(cursor.getDouble(18));
        address.getCoord().setLongitude(cursor.getDouble(19));
        address.setModifiedDate(cursor.getString(20));
        address.setActive(cursor.getInt(21) == 1);

        Client client = new Client();
        client.setId(cursor.getInt(16));
        client.setName(cursor.getString(22));
        client.setKepler(cursor.getString(23));
        client.setModifiedDate(cursor.getString(24));
        client.setActive(cursor.getInt(25) == 1);
        address.setClient(client);

        task.setAddress(address);
        task.setDescription(cursor.getString(3));
        task.setModifiedDate(cursor.getString(4));
        task.setCheckInLatitude(cursor.getDouble(5));
        task.setCheckInLongitude(cursor.getDouble(6));
        task.setCheckOutLatitude(cursor.getDouble(7));
        task.setCheckOutLongitude(cursor.getDouble(8));
        task.setSend(cursor.getInt(9) == 1);
        task.setDate(cursor.getString(10));
        task.setCheckin(cursor.getInt(11) == 1);
        task.setCheckout(cursor.getInt(12) == 1);
        task.setCheckInDate(cursor.getString(13));
        task.setCheckOutDate(cursor.getString(14));
        task.setConclusion(cursor.getString(15));
        return task;
    }

    public boolean saveTask(Task task) {
        if (task != null) {
            ContentValues values = new ContentValues();
            values.put("user", task.getUser().getId());
            values.put("address", task.getAddress().getId());
            values.put("description", task.getDescription());
            values.put("date", task.getModifiedDateString());
            values.put("in_lat", task.getCheckInCoord().getLatitude());
            values.put("in_lon", task.getCheckInCoord().getLongitude());
            values.put("out_lat", task.getCheckOutCoord().getLatitude());
            values.put("out_lon", task.getCheckOutCoord().getLongitude());
            values.put("send", task.isSend() ? 1 : 0);
            values.put("register_date", task.getDate());
            values.put("check_in", task.isCheckin() ? 1 : 0);
            values.put("check_out", task.isCheckout() ? 1 : 0);
            values.put("checkin_date", task.getCheckInDate());
            values.put("checkout_date", task.getCheckOutDate());
            values.put("conclusion", task.getConclusion());

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

    public Client getClient(Client clientParam) {
        Cursor cursor = db.get(Database.CLIENTS_TABLE, String.format("id='%s' AND user='%s'", clientParam.getId(), clientParam.getUser().getId()));
        if (cursor != null) {
            Client client = new Client();
            client.setId(cursor.getInt(0));
            client.setUser(getUser(cursor.getInt(1)));
            client.setName(cursor.getString(2));
            client.setKepler(cursor.getString(3));
            client.setModifiedDate(cursor.getString(4));
            client.setActive(cursor.getInt(5) == 1);
            cursor.close();
            return client;
        }
        return null;
    }

    public ArrayList<Client> getClients(int userId, String name) {
        Cursor cursor = db.getAllOrderBy(Database.CLIENTS_TABLE, String.format("name LIKE '%%%s%%' AND user='%s'", name, userId), "name ASC");
        ArrayList<Client> clients = new ArrayList<Client>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Client client = new Client();
                client.setId(cursor.getInt(0));
                client.setUser(getUser(cursor.getInt(1)));
                client.setName(cursor.getString(2));
                client.setKepler(cursor.getString(3));
                client.setModifiedDate(cursor.getString(4));
                client.setActive(cursor.getInt(5) == 1);
                clients.add(client);
            }
            cursor.close();
        }
        return clients;
    }

    public boolean saveClient(Client client) {
        if (client != null) {
            ContentValues values = new ContentValues();
            values.put("id", client.getId());
            values.put("user", client.getUser().getId());
            values.put("name", client.getName());
            values.put("kepler", client.getKepler());
            values.put("date", client.getModifiedDateString());
            values.put("active", client.isActive() ? 1 : 0);

            Client clientDB = getClient(client);
            if (clientDB != null) {
                if (clientDB.isGreaterDate(client)) {
                    db.update(Database.CLIENTS_TABLE, values, String.format("id='%s' AND user='%s'", client.getId(), client.getUser().getId()));
                }
            } else {
                Log.d(TAG, client.toString());
                db.insert(Database.CLIENTS_TABLE, values);
            }
            return true;
        }
        return false;
    }

    public Address getAddress(Address addressParam) {
        Cursor cursor = db.get(Database.ADDRESSES_TABLE, String.format("id='%s' AND client='%s'", addressParam.getId(), addressParam.getClient().getId()));
        if (cursor != null) {
            Address address = new Address();
            address.setId(cursor.getInt(0));
            address.setClient(address.getClient());
            address.setAddress(cursor.getString(2));
            address.getCoord().setLatitude(cursor.getDouble(3));
            address.getCoord().setLongitude(cursor.getDouble(4));
            address.setModifiedDate(cursor.getString(5));
            address.setActive(cursor.getInt(5) == 1);
            cursor.close();
            return address;
        }
        return null;
    }

    public ArrayList<Address> getAddresses(Client client) {
        Cursor cursor = db.getAllOrderBy(Database.ADDRESSES_TABLE, String.format("client='%s'", client.getId()), "address ASC");
        ArrayList<Address> addresses = new ArrayList<Address>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Address address = new Address();
                address.setId(cursor.getInt(0));
                address.setClient(client);
                address.setAddress(cursor.getString(2));
                address.getCoord().setLatitude(cursor.getDouble(3));
                address.getCoord().setLongitude(cursor.getDouble(4));
                address.setModifiedDate(cursor.getString(5));
                address.setActive(cursor.getInt(5) == 1);
                addresses.add(address);
            }
            cursor.close();
        }
        return addresses;
    }

    public boolean saveAdress(Address address) {
        if (address != null) {
            ContentValues values = new ContentValues();
            values.put("id", address.getId());
            values.put("client", address.getClient().getId());
            values.put("address", address.getAddress());
            values.put("latitude", address.getCoord().getLatitude());
            values.put("longitude", address.getCoord().getLongitude());
            values.put("date", address.getModifiedDateString());
            values.put("active", address.isActive() ? 1 : 0);

            Address addressDB = getAddress(address);
            if (addressDB != null) {
                if (addressDB.isGreaterDate(address)) {
                    db.update(Database.ADDRESSES_TABLE, values, String.format("id='%s' AND client='%s'", address.getId(), address.getClient().getId()));
                }
            } else {
                Log.d(TAG, address.toString());
                db.insert(Database.ADDRESSES_TABLE, values);
            }
            return true;
        }
        return false;
    }
}
