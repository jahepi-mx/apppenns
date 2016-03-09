package pennsylvania.jahepi.com.apppenns.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class Database {

    public static final String MESSAGES_TABLE = "messages";
    public static final String USERS_TABLE = "users";
    public static final String TASKS_TABLE = "tasks";

    private static final int DB_VERSION = 16;
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

    public Cursor getNoReadMessagesTotal(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT COUNT(*) AS total FROM " + MESSAGES_TABLE + " WHERE (from_user='" + userId + "' OR to_user='" + userId + "') AND read='0'", null);
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
            db.execSQL("CREATE TABLE " + USERS_TABLE + " (id INT PRIMARY KEY, email TEXT, password TEXT, name TEXT, date TEXT, active INT)");
            Log.d(TAG, "Users table created!");
            db.execSQL("CREATE TABLE " + MESSAGES_TABLE + " (id INTEGER PRIMARY KEY, from_user INT, to_user INT, message TEXT, date TEXT, delivered INT, read INT, send INT, read_sync INT, UNIQUE (to_user, from_user, date))");
            Log.d(TAG, "Messages table created!");
            db.execSQL("CREATE TABLE " + TASKS_TABLE + " (id INTEGER PRIMARY KEY, user INT, client TEXT, description TEXT, date TEXT, in_lat REAL, in_lon REAL, out_lat REAL, out_lon REAL, send INT, register_date TEXT, check_in INT, check_out INT, checkin_date TEXT, checkout_date TEXT, conclusion TEXT)");
            Log.d(TAG, "Tasks table created!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
            Log.d(TAG, "Users table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
            Log.d(TAG, "Messages table removed!");
            db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
            Log.d(TAG, "Tasks table removed!");
            onCreate(db);
        }
    }
}
