package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.Map;

import hirondelle.date4j.DateTime;
import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.CalendarData;

/**
 * Created by jahepi on 13/03/16.
 */
public class CalendarAdapter extends CaldroidGridAdapter {

    public CalendarAdapter(Context context, int month, int year, Map<String, Object> caldroidData, Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calendar_cell, null);
        }

        int topPadding = convertView.getPaddingTop();
        int leftPadding = convertView.getPaddingLeft();
        int bottomPadding = convertView.getPaddingBottom();
        int rightPadding = convertView.getPaddingRight();

        TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
        TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);

        tv1.setTextColor(Color.BLACK);
        tv2.setTextColor(Color.RED);

        DateTime dateTime = this.datetimeList.get(position);

        String key = dateTime.format("YYYY-MM-DD");
        CustomApplication application = (CustomApplication) extraData.get("application");

        int year = dateTime.getYear();
        int month = dateTime.getMonth();
        Map<String, Object> map = application.getCalendarData(year, month);
        CalendarData calendarData = (CalendarData) map.get(key);
        tv1.setText("" + dateTime.getDay());
        if (calendarData != null) {
            tv2.setText("" + calendarData.getQuantity());
        }

        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        convertView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        return convertView;
    }
}
