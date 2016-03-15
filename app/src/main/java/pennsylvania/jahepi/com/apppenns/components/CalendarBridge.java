package pennsylvania.jahepi.com.apppenns.components;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by javier.hernandez on 15/03/2016.
 */
public class CalendarBridge {

    public static final long ID = 2;
    public static final String TIMEZONE = "America/Mexico_City";
    public static final int REMIDER_TIME = 15;

    private ContentResolver contentResolver;
    private SimpleDateFormat dateFormat;

    public CalendarBridge(Context context) {
        contentResolver = context.getContentResolver();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public long addEvent(String startDateTime, String endDateTime, String title, String description) {
        long eventID = 0;

        Calendar beginTime = Calendar.getInstance();
        try {
            beginTime.setTime(dateFormat.parse(startDateTime));
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        Calendar endTime = Calendar.getInstance();
        try {
            endTime.setTime(dateFormat.parse(endDateTime));
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, beginTime.getTimeInMillis());
        values.put(Events.DTEND, endTime.getTimeInMillis());
        values.put(Events.TITLE, title);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, CalendarBridge.ID);
        values.put(Events.EVENT_TIMEZONE, CalendarBridge.TIMEZONE);

        try {
            Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
            eventID = Long.parseLong(uri.getLastPathSegment());
            ContentValues valuesReminder = new ContentValues();
            valuesReminder.put(CalendarContract.Reminders.MINUTES, REMIDER_TIME);
            valuesReminder.put(Reminders.EVENT_ID, eventID);
            valuesReminder.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            contentResolver.insert(Reminders.CONTENT_URI, valuesReminder);
        } catch (SecurityException exp) {
            exp.printStackTrace();
        }
        return eventID;
    }

    public boolean removeEvent(int id) {
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
        int rows = contentResolver.delete(deleteUri, null, null);
        return rows > 0;
    }

    public static void startCalendar(Context context, String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar time = Calendar.getInstance();
        try {
            time.setTime(dateFormat.parse(date));
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, time.getTimeInMillis());
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        context.startActivity(intent);
    }
}
