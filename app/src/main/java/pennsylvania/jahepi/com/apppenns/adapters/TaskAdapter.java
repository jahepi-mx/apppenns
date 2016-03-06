package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
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
 */
public class TaskAdapter extends ArrayAdapter<Entity> {

    private final static int MESSAGE_LENGTH = 35;
    private final static String URI_TASK =  "@drawable/task";
    private final static String TICK = "✔";


    public TaskAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_item, parent, false);

            TextView taskText = (TextView) convertView.findViewById(R.id.taskText);
            TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.taskIcon);
            int imageResourceTask = getContext().getResources().getIdentifier(URI_TASK, null, getContext().getPackageName());
            Drawable drawableTask = getContext().getResources().getDrawable(imageResourceTask);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.taskText = taskText;
            viewHolder.taskTitle = taskTitle;
            viewHolder.drawableTask = drawableTask;
            viewHolder.imageView = imageView;

            viewHolder.imageView.setImageDrawable(viewHolder.drawableTask);

            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Task task = (Task) getItem(position);

        holder.taskTitle.setTypeface(null, Typeface.BOLD);
        holder.taskTitle.setText(task.getClient());
        if (task.isSend()) {
            holder.taskTitle.setText(holder.taskTitle.getText() + TICK);
        }

        holder.taskText.setText(Util.abbreviate(task.getDescription(), MESSAGE_LENGTH));

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
        Drawable drawableTask;
        ImageView imageView;
    }
}
