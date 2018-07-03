package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jahepi on 12/03/16.
 * Time dialog component.
 */
public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Calendar calendar;
    private DialogListener listener;

    public TimeDialog() {
        super();
        calendar = Calendar.getInstance();
    }

    public void setTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try {
            calendar.setTime(dateFormat.parse(time));
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public String getTime() {
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        listener.accept(this);
    }
}
