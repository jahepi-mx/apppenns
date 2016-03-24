package pennsylvania.jahepi.com.apppenns.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.dialogs.CheckOutDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Coord;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by jahepi on 06/03/16.
 */
public class TaskViewActivity extends AuthActivity implements View.OnClickListener, DialogInterface.OnClickListener, DialogListener {

    private static final String TAG = "TaskViewActivity";
    private static final int ALPHA = 50;

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

        Intent intent = getIntent();
        task = (Task) intent.getSerializableExtra(CustomApplication.GENERIC_INTENT);

        checkinBtn = (Button) findViewById(R.id.checkinBtn);
        checkoutBtn = (Button) findViewById(R.id.checkoutBtn);
        backBtn = (Button) findViewById(R.id.backBtn);

        if (task != null) {

            fileAttachmentAdapter = new FileAttachmentAdapter(this, R.layout.file_item);
            fileAttachmentAdapter.setHideDeleteOption(true);

            Iterator<Attachment> iterator = task.getAttachmentsIterator();
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                fileAttachmentAdapter.add(attachment);
            }

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

        checkOutAlert = new CheckOutDialog(this, this);

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
    public void accept(Object dialogParam) {
        checkout();
    }

    @Override
    public void cancel(Object dialogParam) {

    }
}
