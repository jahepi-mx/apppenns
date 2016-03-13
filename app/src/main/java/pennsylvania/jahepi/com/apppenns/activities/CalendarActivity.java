package pennsylvania.jahepi.com.apppenns.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.components.CustomCalendar;
import pennsylvania.jahepi.com.apppenns.entities.CalendarData;

/**
 * Created by jahepi on 12/03/16.
 */
public class CalendarActivity extends AuthActivity implements View.OnClickListener {

    private CustomCalendar caldroidFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(this);

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        Calendar cal = Calendar.getInstance();
        Intent intent = getIntent();
        String date = intent.getStringExtra(CustomApplication.GENERIC_INTENT);
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                cal.setTime(dateFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        caldroidFragment = new CustomCalendar();
        Bundle args = new Bundle();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        args.putInt(CaldroidFragment.MONTH, month);
        args.putInt(CaldroidFragment.YEAR, year);
        caldroidFragment.setArguments(args);
        caldroidFragment.setSelectedDate(cal.getTime());

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendarFragment, caldroidFragment);
        t.commit();

        caldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                Intent intent = new Intent(CalendarActivity.this, TaskListActivity.class);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                intent.putExtra(CustomApplication.GENERIC_INTENT, dateFormat.format(date));
                startActivity(intent);
            }

            public void onChangeMonth(int month, int year) {
                ArrayList<CalendarData> list = application.getCalendarData(year, month);
                Iterator<CalendarData> iterator = list.iterator();
                HashMap<String, Object> extraData = new HashMap<String, Object>();
                while (iterator.hasNext()) {
                    CalendarData data = iterator.next();
                    extraData.put(data.getDate(), data);
                }
                caldroidFragment.setExtraData(extraData);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
