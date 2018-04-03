package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.activities.TaskViewActivity;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.components.filechooser.activities.FileChooserActivity;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;

/**
 * Created by javier.hernandez on 08/03/2016.
 */
public class CheckOutDialog extends AlertDialog implements View.OnClickListener {

    public final static int REQUEST_CODE_FILE_FROM_CHECKOUT = 8;
    public final static int REQUEST_IMAGE_CAPTURE_FROM_CHECKOUT = 16;

    private EditText editText;
    private MultiAutoCompleteTextView emailTextiew;
    private DialogListener listener;
    private AutoCompleteEmailAdapter adapter;
    private Button yesBtn, noBtn;
    private ListView attachmentList;
    public FileAttachmentAdapter fileAttachmentAdapter;
    public File photoFile;
    private TaskViewActivity parentActivity;

    public CheckOutDialog(Context context, TaskViewActivity parentActivity, DialogListener listener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) inflater.inflate(R.layout.checkout_dialog, null);
        this.parentActivity = parentActivity;
        editText = (EditText) view.findViewById(R.id.taskConclusionEditText);
        adapter = new AutoCompleteEmailAdapter(context, R.layout.generic_item);
        emailTextiew = (MultiAutoCompleteTextView) view.findViewById(R.id.emailText);
        emailTextiew.setAdapter(adapter);
        emailTextiew.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        fileAttachmentAdapter = new FileAttachmentAdapter(this.parentActivity, R.layout.file_item, false);
        attachmentList = (ListView) view.findViewById(R.id.attachmentsListView);
        if (parentActivity.task != null) {
            fileAttachmentAdapter.addAll(parentActivity.task.getAttachmentsFromConclusion());
        }
        attachmentList.setAdapter(fileAttachmentAdapter);

        int numberOfAttachments = fileAttachmentAdapter.getCount();
        ViewGroup.LayoutParams params = attachmentList.getLayoutParams();
        int size = numberOfAttachments == 0 ? 160 : numberOfAttachments * 160;
        params.height = size;
        attachmentList.setLayoutParams(params);

        fileAttachmentAdapter.setChangeListener(new FileAttachmentAdapter.FileAttachmentAdapterListener() {
            @Override
            public void onChange(Attachment attachment) {
                int numberOfAttachments = fileAttachmentAdapter.getCount();
                ViewGroup.LayoutParams params = attachmentList.getLayoutParams();
                int size = numberOfAttachments == 0 ? 160 : numberOfAttachments * 160;
                params.height = size;
                attachmentList.setLayoutParams(params);
                CheckOutDialog.this.parentActivity.updateTask(true);
            }

            @Override
            public void onRemove(Attachment attachment) {
                int numberOfAttachments = fileAttachmentAdapter.getCount();
                ViewGroup.LayoutParams params = attachmentList.getLayoutParams();
                int size = numberOfAttachments == 0 ? 160 : numberOfAttachments * 160;
                params.height = size;
                attachmentList.setLayoutParams(params);
                CheckOutDialog.this.parentActivity.updateTask(true);
            }
        });

        Button photoBtn = (Button) view.findViewById(R.id.photoBtn);
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(CheckOutDialog.this.getContext().getPackageManager()) != null) {
                    photoFile = Util.createImageFile(CheckOutDialog.this.parentActivity.getCustomApplication().getAndroidId());
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        CheckOutDialog.this.parentActivity.startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE_FROM_CHECKOUT);
                    }
                }
            }
        });

        Button filesBtn = (Button) view.findViewById(R.id.filesBtn);
        filesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOutDialog.this.parentActivity.startActivityForResult(new Intent(CheckOutDialog.this.parentActivity, FileChooserActivity.class), REQUEST_CODE_FILE_FROM_CHECKOUT);
            }
        });

        setView(view);
        setTitle(R.string.txt_confirm);
        setIcon(R.drawable.ubication_black);
        setMessage(context.getString(R.string.txt_confirm_checkout));
        noBtn = (Button) view.findViewById(R.id.noBtn);
        yesBtn = (Button) view.findViewById(R.id.yesBtn);
        this.listener = listener;

        noBtn.setOnClickListener(this);
        yesBtn.setOnClickListener(this);
    }

    public String getConclusion() {
        return editText.getText().toString();
    }

    public String getEmails() {
        return emailTextiew.getText().toString();
    }

    public void setConclusion(String conclusion) {
        editText.setText(conclusion);
    }

    public void setEmails(String emails) {
        emailTextiew.setText(emails);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == yesBtn) {
            listener.accept(this);
        }
    }

    private static class AutoCompleteEmailAdapter extends ArrayAdapter<String> {

        public AutoCompleteEmailAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public String getItem(int position) {
            try {
                return super.getItem(position);
            } catch (Exception exp) {
                return null;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null || convertView.getTag() == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.generic_item, null);
                TextView textViewName = (TextView) convertView.findViewById(R.id.name);
                ViewHolder holder = new ViewHolder();
                holder.textViewName = textViewName;
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position);
            if (item != null) {
                holder.textViewName.setText(getItem(position));
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint != null) {
                        String keyword = constraint.toString();
                        ArrayList<String> emails = ((CustomApplication) getContext().getApplicationContext()).getUserEmails(keyword);
                        if (emails.size() > 0) {
                            results.values = emails;
                            results.count = emails.size();
                        }
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        clear();
                        addAll((ArrayList<String>) results.values);
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        static class ViewHolder {
            TextView textViewName;
        }
    }
}
