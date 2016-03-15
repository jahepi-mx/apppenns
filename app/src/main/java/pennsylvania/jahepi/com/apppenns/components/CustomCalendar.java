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
            Calendar cal = Calendar.getInstance();
            Bundle args = new Bundle();
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            args.putInt(CaldroidFragment.MONTH, month);
            args.putInt(CaldroidFragment.YEAR, year);
            self.setArguments(args);
        }
        return self;
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CalendarAdapter(getActivity().getApplicationContext(), month, year, getCaldroidData(), extraData);
    }
}
