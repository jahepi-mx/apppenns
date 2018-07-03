package pennsylvania.jahepi.com.apppenns.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.tasks.AttachmentTask;

/**
 * Created by jahepi on 20/03/16.
 * Class adapter for attachments
 */
public class FileAttachmentAdapter extends ArrayAdapter<Attachment> implements AttachmentTask.AttachmentTaskListener, DialogInterface.OnClickListener {

    private static final int REQUEST_CODE = 1;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5;

    private int mResource;
    private boolean hideDeleteOption;
    private CustomAlertDialog removeDialog;
    private FileAttachmentAdapterListener listener;
    private int selectedPosition;
    private boolean hideDeleteButton;

    public FileAttachmentAdapter(Context context, int resource, boolean hideDeleteButton) {
        super(context, resource);
        mResource = resource;
        removeDialog = new CustomAlertDialog(context);
        removeDialog.setPositiveButton(R.string.btn_yes, this);
        removeDialog.setNegativeButton(R.string.btn_no, this);
        removeDialog.setTitle(R.string.txt_delete);
        removeDialog.setMessage(R.string.txt_confirm_delete);
        removeDialog.setIcon(R.drawable.file_black);
        this.hideDeleteButton = hideDeleteButton;
    }

    public ArrayList<Attachment> getAttachments() {
        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        for (int i = 0; i < getCount(); i++) {
            attachments.add(getItem(i));
        }
        return attachments;
    }

    public boolean isHideDeleteOption() {
        return hideDeleteOption;
    }

    public void setHideDeleteOption(boolean hideDeleteOption) {
        this.hideDeleteOption = hideDeleteOption;
    }

    public void setChangeListener(FileAttachmentAdapterListener listener) {
        this.listener = listener;
    }

    public boolean addAttachment(Attachment attachment) {
        Attachment.File file = attachment.getFile();
        File fileRef = new File(file.getPathNoName(), file.getName());
        if (fileRef.length() > MAX_FILE_SIZE) {
            Toast.makeText(getContext(), getContext().getString(R.string.txt_error_attachment), Toast.LENGTH_LONG).show();
            return false;
        }
        super.add(attachment);
        if (listener != null) {
            listener.onChange(attachment);
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
            if (this.hideDeleteButton) {
                deleteBtn.setVisibility(View.GONE);
            }
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.textViewName.setText(getItem(position).getFile().getName());

        if (!isHideDeleteOption()) {
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = position;
                    removeDialog.show();
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
            try {
                ((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE);
            } catch (Exception exp) {
                Toast.makeText(getContext(), R.string.txt_error_open_file, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.txt_error_file, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Attachment attachment = getItem(selectedPosition);
            remove(attachment);
            notifyDataSetChanged();
            if (listener != null) {
                listener.onRemove(attachment);
            }
        }
    }

    private static class ViewHolder {
        TextView textViewName;
        ImageButton deleteBtn, viewBtn;
    }

    public static interface FileAttachmentAdapterListener {
        public void onChange(Attachment attachment);
        public void onRemove(Attachment attachment);
    }
}
