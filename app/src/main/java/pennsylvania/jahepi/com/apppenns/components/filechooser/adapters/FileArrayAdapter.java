package pennsylvania.jahepi.com.apppenns.components.filechooser.adapters;

/**
 * Created by jahepi on 20/03/16.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.components.filechooser.Config;
import pennsylvania.jahepi.com.apppenns.components.filechooser.FileInfo;

public class FileArrayAdapter extends ArrayAdapter<FileInfo> {

    private Context context;
    private int resorceID;
    private List<FileInfo> items;

    public FileArrayAdapter(Context context, int textViewResourceId, List<FileInfo> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.resorceID = textViewResourceId;
        this.items = objects;
    }

    public FileInfo getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resorceID, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(android.R.id.icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.details = (TextView) convertView.findViewById(R.id.details);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FileInfo option = items.get(position);
        if (option != null) {

            if (option.getData().equalsIgnoreCase(Config.FOLDER)) {
                viewHolder.icon.setImageResource(R.drawable.folder);
            } else if (option.getData().equalsIgnoreCase(Config.PARENT_FOLDER)) {
                viewHolder.icon.setImageResource(R.drawable.back);
            } else {
                String name = option.getName().toLowerCase();
                if (name.endsWith(Config.XLS) || name.endsWith(Config.XLSX)) {
                    viewHolder.icon.setImageResource(R.drawable.xls);
                } else if (name.endsWith(Config.DOC) || name.endsWith(Config.DOCX)) {
                    viewHolder.icon.setImageResource(R.drawable.doc);
                } else if (name.endsWith(Config.PPT) || option.getName().endsWith(Config.PPTX)) {
                    viewHolder.icon.setImageResource(R.drawable.ppt);
                } else if (name.endsWith(Config.PDF)) {
                    viewHolder.icon.setImageResource(R.drawable.pdf);
                } else if (name.endsWith(Config.APK)) {
                    viewHolder.icon.setImageResource(R.drawable.apk);
                } else if (name.endsWith(Config.TXT)) {
                    viewHolder.icon.setImageResource(R.drawable.txt);
                } else if (name.endsWith(Config.JPG) || name.endsWith(Config.JPEG)) {
                    viewHolder.icon.setImageResource(R.drawable.jpg);
                } else if (name.endsWith(Config.PNG)) {
                    viewHolder.icon.setImageResource(R.drawable.png);
                } else if (name.endsWith(Config.ZIP)) {
                    viewHolder.icon.setImageResource(R.drawable.zip);
                } else if (name.endsWith(Config.RTF)) {
                    viewHolder.icon.setImageResource(R.drawable.rtf);
                } else if (name.endsWith(Config.GIF)) {
                    viewHolder.icon.setImageResource(R.drawable.gif);
                } else if (name.endsWith(Config.AVI)) {
                    viewHolder.icon.setImageResource(R.drawable.avi);
                } else if (name.endsWith(Config.MP3)) {
                    viewHolder.icon.setImageResource(R.drawable.mp3);
                } else if (name.endsWith(Config.MP4)) {
                    viewHolder.icon.setImageResource(R.drawable.mp4);
                } else if (name.endsWith(Config.RAR)) {
                    viewHolder.icon.setImageResource(R.drawable.rar);
                } else if (name.endsWith(Config.ACC)) {
                    viewHolder.icon.setImageResource(R.drawable.aac);
                } else {
                    viewHolder.icon.setImageResource(R.drawable.blank);
                }
            }

            viewHolder.name.setText(option.getName());
            viewHolder.details.setText(option.getData());

        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView details;
    }

}
