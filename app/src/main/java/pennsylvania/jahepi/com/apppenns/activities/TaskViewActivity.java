package pennsylvania.jahepi.com.apppenns.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.CustomAlertDialog;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.components.GoogleMapFragment;
import pennsylvania.jahepi.com.apppenns.components.filechooser.Config;
import pennsylvania.jahepi.com.apppenns.components.filechooser.activities.FileChooserActivity;
import pennsylvania.jahepi.com.apppenns.dialogs.CheckOutDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Coord;
import pennsylvania.jahepi.com.apppenns.entities.Notification;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.tasks.GpsTask;

/**
 * Created by jahepi on 06/03/16.
 */
public class TaskViewActivity extends AuthActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final String TAG = "TaskViewActivity";
    private static final int ALPHA = 50;
    public final static int REQUEST_CODE_FILE = 2;
    private final static int REQUEST_IMAGE_CAPTURE = 3;

    private final static String TASK_STATE = "task_state";

    private Button checkinBtn, checkoutBtn, backBtn;
    private CustomAlertDialog checkInAlert;
    private CheckOutDialog checkOutAlert;
    private Task task;
    private FileAttachmentAdapter fileAttachmentAdapter;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_view);
        MapsInitializer.initialize(getApplicationContext());
        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskViewActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button reprogrammedBtn = (Button) findViewById(R.id.reprogrammedBtn);
        reprogrammedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskViewActivity.this, AddTaskActivity.class);
                task.setStatus(Task.STATUS_RESCHEDULED);
                intent.putExtra(CustomApplication.ADDITIONAL_GENERIC_INTENT, task);
                startActivity(intent);
                finish();
            }
        });

        Button trackingBtn = (Button) findViewById(R.id.trackingBtn);
        trackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskViewActivity.this, AddTaskActivity.class);
                task.setStatus(Task.STATUS_TRACKING);
                intent.putExtra(CustomApplication.ADDITIONAL_GENERIC_INTENT, task);
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

        backBtn = (Button) findViewById(R.id.backBtn);

        if (savedInstanceState != null) {
            task = (Task) savedInstanceState.get(TASK_STATE);
        }

        if (task != null) {

            if (task.getParentTask() != null) {
                trackingBtn.setVisibility(View.GONE);
            } else {
                if (task.isCheckin() && task.isCheckout() && !task.isCancelled()) {
                    trackingBtn.setVisibility(View.VISIBLE);
                } else {
                    trackingBtn.setVisibility(View.GONE);
                }
            }

            checkinBtn = (Button) findViewById(R.id.checkinBtn);
            checkoutBtn = (Button) findViewById(R.id.checkoutBtn);

            GoogleMapFragment fragment = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            fragment.addLocation(getString(R.string.txt_map_me), BitmapDescriptorFactory.HUE_RED, application.getLatitude(), application.getLongitude());
            fragment.addLocation(getString(R.string.txt_map_client), BitmapDescriptorFactory.HUE_AZURE, task.getAddress().getCoord().getLatitude(), task.getAddress().getCoord().getLongitude());
            fragment.center(task.getAddress().getCoord().getLatitude(), task.getAddress().getCoord().getLongitude());

            Button photoBtn = (Button) findViewById(R.id.photoBtn);
            photoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        photoFile = Util.createImageFile(application.getAndroidId());
                        if (photoFile != null) {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }
            });

            fileAttachmentAdapter = new FileAttachmentAdapter(this, R.layout.file_item);
            fileAttachmentAdapter.setChangeListener(new FileAttachmentAdapter.FileAttachmentAdapterListener() {
                @Override
                public void onChange(Attachment attachment) {
                    updateTask(task);
                }

                @Override
                public void onRemove(Attachment attachment) {
                    updateTask(task);
                }
            });
            Iterator<Attachment> iterator = task.getAttachmentsIterator();
            fileAttachmentAdapter.addAll(task.getAttachments());

            ListView attachmentList = (ListView) findViewById(R.id.attachmentsListView);
            attachmentList.setAdapter(fileAttachmentAdapter);

            TextView dateTextView = (TextView) findViewById(R.id.taskDateValue);
            TextView clientTextView = (TextView) findViewById(R.id.taskClientValue);
            TextView descTextView = (TextView) findViewById(R.id.taskDescValue);
            TextView conclusionTextView = (TextView) findViewById(R.id.taskConclusionValue);
            TextView timeTextView = (TextView) findViewById(R.id.taskTimeValue);
            TextView typeTextView = (TextView) findViewById(R.id.taskTypeValue);
            TextView notificationsTextView = (TextView) findViewById(R.id.notificationsValues);

            clientTextView.setText(task.getClient().getName() + " " + task.getAddress().getAddress());
            descTextView.setText(task.getDescription());
            conclusionTextView.setText(task.getConclusion());
            dateTextView.setText(task.getDate());
            timeTextView.setText(String.format(getString(R.string.txt_time_value), task.getStartTime(), task.getEndTime()));
            typeTextView.setText(task.getType().getName());

            ArrayList<Notification> notifications = task.getNotifications();
            for (Notification notification : notifications) {
                notificationsTextView.setText(notificationsTextView.getText() + String.format(getString(R.string.txt_notification), notification.getFrom().getName(), notification.getNotification(), notification.getEventDate()));
            }

            if (task.isCheckin()) {
                checkinBtn.getBackground().setAlpha(ALPHA);
            }

            if (task.isCheckout()) {
                checkoutBtn.getBackground().setAlpha(ALPHA);
            }

            if (!task.isCheckin() && !task.isCheckout()) {
                checkoutBtn.getBackground().setAlpha(ALPHA);
            }

            checkInAlert = new CustomAlertDialog(this);
            checkInAlert.setPositiveButton(R.string.btn_yes, this);
            checkInAlert.setNegativeButton(R.string.btn_no, this);
            checkInAlert.setTitle(getString(R.string.txt_confirm));
            checkInAlert.setMessage(getString(R.string.txt_confirm_checkin));
            checkInAlert.setIcon(R.drawable.ubication_black);

            checkOutAlert = new CheckOutDialog(this, new DialogListener() {
                @Override
                public void accept(Object dialog) {
                    checkout();
                }

                @Override
                public void cancel(Object dialog) {

                }
            });

            checkinBtn.setOnClickListener(this);
            checkoutBtn.setOnClickListener(this);
        }

        backBtn.setOnClickListener(this);
    }

    private void updateTask(Task task) {
        task.setSend(false);
        task.setModifiedDate(Util.getDateTime());
        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        for (int i = 0; i < fileAttachmentAdapter.getCount(); i++) {
            attachments.add(fileAttachmentAdapter.getItem(i));
        }
        task.addAttachments(attachments);
        application.saveTask(task);
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
                if (application.isTracking()) {
                    Task parent = task.getParentTask();
                    if (parent != null) {
                        intent = new Intent(this, TaskTrackListActivity.class);
                        intent.putExtra(CustomApplication.GENERIC_INTENT, parent);
                    }
                } else {
                    intent.putExtra(CustomApplication.GENERIC_INTENT, task.getDate());
                }
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
                GpsTask gpsTask = GpsTask.getInstance(getApplicationContext());
                if (!gpsTask.isRunning()) {
                    gpsTask.setManager(getFragmentManager());
                    gpsTask.setListener(new GpsTask.GpsTaskListener() {
                        @Override
                        public void success(double latitude, double longitude) {
                            task.setCheckInDate(Util.getDateTime());
                            task.setModifiedDate(Util.getDateTime());
                            Coord coord = new Coord();
                            coord.setLatitude(latitude);
                            coord.setLongitude(longitude);
                            task.setCheckInCoord(coord);
                            task.setCheckin(true);
                            task.setSend(false);
                            if (application.saveTask(task)) {
                                boolean flag = false;
                                if (application.isTracking()) {
                                    Task parent = task.getParentTask();
                                    if (parent != null) {
                                        Intent intent = new Intent(TaskViewActivity.this, TaskTrackListActivity.class);
                                        intent.putExtra(CustomApplication.GENERIC_INTENT, parent);
                                        startActivity(intent);
                                        flag = true;
                                    }
                                }
                                if (!flag) {
                                    Intent intent = new Intent(TaskViewActivity.this, TaskListActivity.class);
                                    intent.putExtra(CustomApplication.GENERIC_INTENT, task.getDate());
                                    startActivity(intent);
                                }
                                finish();
                                return;
                            }
                        }

                        @Override
                        public void error(String message) {
                            toast(message);
                        }
                    });
                    gpsTask.execute();
                }
            } else {
                toast(getString(R.string.txt_error_checkin));
                return;
            }
        } else {
            toast(getString(R.string.txt_error_task));
        }
    }

    private void checkout() {
        if (task != null) {
            if (!application.isGpsEnabled()) {
                toast(getString(R.string.txt_error_gps));
                return;
            }
            if (checkOutAlert.getConclusion().length() == 0) {
                toast(getString(R.string.txt_error_conclusion));
                return;
            }
            if (task.isCheckin() && !task.isCheckout() && !task.isCancelled()) {
                GpsTask gpsTask = GpsTask.getInstance(getApplicationContext());
                if (!gpsTask.isRunning()) {
                    gpsTask.setManager(getFragmentManager());
                    gpsTask.setListener(new GpsTask.GpsTaskListener() {
                        @Override
                        public void success(double latitude, double longitude) {
                            task.setCheckOutDate(Util.getDateTime());
                            task.setModifiedDate(Util.getDateTime());
                            Coord coord = new Coord();
                            coord.setLatitude(latitude);
                            coord.setLongitude(longitude);
                            task.setCheckOutCoord(coord);
                            task.setCheckout(true);
                            task.setSend(false);
                            task.setConclusion(checkOutAlert.getConclusion());
                            task.setEmails(checkOutAlert.getEmails());
                            if (application.saveTask(task)) {
                                boolean flag = false;
                                if (application.isTracking()) {
                                    Task parent = task.getParentTask();
                                    if (parent != null) {
                                        Intent intent = new Intent(TaskViewActivity.this, TaskTrackListActivity.class);
                                        intent.putExtra(CustomApplication.GENERIC_INTENT, parent);
                                        startActivity(intent);
                                        flag = true;
                                    }
                                }
                                if (!flag) {
                                    Intent intent = new Intent(TaskViewActivity.this, TaskListActivity.class);
                                    intent.putExtra(CustomApplication.GENERIC_INTENT, task.getDate());
                                    startActivity(intent);
                                }
                                finish();
                                return;
                            }
                        }

                        @Override
                        public void error(String message) {
                            toast(message);
                        }
                    });
                    gpsTask.execute();
                }
            } else {
                toast(getString(R.string.txt_error_checkout));
                return;
            }
        } else {
            toast(getString(R.string.txt_error_task));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoFile != null) {
                Attachment attachment = Util.buildAttachment(photoFile);
                photoFile = null;
                if (fileAttachmentAdapter.addAttachment(attachment)) {
                    fileAttachmentAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == REQUEST_CODE_FILE && data != null) {
            File file = (File) data.getSerializableExtra(Config.KEY_FILE_SELECTED);
            if (file != null) {
                Attachment attachment = Util.buildAttachment(file);
                if (fileAttachmentAdapter.addAttachment(attachment)) {
                    fileAttachmentAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<Attachment> attachments = fileAttachmentAdapter.getAttachments();
        if (photoFile != null) {
            Attachment attachment = Util.buildAttachment(photoFile);
            attachments.add(attachment);
        }
        task.addAttachments(attachments);
        outState.putSerializable(TASK_STATE, task);
        super.onSaveInstanceState(outState);
    }
}
