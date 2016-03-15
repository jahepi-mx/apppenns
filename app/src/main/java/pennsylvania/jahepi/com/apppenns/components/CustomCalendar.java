package pennsylvania.jahepi.com.apppenns.components;

import android.os.Bundle;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.Calendar;

import pennsylvania.jahepi.com.apppenns.adapters.CalendarAdapter;

/**
 * Created by jahepi on 13/03/16.
 */
public class CustomCalendar extends CaldroidFragment {

    private static CustomCalendar self;

    public static CustomCalendar getInstance() {
        if (self == null) {
            self = new CustomCalendar();
            Bundle args = new Bundle();
            self.setArguments(args);
        }
        return self;
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CalendarAdapter(getActivity().getApplicationContext(), month, year, getCaldroidData(), extraData);
    }
}
