package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.entities.Entity;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by jahepi on 05/03/16.
 * Class adapter for tasks.
 */
public class TaskAdapter extends ArrayAdapter<Entity> {

    private final static int MESSAGE_LENGTH = 35;
    private final static String URI_TASK =  "@drawable/ubication_black";
    private final static String TICK = "✔";

    private int mResource;

    public TaskAdapter(Context context, int resource) {
        super(context, resource);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);

            TextView taskText = (TextView) convertView.findViewById(R.id.taskText);
            TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);
            TextView hasChildrenTxt = (TextView) convertView.findViewById(R.id.hasChildrenTxt);
            TextView taskDate = (TextView) convertView.findViewById(R.id.taskDate);
            TextView taskStatus = (TextView) convertView.findViewById(R.id.taskStatus);
            TextView taskType = (TextView) convertView.findViewById(R.id.taskType);
            TextView taskDistance = (TextView) convertView.findViewById(R.id.taskDistance);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.taskIcon);
            int imageResourceTask = getContext().getResources().getIdentifier(URI_TASK, null, getContext().getPackageName());
            Drawable drawableTask = getContext().getResources().getDrawable(imageResourceTask);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.taskText = taskText;
            viewHolder.taskTitle = taskTitle;
            viewHolder.drawableTask = drawableTask;
            viewHolder.imageView = imageView;
            viewHolder.taskStatus = taskStatus;
            viewHolder.taskDistance = taskDistance;
            viewHolder.taskType = taskType;
            viewHolder.taskDate = taskDate;
            viewHolder.hasChildrenTxt = hasChildrenTxt;

            viewHolder.imageView.setImageDrawable(viewHolder.drawableTask);

            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Task task = (Task) getItem(position);

        holder.taskTitle.setTypeface(null, Typeface.BOLD);
        holder.taskTitle.setText(task.getClient().getName());

        if (task.hasChildren()) {
            holder.hasChildrenTxt.setVisibility(View.VISIBLE);
        } else {
            holder.hasChildrenTxt.setVisibility(View.GONE);
        }

        if (task.getDistance() != 0) {
            holder.taskDistance.setText(String.format(getContext().getString(R.string.txt_distance), task.getDistance()));
        } else {
            holder.taskDistance.setText(getContext().getString(R.string.txt_distance_measure));
        }

        if (task.isSend()) {
            holder.taskTitle.setText(holder.taskTitle.getText() + TICK);
        }

        if (!task.isCheckin() && !task.isCheckout()) {
            holder.taskStatus.setText(getContext().getString(R.string.txt_checkin));
            holder.taskStatus.setTextColor(Color.BLUE);
        }

        if (task.isCheckin() && !task.isCheckout()) {
            holder.taskStatus.setText(getContext().getString(R.string.txt_checkout));
            holder.taskStatus.setTextColor(Color.GREEN);
        }

        if (task.isCheckin() && task.isCheckout()) {
            holder.taskStatus.setText(getContext().getString(R.string.txt_done));
            holder.taskStatus.setTextColor(Color.BLACK);
        }

        if (task.isCancelled()) {
            holder.taskStatus.setText(getContext().getString(R.string.txt_cancelled));
            holder.taskStatus.setTextColor(Color.GRAY);
        }

        if (task.getStatus().equals(Task.STATUS_TRACKING)) {
            holder.taskType.setText(getContext().getString(R.string.txt_tracking_item));
        } else if (task.getStatus().equals(Task.STATUS_RESCHEDULED)) {
            holder.taskType.setText(getContext().getString(R.string.txt_reprogrammed_item));
        } else {
            holder.taskType.setText("");
        }
        holder.taskDate.setText(task.getDate());
        holder.taskText.setText(Util.abbreviate(task.getAddress().getAddress(), MESSAGE_LENGTH));

        return convertView;
    }

    public Task getTask(Task task) {
        for (int i = 0; i < getCount(); i++) {
            Task taskAdapter = (Task) getItem(i);
            if (task.equals(taskAdapter)) {
                return taskAdapter;
            }
        }
        return null;
    }

    private static class ViewHolder {
        TextView taskText;
        TextView taskTitle;
        TextView hasChildrenTxt;
        TextView taskStatus;
        TextView taskType;
        TextView taskDate;
        TextView taskDistance;
        Drawable drawableTask;
        ImageView imageView;
    }
}
