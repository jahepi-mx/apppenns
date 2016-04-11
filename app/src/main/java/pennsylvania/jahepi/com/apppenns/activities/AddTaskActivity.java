package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;
import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.components.GoogleMapFragment;
import pennsylvania.jahepi.com.apppenns.components.TypeSpinner;
import pennsylvania.jahepi.com.apppenns.components.filechooser.Config;
import pennsylvania.jahepi.com.apppenns.components.filechooser.activities.FileChooserActivity;
import pennsylvania.jahepi.com.apppenns.dialogs.DateDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.dialogs.TimeDialog;
import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.entities.Type;

/**
 * Created by javier.hernandez on 04/03/2016.
 */
public class AddTaskActivity extends AuthActivity implements DialogListener {

    private final static String TAG = "AddTaskActivity";
    public final static int REQUEST_CODE = 1;
    public final static int REQUEST_CODE_FILE = 2;
    private final static int REQUEST_IMAGE_CAPTURE = 3;

    private final static String TASK_STATE = "task_state";

    private static enum TIME_TYPE {START, END};

    private Button dateBtn, clientBtn, startTimeBtn, endTimeBtn;
    private EditText descriptionEditText;
    private TypeSpinner typeSpinner;
    private DateDialog dateDialog;
    private TimeDialog timeDialog;
    private Address address;
    private TIME_TYPE type;
    private FileAttachmentAdapter fileAttachmentAdapter;
    private ListView attachmentList;
    private Task parentTask;
    private GoogleMapFragment mapFragment;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_add);

        mapFragment = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        descriptionEditText = (EditText) findViewById(R.id.taskDescEditText);

        fileAttachmentAdapter = new FileAttachmentAdapter(this, R.layout.file_item);
        attachmentList = (ListView) findViewById(R.id.attachmentsListView);
        attachmentList.setAdapter(fileAttachmentAdapter);

        ArrayList<Type> types = application.getTypes();
        typeSpinner = (TypeSpinner) findViewById(R.id.typeSpinner);
        ArrayAdapter<Type> adapter = new ArrayAdapter<Type>(this, R.layout.type_item, types);
        typeSpinner.setAdapter(adapter);

        Intent intent = getIntent();
        String date = intent.getStringExtra(CustomApplication.GENERIC_INTENT);
        parentTask = (Task) intent.getSerializableExtra(CustomApplication.ADDITIONAL_GENERIC_INTENT);
        if (date == null) {
            date = Util.getDate();
        }

        String time = Util.getTime();

        dateDialog = new DateDialog();
        dateDialog.setDate(date);
        dateDialog.setListener(this);

        timeDialog = new TimeDialog();
        timeDialog.setTime(time);
        timeDialog.setListener(this);

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

        Button filesBtn = (Button) findViewById(R.id.filesBtn);
        filesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddTaskActivity.this, FileChooserActivity.class), REQUEST_CODE_FILE);
            }
        });

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentTask == null) {
                    Intent intent = new Intent(AddTaskActivity.this, TaskListActivity.class);
                    intent.putExtra(CustomApplication.GENERIC_INTENT, dateBtn.getText());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AddTaskActivity.this, TaskViewActivity.class);
                    intent.putExtra(CustomApplication.GENERIC_INTENT, parentTask);
                    startActivity(intent);
                }
                AddTaskActivity.this.finish();
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
                startActivity(intent);
                AddTaskActivity.this.finish();
            }
        });

        clientBtn = (Button) findViewById(R.id.clientBtn);
        dateBtn = (Button) findViewById(R.id.taskDateBtn);
        startTimeBtn = (Button) findViewById(R.id.startTimeBtn);
        endTimeBtn = (Button) findViewById(R.id.endTimeBtn);
        dateBtn.setText(date);
        startTimeBtn.setText(time);
        endTimeBtn.setText(time);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateDialog.isAdded()) {
                    FragmentManager fm = getFragmentManager();
                    dateDialog.show(fm, TAG);
                    dateDialog.setDate(dateBtn.getText().toString());
                }
            }
        });

        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timeDialog.isAdded()) {
                    type = TIME_TYPE.START;
                    FragmentManager fm = getFragmentManager();
                    timeDialog.show(fm, TAG);
                    timeDialog.setTime(startTimeBtn.getText().toString());
                }
            }
        });

        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timeDialog.isAdded()) {
                    type = TIME_TYPE.END;
                    FragmentManager fm = getFragmentManager();
                    timeDialog.show(fm, TAG);
                    timeDialog.setTime(endTimeBtn.getText().toString());
                }
            }
        });

        clientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddTaskActivity.this, ClientActivity.class), REQUEST_CODE);
            }
        });

        if (parentTask != null) {
            setTaskDefaultState(parentTask);
        } else {
            mapFragment.addLocation(getString(R.string.txt_map_me), BitmapDescriptorFactory.HUE_RED, application.getLatitude(), application.getLongitude());
            mapFragment.center(application.getLatitude(), application.getLongitude());
        }

        Button addButton = (Button) findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = descriptionEditText.getText().toString();
                String date = dateBtn.getText().toString();
                String startTime = startTimeBtn.getText().toString();
                String endTime = endTimeBtn.getText().toString();
                Type type = (Type) typeSpinner.getSelectedItem();

                if (address == null || type == null) {
                    toast(getString(R.string.txt_error_send));
                    return;
                }

                Task task = new Task();
                task.setUser(application.getUser());
                task.setAddress(address);
                task.setDescription(description);
                task.setDate(date);
                task.setModifiedDate(Util.getDateTime());
                task.setConclusion("");
                task.setEmails("");
                task.setType(type);
                task.setStartTime(startTime);
                task.setEndTime(endTime);
                task.setParentTask(parentTask);
                // Add event to calendar provider
                long calendarEventId  = application.addEvent(task.getStartDateTime(), task.getEndDateTime(), task.getClient().getName(), task.getDescription());
                task.setEventId((int) calendarEventId);

                ArrayList<Attachment> attachments = new ArrayList<Attachment>();
                for (int i = 0; i < fileAttachmentAdapter.getCount(); i++) {
                    attachments.add(fileAttachmentAdapter.getItem(i));
                }
                task.addAttachments(attachments);

                if (application.saveTask(task)) {
                    Intent intent = new Intent(AddTaskActivity.this, TaskListActivity.class);
                    intent.putExtra(CustomApplication.GENERIC_INTENT, dateBtn.getText());
                    startActivity(intent);
                    finish();
                } else {
                    toast(getString(R.string.txt_error_database));
                }
            }
        });

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
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
        } else if (requestCode == REQUEST_CODE) {
            if (data != null && data.hasExtra(CustomApplication.GENERIC_INTENT)) {
                address = (Address) data.getSerializableExtra(CustomApplication.GENERIC_INTENT);
                TextView textView = (TextView) findViewById(R.id.clientAddressTextView);
                textView.setText(address.getClient().getName() + " " + address.getAddress());
                setMapLocation(address);
            }
        } else if (requestCode == REQUEST_CODE_FILE) {
            if (data != null) {
                File file = (File) data.getSerializableExtra(Config.KEY_FILE_SELECTED);
                if (file != null) {
                    Attachment attachment = Util.buildAttachment(file);
                    if (fileAttachmentAdapter.addAttachment(attachment)) {
                        fileAttachmentAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void setTaskDefaultState(Task task) {
        descriptionEditText.setText(task.getDescription());
        dateBtn.setText(task.getDate());
        startTimeBtn.setText(task.getStartTime());
        endTimeBtn.setText(task.getEndTime());
        typeSpinner.setSelectedItem(task.getType());
        address = task.getAddress();
        fileAttachmentAdapter.addAll(task.getAttachments());
        if (address != null) {
            TextView textView = (TextView) findViewById(R.id.clientAddressTextView);
            textView.setText(address.getClient().getName() + " " + address.getAddress());
            setMapLocation(address);
        }
    }

    private void setMapLocation(Address address) {
        mapFragment.clear();
        mapFragment.addLocation(getString(R.string.txt_map_client), BitmapDescriptorFactory.HUE_AZURE, address.getCoord().getLatitude(), address.getCoord().getLongitude());
        mapFragment.center(address.getCoord().getLatitude(), address.getCoord().getLongitude());
    }

    @Override
    public void accept(Object dialogParam) {
        if (dialogParam == dateDialog) {
            dateBtn.setText(dateDialog.getDate());
        }
        if (dialogParam == timeDialog) {
            if (type == TIME_TYPE.START) {
                startTimeBtn.setText(timeDialog.getTime());
            }
            if (type == TIME_TYPE.END) {
                endTimeBtn.setText(timeDialog.getTime());
            }
        }
    }

    @Override
    public void cancel(Object dialogParam) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Task task = new Task();
        task.setDescription(descriptionEditText.getText().toString());
        task.setDate(dateBtn.getText().toString());
        task.setStartTime(startTimeBtn.getText().toString());
        task.setEndTime(endTimeBtn.getText().toString());
        task.setType((Type) typeSpinner.getSelectedItem());
        task.setAddress(address);
        ArrayList<Attachment> attachments = fileAttachmentAdapter.getAttachments();
        if (photoFile != null) {
            Attachment attachment = Util.buildAttachment(photoFile);
            attachments.add(attachment);
        }
        task.addAttachments(attachments);
        outState.putSerializable(TASK_STATE, task);
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        setTaskDefaultState((Task) savedInstanceState.get(TASK_STATE));
    }
}
