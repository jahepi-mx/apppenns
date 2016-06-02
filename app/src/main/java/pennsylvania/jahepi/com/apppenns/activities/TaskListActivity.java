package pennsylvania.jahepi.com.apppenns.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.TaskAdapter;
import pennsylvania.jahepi.com.apppenns.components.CalendarBridge;
import pennsylvania.jahepi.com.apppenns.dialogs.DateDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.dialogs.TaskOptionsDialog;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by jahepi on 05/03/16.
 */
public class TaskListActivity extends AuthActivity implements DialogListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, CustomApplication.ApplicationNotifierListener, DialogInterface.OnClickListener, TaskOptionsDialog.TaskOptionDialogListener {

    private final static String TAG = "TaskListActivity";

    private DateDialog dateDialog;
    private Button dateBtn;
    private TaskAdapter adapter;
    private AlertDialog.Builder cancelledDialog;
    private TaskOptionsDialog optionsDialog;
    private Task selectedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);

        application.setIsTracking(false);

        Intent intent = getIntent();
        String date = intent.getStringExtra(CustomApplication.GENERIC_INTENT);
        if (date == null) {
            date = Util.getDate();
        }

        dateDialog = new DateDialog();
        dateDialog.setDate(date);
        dateDialog.setListener(this);

        cancelledDialog = new AlertDialog.Builder(this);
        cancelledDialog.setTitle(R.string.txt_cancelled_title);
        cancelledDialog.setMessage(R.string.txt_cancelled_message);
        cancelledDialog.setPositiveButton(R.string.btn_yes, this);
        cancelledDialog.setNegativeButton(R.string.btn_no, this);

        optionsDialog = new TaskOptionsDialog();
        optionsDialog.setListener(this);

        ArrayList<Task> tasks = application.getTasks(date);

        Button addTask = (Button) findViewById(R.id.newTaskBtn);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
                intent.putExtra(CustomApplication.GENERIC_INTENT, dateBtn.getText());
                startActivity(intent);
                TaskListActivity.this.finish();
            }
        });

        Button calendarBtn = (Button) findViewById(R.id.calendarBtn);
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarBridge.startCalendar(TaskListActivity.this, dateBtn.getText().toString());
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, MainActivity.class);
                startActivity(intent);
                TaskListActivity.this.finish();
            }
        });

        dateBtn = (Button) findViewById(R.id.dateBtn);
        dateBtn.setText(date);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateDialog.isAdded()) {
                    FragmentManager fm = getFragmentManager();
                    dateDialog.show(fm, TAG);
                }
            }
        });

        adapter = new TaskAdapter(getApplicationContext(), R.layout.task_item);
        adapter.addAll(tasks);

        ListView listView = (ListView) findViewById(R.id.taskListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        onChangeLocation(application.getLatitude(), application.getLongitude());
    }

    @Override
    protected void onStart() {
        super.onStart();
        application.addMessageNotifierListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            adapter.clear();
            adapter.addAll(application.getTasks(dateBtn.getText().toString()));
            onChangeLocation(application.getLatitude(), application.getLongitude());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        application.removeMessageNotifierListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = (Task) adapter.getItem(position);
        Intent intent = new Intent(this, TaskViewActivity.class);
        intent.putExtra(CustomApplication.GENERIC_INTENT, task);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        selectedTask = (Task) adapter.getItem(position);
        if (!optionsDialog.isAdded()) {
            FragmentManager fm = getFragmentManager();
            optionsDialog.show(fm, TAG);
        }
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (selectedTask != null) {
                selectedTask.setSend(false);
                selectedTask.setCancelled(true);
                selectedTask.setModifiedDate(Util.getDateTime());
                application.saveTask(selectedTask);
                application.removeEvent(selectedTask.getEventId());
                selectedTask = null;
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void accept(Object dialogParam) {
        String date = dateDialog.getDate();
        dateBtn.setText(date);
        adapter.clear();
        adapter.addAll(application.getTasks(date));
        onChangeLocation(application.getLatitude(), application.getLongitude());
    }

    @Override
    public void cancel(Object dialogParam) {

    }

    @Override
    public void onNewMessages(ArrayList<Message> messages) {

    }

    @Override
    public void onMessagesSend(ArrayList<Message> messages) {

    }

    @Override
    public void onMessagesRead(ArrayList<Message> messages) {

    }

    @Override
    public void onTasksSend(final ArrayList<Task> tasks) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Task task : tasks) {
                    Task adapterTask = adapter.getTask(task);
                    if (adapterTask != null) {
                        adapterTask.setSend(true);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onChangeLocation(final double latitude, final double longitude) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (latitude != 0 && longitude != 0) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        Task task = (Task) adapter.getItem(i);
                        task.setDistance(latitude, longitude);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onNewTasks(final ArrayList<Task> tasks) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Task task : tasks) {
                    Task adapterTask = adapter.getTask(task);
                    if (adapterTask == null) {
                        String selectedDate = dateBtn.getText().toString();
                        if (task.getDate().equals(selectedDate)) {
                            adapter.insert(task, 0);
                        }
                    } else {
                        adapterTask.copy(task);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onTrackingTask() {
        Intent intent = new Intent(this, TaskTrackListActivity.class);
        intent.putExtra(CustomApplication.GENERIC_INTENT, selectedTask);
        startActivity(intent);
    }

    @Override
    public void onCancelTask() {
        cancelledDialog.show();
    }
}
