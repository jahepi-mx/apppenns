package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.Map;

import hirondelle.date4j.DateTime;
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
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calendar_cell, null);

            TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
            TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);

            ViewHolder holder = new ViewHolder();
            holder.tv1 = tv1;
            holder.tv2 = tv2;

            convertView.setTag(holder);
        }

        int topPadding = convertView.getPaddingTop();
        int leftPadding = convertView.getPaddingLeft();
        int bottomPadding = convertView.getPaddingBottom();
        int rightPadding = convertView.getPaddingRight();

        ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.tv1.setTextColor(Color.BLACK);
        holder.tv2.setTextColor(Color.RED);

        DateTime dateTime = this.datetimeList.get(position);
        Resources resources = context.getResources();

        String key = dateTime.format("YYYY-MM-DD");
        CalendarData calendarData = (CalendarData) getExtraData().get(key);

        boolean hasTasks = false;
        holder.tv1.setText("" + dateTime.getDay());
        if (calendarData != null) {
            holder.tv2.setText("" + calendarData.getQuantity());
            holder.tv2.setBackgroundColor(Color.RED);
            holder.tv2.setTextColor(Color.WHITE);
            hasTasks = true;
        } else {
            holder.tv2.setText("");
            holder.tv2.setBackgroundColor(Color.TRANSPARENT);
        }

        if (dateTime.getMonth() != month) {
            holder.tv1.setTextColor(resources.getColor(com.caldroid.R.color.caldroid_darker_gray));
        }

        boolean shouldResetSelectedView = false;
        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            convertView.setBackgroundColor(resources.getColor(com.caldroid.R.color.caldroid_sky_blue));
            holder.tv1.setTextColor(Color.BLACK);
            if (!hasTasks) {
                holder.tv2.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            shouldResetSelectedView = true;
        }

        if (shouldResetSelectedView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
                convertView.setBackgroundResource(com.caldroid.R.drawable.red_border);
            } else {
                convertView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
            }
        }

        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        convertView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        return convertView;
    }

    private static class ViewHolder {
        TextView tv1, tv2;
    }
}
