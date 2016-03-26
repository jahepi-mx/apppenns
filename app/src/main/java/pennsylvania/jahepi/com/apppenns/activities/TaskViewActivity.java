package pennsylvania.jahepi.com.apppenns.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.components.filechooser.Config;
import pennsylvania.jahepi.com.apppenns.components.filechooser.activities.FileChooserActivity;
import pennsylvania.jahepi.com.apppenns.dialogs.CheckOutDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Coord;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by jahepi on 06/03/16.
 */
public class TaskViewActivity extends AuthActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final String TAG = "TaskViewActivity";
    private static final int ALPHA = 50;
    public final static int REQUEST_CODE_FILE = 2;

    private Button checkinBtn, checkoutBtn, backBtn;
    private AlertDialog.Builder checkInAlert;
    private CheckOutDialog checkOutAlert;
    private Task task;
    private FileAttachmentAdapter fileAttachmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_view);

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskViewActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button filesBtn = (Button) findViewById(R.id.filesBtn);
        filesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(TaskViewActivity.this, FileChooserActivity.class), REQUEST_CODE_FILE);
            }
        });

        Intent intent = getIntent();
        task = (Task) intent.getSerializableExtra(CustomApplication.GENERIC_INTENT);

        checkinBtn = (Button) findViewById(R.id.checkinBtn);
        checkoutBtn = (Button) findViewById(R.id.checkoutBtn);
        backBtn = (Button) findViewById(R.id.backBtn);

        if (task != null) {

            fileAttachmentAdapter = new FileAttachmentAdapter(this, R.layout.file_item);
            fileAttachmentAdapter.setChangeListener(new FileAttachmentAdapter.OnChangeListener() {
                @Override
                public void onChange(Attachment attachment) {
                    task.setSend(false);
                    task.setModifiedDate(Util.getDateTime());
                    ArrayList<Attachment> attachments = new ArrayList<Attachment>();
                    for (int i = 0; i < fileAttachmentAdapter.getCount(); i++) {
                        attachments.add(fileAttachmentAdapter.getItem(i));
                    }
                    task.addAttachments(attachments);
                    application.saveTask(task);
                }
            });
            Iterator<Attachment> iterator = task.getAttachmentsIterator();
            fileAttachmentAdapter.addAll(task.getAttachments());

            ListView attachmentList = (ListView) findViewById(R.id.attachmentsListView);
            attachmentList.setAdapter(fileAttachmentAdapter);

            TextView dateTextView = (TextView) findViewById(R.id.taskDateValue);
            TextView clientTextView = (TextView) findViewById(R.id.taskClientValue);
            TextView descTextView = (TextView) findViewById(R.id.taskDescValue);
            TextView timeTextView = (TextView) findViewById(R.id.taskTimeValue);
            TextView typeTextView = (TextView) findViewById(R.id.taskTypeValue);

            clientTextView.setText(task.getClient().getName() + " " + task.getAddress().getAddress());
            descTextView.setText(task.getDescription());
            dateTextView.setText(task.getDate());
            timeTextView.setText(String.format(getString(R.string.txt_time_value), task.getStartTime(), task.getEndTime()));
            typeTextView.setText(task.getType().getName());

            if (task.isCheckin()) {
                checkinBtn.getBackground().setAlpha(ALPHA);
            }

            if (task.isCheckout()) {
                checkoutBtn.getBackground().setAlpha(ALPHA);
            }

            if (!task.isCheckin() && !task.isCheckout()) {
                checkoutBtn.getBackground().setAlpha(ALPHA);
            }
        }

        checkInAlert = new AlertDialog.Builder(this);
        checkInAlert.setPositiveButton(R.string.btn_yes, this);
        checkInAlert.setNegativeButton(R.string.btn_no, this);
        checkInAlert.setTitle(getString(R.string.txt_confirm));
        checkInAlert.setMessage(getString(R.string.txt_confirm_checkin));
        checkInAlert.setIcon(R.drawable.task);

        checkOutAlert = new CheckOutDialog(this, new DialogListener() {
            @Override
            public void accept(Object dialog) {
                checkout();
            }

            @Override
            public void cancel(Object dialog) {

            }
        });

        backBtn.setOnClickListener(this);
        checkinBtn.setOnClickListener(this);
        checkoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            checkin();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == checkinBtn) {
            checkInAlert.show();
        }
        if (v == checkoutBtn) {
            checkOutAlert.show();
        }
        if (v == backBtn) {
            Intent intent = new Intent(this, TaskListActivity.class);
            if (task != null) {
                intent.putExtra(CustomApplication.GENERIC_INTENT, task.getDate());
            }
            startActivity(intent);
            finish();
        }
    }

    private void checkin() {
        if (task != null) {
            if (!application.isGpsEnabled()) {
                toast(getString(R.string.txt_error_gps));
                return;
            }
            if (!task.isCheckin() && !task.isCheckout() && !task.isCancelled()) {
                task.setCheckInDate(Util.getDateTime());
                task.setModifiedDate(Util.getDateTime());
                Coord coord = new Coord();
                coord.setLatitude(application.getLatitude());
                coord.setLongitude(application.getLongitude());
                task.setCheckInCoord(coord);
                task.setCheckin(true);
                task.setSend(false);
                if (application.saveTask(task)) {
                    Intent intent = new Intent(this, TaskListActivity.class);
                    intent.putExtra(CustomApplication.GENERIC_INTENT, task.getDate());
                    startActivity(intent);
                    finish();
                    return;
                }
            } else {
                toast(getString(R.string.txt_error_checkin));
                return;
            }
        }
        toast(getString(R.string.txt_error_task));
    }

    private void checkout() {
        if (task != null) {
            if (!application.isGpsEnabled()) {
                toast(getString(R.string.txt_error_gps));
                return;
            }
            if (task.isCheckin() && !task.isCheckout() && !task.isCancelled()) {
                task.setCheckOutDate(Util.getDateTime());
                task.setModifiedDate(Util.getDateTime());
                Coord coord = new Coord();
                coord.setLatitude(application.getLatitude());
                coord.setLongitude(application.getLongitude());
                task.setCheckOutCoord(coord);
                task.setCheckout(true);
                task.setSend(false);
                task.setConclusion(checkOutAlert.getConclusion());
                task.setEmails(checkOutAlert.getEmails());
                if (application.saveTask(task)) {
                    Intent intent = new Intent(this, TaskListActivity.class);
                    intent.putExtra(CustomApplication.GENERIC_INTENT, task.getDate());
                    startActivity(intent);
                    finish();
                    return;
                }
            } else {
                toast(getString(R.string.txt_error_checkout));
                return;
            }
        }
        toast(getString(R.string.txt_error_task));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILE && data != null) {
            File file = (File) data.getSerializableExtra(Config.KEY_FILE_SELECTED);
            if (file != null) {
                Log.d(TAG, file.getAbsolutePath());
                Attachment.File attachmentFile = new Attachment.File();
                attachmentFile.setPath(file.getAbsolutePath());
                attachmentFile.setName(file.getName());
                String extension = "";
                String mime = "";
                try {
                    extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toURL().toString());
                } catch (MalformedURLException exp) {
                    exp.printStackTrace();;
                }
                if (extension != null) {
                    mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                attachmentFile.setMime(mime);
                attachmentFile.setModifiedDate(Util.getDateTime());
                Attachment attachment = new Attachment();
                attachment.setFile(attachmentFile);
                if (fileAttachmentAdapter.addAttachment(attachment)) {
                    fileAttachmentAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
