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
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.tasks.AttachmentTask;

/**
 * Created by jahepi on 20/03/16.
 */
public class FileAttachmentAdapter extends ArrayAdapter<Attachment> implements AttachmentTask.AttachmentTaskListener {

    private static final int REQUEST_CODE = 1;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5;

    private int mResource;
    private boolean hideDeleteOption;
    private OnChangeListener changeListener;

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

    public void setChangeListener(OnChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public boolean addAttachment(Attachment attachment) {
        Attachment.File file = attachment.getFile();
        File fileRef = new File(file.getPathNoName(), file.getName());
        if (fileRef.length() > MAX_FILE_SIZE) {
            Toast.makeText(getContext(), getContext().getString(R.string.txt_error_attachment), Toast.LENGTH_LONG).show();
            return false;
        }
        super.add(attachment);
        if (changeListener != null) {
            changeListener.onChange(attachment);
        }
        return true;
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
        holder.textViewName.setText(getItem(position).getFile().getName());

        if (!isHideDeleteOption()) {
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(getItem(position));
                    notifyDataSetChanged();
                    if (changeListener != null) {
                        changeListener.onChange(getItem(position));
                    }
                }
            });
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Attachment.File file = getItem(position).getFile();
                AttachmentTask attachmentTask = AttachmentTask.getInstance(getContext());
                if (!attachmentTask.isRunning()) {
                    attachmentTask.setFile(file);
                    attachmentTask.setManager(((Activity) getContext()).getFragmentManager());
                    attachmentTask.setListener(FileAttachmentAdapter.this);
                    attachmentTask.execute();
                }
            }
        });

        return convertView;
    }

    @Override
    public void onFinish(boolean success, boolean downloaded, Attachment.File file) {
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

    public static interface OnChangeListener {
        public void onChange(Attachment attachment);
    }
}
