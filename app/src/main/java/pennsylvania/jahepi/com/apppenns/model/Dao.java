package pennsylvania.jahepi.com.apppenns.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Client;
import pennsylvania.jahepi.com.apppenns.entities.Coord;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Notification;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.entities.Type;
import pennsylvania.jahepi.com.apppenns.entities.Ubication;
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
        Cursor cursor = db.getAllOrderBy(Database.USERS_TABLE, "active='1'", "group_name, name ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                User user = mapUser(cursor);
                users.add(user);
            }
            cursor.close();
        }
        return users;
    }

    public User getUser(String email, String password) {
        Cursor cursor = db.get(Database.USERS_TABLE, String.format("email='%s' AND password='%s' AND active='1'", email, password));
        if (cursor != null) {
            User user = mapUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    public User getUser(int userId) {
        Cursor cursor = db.get(Database.USERS_TABLE, String.format("id='%s'", userId));
        if (cursor != null) {
            User user = mapUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    private User mapUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(0));
        user.setEmail(cursor.getString(1));
        user.setPassword(cursor.getString(2));
        user.setName(cursor.getString(3));
        user.setModifiedDate(cursor.getString(4));
        user.setActive(cursor.getInt(5) != 0);
        String groups = cursor.getString(6);
        if (groups != null) {
            String[] groupsArray = groups.split(",");
            for (int u = 0; u < groupsArray.length; u++) {
                if (!groupsArray[u].equals("")) {
                    user.addGroup(groupsArray[u]);
                }
            }
        }
        return user;
    }

    public boolean saveUser(User user) {
        if (user != null) {
            ContentValues values = new ContentValues();
            values.put("email", user.getEmail());
            values.put("password", user.getPassword());
            values.put("name", user.getName());
            values.put("group_name", TextUtils.join(",", user.getGroups()));
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

    public ArrayList<Ubication> getNewUbications() {
        ArrayList<Ubication> ubications = new ArrayList<Ubication>();
        Cursor cursor = db.getAll(Database.UBICATIONS_TABLE, "send=0");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Ubication ubication = new Ubication();
                ubication.setId(cursor.getInt(0));
                User user = new User();
                user.setId(cursor.getInt(1));
                ubication.setUser(user);
                Coord coord = new Coord();
                coord.setLatitude(cursor.getDouble(2));
                coord.setLongitude(cursor.getDouble(3));
                ubication.setCoord(coord);
                ubication.setModifiedDate(cursor.getString(4));
                ubication.setSend(cursor.getInt(5) == 1);
                ubications.add(ubication);
            }
            cursor.close();
        }
        return ubications;
    }

    public boolean saveUbication(Ubication ubication) {
        if (ubication != null) {
            ContentValues values = new ContentValues();
            values.put("user", ubication.getUser().getId());
            values.put("latitude", ubication.getCoord().getLatitude());
            values.put("longitude", ubication.getCoord().getLongitude());
            values.put("date", ubication.getModifiedDateString());
            values.put("send", ubication.isSend() ? 1 : 0);
            long id = db.insert(Database.UBICATIONS_TABLE, values);
            ubication.setId((int) id);
            return true;
        }
        return false;
    }

    public boolean updateUbicationAsSend(Ubication ubication) {
        if (ubication != null) {
            ContentValues values = new ContentValues();
            values.put("send", "1");
            return db.update(Database.UBICATIONS_TABLE, values, String.format("id='%s'", ubication.getId()));
        }
        return false;
    }

    public Type getType(int typeId) {
        Cursor cursor = db.get(Database.TYPES_TABLE, String.format("id='%s'", typeId));
        if (cursor != null) {
            Type type = mapType(cursor);
            cursor.close();
            return type;
        }
        return null;
    }

    public ArrayList<Type> getTypes(String category) {
        ArrayList<Type> types = new ArrayList<Type>();
        Cursor cursor = db.getAllOrderBy(Database.TYPES_TABLE, String.format("active='1' AND category='%s'", category), "name ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Type type = mapType(cursor);
                types.add(type);
            }
            cursor.close();
        }
        return types;
    }

    private Type mapType(Cursor cursor) {
        Type type = new Type();
        type.setId(cursor.getInt(0));
        type.setCategory(cursor.getString(1));
        type.setName(cursor.getString(2));
        type.setColor(cursor.getString(3));
        type.setModifiedDate(cursor.getString(4));
        type.setActive(cursor.getInt(5) != 0);
        return type;
    }

    public boolean saveType(Type type) {
        if (type != null) {
            ContentValues values = new ContentValues();
            values.put("name", type.getName());
            values.put("category", type.getCategory());
            values.put("color", type.getColor());
            values.put("active", type.isActive() ? 1 : 0);
            values.put("date", type.getModifiedDateString());

            Type typeDB = getType(type.getId());
            if (typeDB != null) {
                if (typeDB.isGreaterDate(type)) {
                    db.update(Database.TYPES_TABLE, values, String.format("id='%s'", type.getId()));
                }
            } else {
                values.put("id", type.getId());
                db.insert(Database.TYPES_TABLE, values);
            }
            return true;
        }
        return false;
    }

    public Attachment.File getFile(int fileId) {
        Cursor cursor = db.get(Database.FILES_TABLE, String.format("id='%s'", fileId));
        if (cursor != null) {
            Attachment.File file = mapFile(cursor);
            cursor.close();
            return file;
        }
        return null;
    }

    public ArrayList<Attachment.File> getNotSendFiles() {
        Cursor cursor = db.getAll(Database.FILES_TABLE, String.format("send='%s'", 0));
        ArrayList<Attachment.File> files = new ArrayList<Attachment.File>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Attachment.File file = mapFile(cursor);
                files.add(file);
            }
            cursor.close();
        }
        return files;
    }

    private Attachment.File mapFile(Cursor cursor) {
        Attachment.File file = new Attachment.File();
        file.setId(cursor.getInt(0));
        file.setPath(cursor.getString(1));
        file.setName(cursor.getString(2));
        file.setMime(cursor.getString(3));
        file.setModifiedDate(cursor.getString(4));
        file.setActive(cursor.getInt(5) != 0);
        file.setSend(cursor.getInt(6) == 1);
        return file;
    }

    public boolean saveFile(Attachment.File file) {
        if (file != null) {
            ContentValues values = new ContentValues();
            values.put("name", file.getName());
            values.put("path", file.getPath());
            values.put("mime", file.getMime());
            values.put("send", file.isSend() ? 1 : 0);
            values.put("active", file.isActive() ? 1 : 0);
            values.put("date", file.getModifiedDateString());

            Attachment.File fileDB = getFile(file.getId());
            if (fileDB != null) {
                if (fileDB.isGreaterDate(file)) {
                    db.update(Database.FILES_TABLE, values, String.format("id='%s'", file.getId()));
                }
            } else {
                long id = db.insert(Database.FILES_TABLE, values);
                file.setId((int) id);
            }
            return true;
        }
        return false;
    }

    public boolean updateFileAsSend(Attachment.File file) {
        if (file != null) {
            ContentValues values = new ContentValues();
            values.put("send", "1");
            return db.update(Database.FILES_TABLE, values, String.format("id='%s'", file.getId()));
        }
        return false;
    }

    public ArrayList<Attachment> getAttachments(Message message) {
        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        Cursor cursor = db.getAttachments(String.format("WHERE attachments.message='%s'", message.getId()), "");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Attachment attachment = mapAttachment(cursor);
                attachments.add(attachment);
            }
            cursor.close();
        }
        return attachments;
    }

    public ArrayList<Message> getMessages(int userId, String date) {
        ArrayList<Message> messages = new ArrayList<Message>();
        Cursor cursor = db.getAllOrderBy(Database.MESSAGES_TABLE, String.format("((from_user='%s' OR to_user='%s') AND date LIKE '%%%s%%') OR ((from_user='%s' OR to_user='%s') AND read = 0)", userId, userId, date, userId, userId), "read, id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Message message = mapMessage(cursor);
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
                Message message = mapMessage(cursor);
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
                Message message = mapMessage(cursor);
                messages.add(message);
            }
            cursor.close();
        }
        return messages;
    }

    public Message getMessage(Message msg) {
        Cursor cursor = db.get(Database.MESSAGES_TABLE, String.format("date='%s' AND from_user='%s' AND to_user='%s'", msg.getModifiedDateString(), msg.getFrom().getId(), msg.getTo().getId()));
        if (cursor != null) {
            Message message = mapMessage(cursor);
            cursor.close();
            return message;
        }
        return null;
    }

    private Message mapMessage(Cursor cursor) {
        Message message = new Message();
        message.setId(cursor.getInt(0));
        message.setFrom(getUser(cursor.getInt(1)));
        message.setTo(getUser(cursor.getInt(2)));
        message.setMessage(cursor.getString(3));
        message.setModifiedDate(cursor.getString(4));
        message.setDelivered(cursor.getInt(5) == 1);
        message.setRead(cursor.getInt(6) == 1);
        message.setSend(cursor.getInt(7) == 1);
        message.setType(getType(cursor.getInt(9)));
        ArrayList<Attachment> attachments = getAttachments(message);
        message.addAttachments(attachments);
        return message;
    }

    public boolean saveMessage(Message message) {
        if (message != null) {
            ContentValues values = new ContentValues();
            values.put("from_user", message.getFrom().getId());
            values.put("to_user", message.getTo().getId());
            values.put("message", message.getMessage());
            values.put("type", message.getType().getId());
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
                long id = db.insert(Database.MESSAGES_TABLE, values);
                message.setId((int) id);
                Iterator<Attachment> iterator = message.getAttachmentsIterator();
                while (iterator.hasNext()) {
                    ContentValues attachmentValues = new ContentValues();
                    Attachment attachment = iterator.next();
                    Attachment.File file = attachment.getFile();
                    saveFile(file);
                    attachmentValues.put("message", message.getId());
                    attachmentValues.put("file", file.getId());
                    db.insert(Database.ATTACHMENTS_TABLE, attachmentValues);
                }
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

    public ArrayList<Attachment> getAttachments(Task task) {
        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        Cursor cursor = db.getTaskAttachments(String.format("WHERE task_attachments.task='%s'", task.getId()), "");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Attachment attachment = mapAttachment(cursor);
                attachments.add(attachment);
            }
            cursor.close();
        }
        return attachments;
    }

    private Attachment mapAttachment(Cursor cursor) {
        Attachment attachment = new Attachment();
        attachment.setId(cursor.getInt(0));
        Attachment.File file = new Attachment.File();
        file.setId(cursor.getInt(1));
        file.setName(cursor.getString(2));
        file.setMime(cursor.getString(3));
        file.setPath(cursor.getString(4));
        file.setModifiedDate(cursor.getString(5));
        file.setActive(cursor.getInt(6) == 1);
        file.setSend(cursor.getInt(7) == 1);
        attachment.setFile(file);
        return attachment;
    }

    public ArrayList<Task> getNewTasks(int userId) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        Cursor cursor = db.getTasks(String.format("WHERE tasks.user='%s' AND tasks.send=0 AND clients.user='%s'", userId, userId), "ORDER BY tasks.id DESC");
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
        Cursor cursor = db.getTasks(String.format("WHERE tasks.user='%s' AND tasks.register_date='%s' AND clients.user='%s'", userId, date, userId), "ORDER BY tasks.id DESC");
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
        Cursor cursor = db.getTasks(String.format("WHERE tasks.fingerprint='%s' AND tasks.user='%s'", taskParam.getFingerprint(), taskParam.getUser().getId()), "");
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

    public Task getTaskById(Task taskParam) {
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
        task.setCancelled(cursor.getInt(26) == 1);
        task.setStartTime(cursor.getString(31));
        task.setEndTime(cursor.getString(32));
        task.setEventId(cursor.getInt(33));
        task.setEmails(cursor.getString(34));
        task.setFingerprint(cursor.getString(36));
        task.setStatus(cursor.getString(37));

        Task parentTask = new Task();
        parentTask.setId(cursor.getInt(35));
        parentTask.setUser(task.getUser());
        task.setParentTask(getTaskById(parentTask));

        Type type = new Type();
        type.setId(cursor.getInt(27));
        type.setName(cursor.getString(28));
        type.setModifiedDate(cursor.getString(29));
        type.setActive(cursor.getInt(30) == 1);
        task.setType(type);
        task.addAttachments(getAttachments(task));
        task.addNotifications(getNotifications(task.getFingerprint()));
        return task;
    }

    public boolean saveTask(Task task) {
        if (task != null) {
            ContentValues values = new ContentValues();
            values.put("user", task.getUser().getId());
            values.put("address", task.getAddress().getId());
            values.put("description", task.getDescription());
            values.put("date", task.getModifiedDateString());
            values.put("send", task.isSend() ? 1 : 0);
            values.put("register_date", task.getDate());
            values.put("type", task.getType().getId());
            values.put("start_time", task.getStartTime());
            values.put("end_time", task.getEndTime());
            values.put("event_id", task.getEventId());
            values.put("status", task.getStatus());
            values.put("fingerprint", task.getFingerprint());
            if (task.updateAllState()) {
                values.put("in_lat", task.getCheckInCoord().getLatitude());
                values.put("in_lon", task.getCheckInCoord().getLongitude());
                values.put("out_lat", task.getCheckOutCoord().getLatitude());
                values.put("out_lon", task.getCheckOutCoord().getLongitude());
                values.put("check_in", task.isCheckin() ? 1 : 0);
                values.put("check_out", task.isCheckout() ? 1 : 0);
                values.put("checkin_date", task.getCheckInDate());
                values.put("checkout_date", task.getCheckOutDate());
                values.put("conclusion", task.getConclusion());
                values.put("cancelled", task.isCancelled() ? 1 : 0);
                values.put("emails", task.getEmails());
                values.put("parent_task", task.getParentTaskId());
            }

            Task taskDB = getTask(task);
            if (taskDB != null) {
                if (taskDB.isGreaterDate(task)) {
                    db.update(Database.TASKS_TABLE, values, String.format("user='%s' AND fingerprint='%s'", task.getUser().getId(), task.getFingerprint()));
                }
            } else {
                Log.d(TAG, task.toString());
                long id = db.insert(Database.TASKS_TABLE, values);
                task.setId((int) id);
            }
            db.delete(Database.TASK_ATTACHMENTS_TABLE, String.format("task='%s'", task.getId()));
            Iterator<Attachment> iterator = task.getAttachmentsIterator();
            while (iterator.hasNext()) {
                ContentValues attachmentValues = new ContentValues();
                Attachment attachment = iterator.next();
                Attachment.File file = attachment.getFile();
                saveFile(file);
                attachmentValues.put("task", task.getId());
                attachmentValues.put("file", file.getId());
                db.insert(Database.TASK_ATTACHMENTS_TABLE, attachmentValues);
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

    public Client getClient(int clientId, int userId) {
        Cursor cursor = db.get(Database.CLIENTS_TABLE, String.format("id='%s' AND user='%s'", clientId, userId));
        if (cursor != null) {
            Client client = mapClient(cursor);
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
                Client client = mapClient(cursor);
                clients.add(client);
            }
            cursor.close();
        }
        return clients;
    }

    private Client mapClient(Cursor cursor) {
        Client client = new Client();
        client.setId(cursor.getInt(0));
        client.setUser(getUser(cursor.getInt(1)));
        client.setName(cursor.getString(2));
        client.setKepler(cursor.getString(3));
        client.setModifiedDate(cursor.getString(4));
        client.setActive(cursor.getInt(5) == 1);
        return client;
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

            Client clientDB = getClient(client.getId(), client.getUser().getId());
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

    public Address getAddress(int addressId, int userId) {
        Cursor cursor = db.get(Database.ADDRESSES_TABLE, String.format("id='%s'", addressId));
        if (cursor != null) {
            Address address = mapAddress(cursor);
            address.setClient(getClient(cursor.getInt(1), userId));
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
                Address address = mapAddress(cursor);
                address.setClient(client);
                addresses.add(address);
            }
            cursor.close();
        }
        return addresses;
    }

    private Address mapAddress(Cursor cursor) {
        Address address = new Address();
        address.setId(cursor.getInt(0));
        address.setAddress(cursor.getString(2));
        address.getCoord().setLatitude(cursor.getDouble(3));
        address.getCoord().setLongitude(cursor.getDouble(4));
        address.setModifiedDate(cursor.getString(5));
        address.setActive(cursor.getInt(5) == 1);
        return address;
    }

    public boolean saveAddress(Address address) {
        if (address != null) {
            ContentValues values = new ContentValues();
            values.put("id", address.getId());
            values.put("client", address.getClient().getId());
            values.put("address", address.getAddress());
            values.put("latitude", address.getCoord().getLatitude());
            values.put("longitude", address.getCoord().getLongitude());
            values.put("date", address.getModifiedDateString());
            values.put("active", address.isActive() ? 1 : 0);

            Address addressDB = getAddress(address.getId(), address.getUserClient().getId());
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

    public Notification getNotification(int id) {
        Cursor cursor = db.get(Database.NOTIFICATIONS_TABLE, String.format("id='%s'", id));
        if (cursor != null) {
            Notification notification = mapNotification(cursor);
            cursor.close();
            return notification;
        }
        return null;
    }

    public ArrayList<Notification> getNotifications(String fingerprint) {
        Cursor cursor = db.getAllOrderBy(Database.NOTIFICATIONS_TABLE, String.format("fingerprint='%s'", fingerprint), "id ASC");
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Notification notification = mapNotification(cursor);
                notifications.add(notification);
            }
            cursor.close();
        }
        return notifications;
    }

    private Notification mapNotification(Cursor cursor) {
        Notification notification = new Notification();
        notification.setId(cursor.getInt(0));
        notification.setFrom(getUser(cursor.getInt(1)));
        notification.setTo(getUser(cursor.getInt(2)));
        notification.setNotification(cursor.getString(3));
        notification.setEventDate(cursor.getString(4));
        notification.setMinutes(cursor.getInt(5));
        notification.setEventId(cursor.getInt(6));
        notification.setFingerprint(cursor.getString(7));
        notification.setModifiedDate(cursor.getString(8));
        notification.setActive(cursor.getInt(9) == 1);
        return notification;
    }

    public boolean saveNotification(Notification notification) {
        if (notification != null) {
            ContentValues values = new ContentValues();
            values.put("id", notification.getId());
            values.put("from_user", notification.getFrom().getId());
            values.put("to_user", notification.getTo().getId());
            values.put("notification", notification.getNotification());
            values.put("event_date", notification.getEventDate());
            values.put("minutes", notification.getMinutes());
            values.put("event_id", notification.getEventId());
            values.put("fingerprint", notification.getFingerprint());
            values.put("date", notification.getModifiedDateString());
            values.put("active", notification.isActive() ? 1 : 0);

            Notification notificationDB = getNotification(notification.getId());
            if (notificationDB != null) {
                if (notificationDB.isGreaterDate(notification)) {
                    db.update(Database.NOTIFICATIONS_TABLE, values, String.format("id='%s'", notification.getId()));
                }
            } else {
                Log.d(TAG, notification.toString());
                db.insert(Database.NOTIFICATIONS_TABLE, values);
            }
            return true;
        }
        return false;
    }

    public ArrayList<String> getUserEmails(String keyword) {
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = db.getUserEmails(keyword);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
            cursor.close();
        }
        return list;
    }
}
