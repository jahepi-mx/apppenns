package pennsylvania.jahepi.com.apppenns.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by javier.hernandez on 24/02/2016.
 * Database class (SQL-Lite)
 */
public class Database {

    public static final String MESSAGES_TABLE = "messages";
    public static final String USERS_TABLE = "users";
    public static final String TASKS_TABLE = "tasks";
    public static final String ADDRESSES_TABLE = "addresses";
    public static final String CLIENTS_TABLE = "clients";
    public static final String TYPES_TABLE = "types";
    public static final String ATTACHMENTS_TABLE = "attachments";
    public static final String TASK_ATTACHMENTS_TABLE = "task_attachments";
    public static final String FILES_TABLE = "files";
    public static final String UBICATIONS_TABLE = "ubications";
    public static final String NOTIFICATIONS_TABLE = "notifications";
    public static final String PRODUCTS_TABLE = "products";
    public static final String ACTIVITIES_TABLE = "activities";

    private static final int DB_VERSION = 49;
    private static final String TAG = "DBHelper";
    private static final String DB_NAME = "pennsylvania.db";
    public static final int ERROR = -1;

    private DBHelper dbHelper;

    public Database(Context context) {
        dbHelper = new DBHelper(context);
    }

    public SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase();
    }

    public Cursor getAll(String table, String condition) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(table, null, condition, null, null, null, null);
    }

    public Cursor getAllGroupBy(String table, String condition, String groupBy) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(table, null, condition, null, groupBy, null, null);
    }

    public Cursor getAllOrderBy(String table, String condition, String orderBy) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(table, null, condition, null, null, null, orderBy);
    }

    public Cursor get(String table, String condition) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, null, condition, null, null, null, null);
        if (cursor.moveToNext()) {
            return cursor;
        } else {
            cursor.close();
            return null;
        }
    }

    public Cursor getProductsTotal(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT COUNT(*) AS total FROM " + PRODUCTS_TABLE + " WHERE user='" + userId + "'", null);
    }

    public Cursor getNoReadMessagesTotal(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT COUNT(*) AS total FROM " + MESSAGES_TABLE + " WHERE (from_user='" + userId + "' OR to_user='" + userId + "') AND read='0'", null);
    }

    public Cursor getTaskChildrenTotal(int taskId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT COUNT(*) AS total FROM " + TASKS_TABLE + " WHERE tasks.parent_task='" + taskId + "'", null);
    }

    public Cursor getTasks(String where, String orderBy) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT tasks.id AS task_id, tasks.user, tasks.address AS address_id, tasks.description, tasks.date AS task_date, tasks.in_lat, tasks.in_lon, tasks.out_lat, tasks.out_lon, tasks.send, tasks.register_date, tasks.check_in, tasks.check_out, tasks.checkin_date, tasks.checkout_date, tasks.conclusion, " +
            "addresses.client AS client_id, addresses.address, addresses.latitude, addresses.longitude, addresses.date AS address_date, addresses.active AS address_active, " +
            "clients.name, clients.kepler, clients.date AS client_date, clients.active AS client_active, tasks.cancelled, types.id AS type_id, types.name AS type_name, types.date AS type_date, types.active AS type_active, tasks.start_time, tasks.end_time, tasks.event_id, tasks.emails, tasks.parent_task, tasks.fingerprint, tasks.status, tasks.activities, tasks.competence_comment, tasks.products " +
            "FROM tasks INNER JOIN addresses ON tasks.address = addresses.id " +
            "INNER JOIN clients ON clients.id = addresses.client " +
            "INNER JOIN types ON tasks.type = types.id " +
            where + " GROUP BY tasks.id " + orderBy;
        return db.rawQuery(sql, null);
    }

    public Cursor getAttachments(String where, String orderBy) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT attachments.id AS attachment_id, files.id AS file_id, files.name, files.mime, files.path, files.date, files.active, files.send " +
                "FROM attachments " +
                "INNER JOIN files ON files.id = attachments.file " +
                where + " " + orderBy;
        return db.rawQuery(sql, null);
    }

    public Cursor getTaskAttachments(String where, String orderBy) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT task_attachments.id AS attachment_id, files.id AS file_id, files.name, files.mime, files.path, files.date, files.active, files.send, task_attachments.from_conclusion " +
                "FROM task_attachments " +
                "INNER JOIN files ON files.id = task_attachments.file " +
                where + " " + orderBy;
        return db.rawQuery(sql, null);
    }

    public Cursor getUserEmails(String keyword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT email FROM users WHERE email LIKE '%" + keyword + "%' OR name LIKE '%" + keyword+ "%'";
        return db.rawQuery(sql, null);
    }

    public int delete(String table, String condition) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.delete(table, condition, null);
    }

    public long insert(String table, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = ERROR;
        try {
            result = db.insertOrThrow(table, null, values);
        } catch (Exception exp) {
            Log.d(TAG, exp.getMessage());
        }
        if (result != ERROR) {
            return result;
        }
        return ERROR;
    }

    public boolean update(String table, ContentValues values, String where) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = ERROR;
        try {
            result = db.update(table, values, where, null);
        } catch (Exception exp) {
            Log.d(TAG, exp.getMessage());
        }
        return result != ERROR;
    }

    public void close() {
        dbHelper.close();
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + USERS_TABLE + " (id INT PRIMARY KEY, email TEXT, password TEXT, name TEXT, date TEXT, active INT, group_name TEXT)");
            Log.d(TAG, "Users table created!");
            db.execSQL("CREATE TABLE " + MESSAGES_TABLE + " (id INTEGER PRIMARY KEY, from_user INT, to_user INT, message TEXT, date TEXT, delivered INT, read INT, send INT, read_sync INT, type INT, UNIQUE (to_user, from_user, date))");
            Log.d(TAG, "Messages table created!");
            db.execSQL("CREATE TABLE " + TASKS_TABLE + " (id INTEGER PRIMARY KEY, user INT, address TEXT, description TEXT, date TEXT, in_lat REAL, in_lon REAL, out_lat REAL, out_lon REAL, send INT, register_date TEXT, check_in INT, check_out INT, checkin_date TEXT, checkout_date TEXT, conclusion TEXT, cancelled INT, type INT, start_time TEXT, end_time TEXT, event_id INT, emails TEXT, parent_task INT, fingerprint TEXT, status TEXT, competence_comment TEXT, products TEXT, activities TEXT)");
            Log.d(TAG, "Tasks table created!");
            db.execSQL("CREATE TABLE " + CLIENTS_TABLE + " (id INT, user INT, name TEXT, kepler TEXT, date TEXT, active INT, UNIQUE (id, user))");
            Log.d(TAG, "Clients table created!");
            db.execSQL("CREATE TABLE " + ADDRESSES_TABLE + " (id INT, client INT, address TEXT, latitude REAL, longitude REAL, date TEXT, active INT, UNIQUE (id, client))");
            Log.d(TAG, "Addresses table created!");
            db.execSQL("CREATE TABLE " + TYPES_TABLE + " (id INT PRIMARY KEY, category TEXT, name TEXT, color TEXT, date TEXT, active INT)");
            Log.d(TAG, "Types table created!");
            db.execSQL("CREATE TABLE " + UBICATIONS_TABLE + " (id INTEGER PRIMARY KEY, user INT, latitude REAL, longitude REAL, date TEXT, send INT)");
            Log.d(TAG, "Ubications table created!");
            db.execSQL("CREATE TABLE " + ATTACHMENTS_TABLE + " (id INTEGER PRIMARY KEY, message INT, file INT)");
            Log.d(TAG, "Attachments table created!");
            db.execSQL("CREATE TABLE " + TASK_ATTACHMENTS_TABLE + " (id INTEGER PRIMARY KEY, task INT, file INT, from_conclusion INT)");
            Log.d(TAG, "Task attachments table created!");
            db.execSQL("CREATE TABLE " + FILES_TABLE + " (id INTEGER PRIMARY KEY, path TEXT, name TEXT, mime TEXT, date TEXT, active INT, send INT)");
            Log.d(TAG, "Files table created!");
            db.execSQL("CREATE TABLE " + NOTIFICATIONS_TABLE + " (id INT PRIMARY KEY, from_user INT, to_user INT, notification TEXT, event_date TEXT, minutes INT, event_id INT, fingerprint TEXT, date TEXT, active INT)");
            Log.d(TAG, "Notifications table created!");
            db.execSQL("CREATE INDEX fingerprint_index ON " + TASKS_TABLE + " (fingerprint)");
            db.execSQL("CREATE TABLE " + PRODUCTS_TABLE + " (id TEXT, user INT, name TEXT, date TEXT, UNIQUE (id, user))");
            Log.d(TAG, "Products table created!");
            db.execSQL("CREATE TABLE " + ACTIVITIES_TABLE + " (id INT, user INT, name TEXT, date TEXT, active INT, UNIQUE (id, user))");
            Log.d(TAG, "Activities table created!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
            Log.d(TAG, "Users table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
            Log.d(TAG, "Messages table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
            Log.d(TAG, "Tasks table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + CLIENTS_TABLE);
            Log.d(TAG, "Clients table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + ADDRESSES_TABLE);
            Log.d(TAG, "Addresses table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + TYPES_TABLE);
            Log.d(TAG, "Types table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + UBICATIONS_TABLE);
            Log.d(TAG, "Ubications table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + ATTACHMENTS_TABLE);
            Log.d(TAG, "Attachments table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + TASK_ATTACHMENTS_TABLE);
            Log.d(TAG, "Task attachments table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + FILES_TABLE);
            Log.d(TAG, "Files table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS_TABLE);
            Log.d(TAG, "Notifications table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE);
            Log.d(TAG, "Products table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + ACTIVITIES_TABLE);
            Log.d(TAG, "Activities table removed!");
            onCreate(db);
        }
    }
}
