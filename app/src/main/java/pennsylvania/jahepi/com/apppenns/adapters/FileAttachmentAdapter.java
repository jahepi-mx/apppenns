package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Message;

/**
 * Created by jahepi on 20/03/16.
 */
public class FileAttachmentAdapter extends ArrayAdapter<Message.File> {

    private int mResource;

    public FileAttachmentAdapter(Context context, int resource) {
        super(context, resource);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, null);

            ViewHolder holder = new ViewHolder();
            TextView textViewName = (TextView) convertView.findViewById(R.id.name);
            holder.textViewName = textViewName;
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.textViewName.setText(getItem(position).getName());

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewName;
    }
}
