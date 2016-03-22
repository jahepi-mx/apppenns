package pennsylvania.jahepi.com.apppenns.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.tasks.AttachmentTask;

/**
 * Created by jahepi on 20/03/16.
 */
public class FileAttachmentAdapter extends ArrayAdapter<Message.File> implements AttachmentTask.AttachmentTaskListener {

    private static final int REQUEST_CODE = 1;

    private int mResource;
    private boolean hideDeleteOption;

    public FileAttachmentAdapter(Context context, int resource) {
        super(context, resource);
        mResource = resource;
    }

    public boolean isHideDeleteOption() {
        return hideDeleteOption;
    }

    public void setHideDeleteOption(boolean hideDeleteOption) {
        this.hideDeleteOption = hideDeleteOption;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, null);

            ViewHolder holder = new ViewHolder();
            TextView textViewName = (TextView) convertView.findViewById(R.id.name);
            ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteFileBtn);
            ImageButton viewBtn = (ImageButton) convertView.findViewById(R.id.viewFileBtn);
            holder.textViewName = textViewName;
            holder.deleteBtn = deleteBtn;
            holder.viewBtn = viewBtn;
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.textViewName.setText(getItem(position).getName());

        if (!isHideDeleteOption()) {
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(getItem(position));
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message.File file = getItem(position);
                AttachmentTask attachmentTask = new AttachmentTask(getContext());
                attachmentTask.setFile(file);
                attachmentTask.setListener(FileAttachmentAdapter.this);
                attachmentTask.execute();
            }
        });

        return convertView;
    }

    @Override
    public void onFinish(boolean success, boolean downloaded, Message.File file) {
        if (success) {
            if (downloaded) {
                ((CustomApplication) getContext().getApplicationContext()).saveFile(file);
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(file.getPath())), file.getMime());
            ((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE);
        } else {
            Toast.makeText(getContext(), R.string.txt_error_file, Toast.LENGTH_LONG).show();
        }
    }

    private static class ViewHolder {
        TextView textViewName;
        ImageButton deleteBtn, viewBtn;
    }
}
