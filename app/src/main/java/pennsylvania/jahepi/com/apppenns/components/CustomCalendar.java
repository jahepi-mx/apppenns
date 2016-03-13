package pennsylvania.jahepi.com.apppenns.components;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import pennsylvania.jahepi.com.apppenns.adapters.CalendarAdapter;

/**
 * Created by jahepi on 13/03/16.
 */
public class CustomCalendar extends CaldroidFragment {

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CalendarAdapter(getActivity(), month, year, getCaldroidData(), extraData);
    }
}
