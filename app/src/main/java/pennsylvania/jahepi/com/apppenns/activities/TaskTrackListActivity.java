package pennsylvania.jahepi.com.apppenns.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import pennsylvania.jahepi.com.apppenns.dialogs.TaskOptionsDialog;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by javier.hernandez on 01/06/2016.
 */
public class TaskTrackListActivity extends AuthActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, CustomApplication.ApplicationNotifierListener, DialogInterface.OnClickListener, TaskOptionsDialog.TaskOptionDialogListener {

    private final static String TAG = "TaskTrackListActivity";
    private final static String TASK_STATE = "task_state";

    private TaskAdapter adapter;
    private AlertDialog.Builder cancelledDialog;
    private TaskOptionsDialog optionsDialog;
    private Task selectedTask;
    private Task parentTask;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_track_list);

        Intent intent = getIntent();
        ArrayList<Task> tasks = new ArrayList<Task>();
        parentTask = (Task) intent.getSerializableExtra(CustomApplication.GENERIC_INTENT);

        if (savedInstanceState != null) {
            parentTask = (Task) savedInstanceState.get(TASK_STATE);
        }

        if (parentTask != null) {
            tasks = application.getChildTasks(parentTask);
        }

        cancelledDialog = new AlertDialog.Builder(this);
        cancelledDialog.setTitle(R.string.txt_cancelled_title);
        cancelledDialog.setMessage(R.string.txt_cancelled_message);
        cancelledDialog.setPositiveButton(R.string.btn_yes, this);
        cancelledDialog.setNegativeButton(R.string.btn_no, this);

        optionsDialog = new TaskOptionsDialog();
        optionsDialog.setListener(this);

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskTrackListActivity.this, MainActivity.class);
                startActivity(intent);
                TaskTrackListActivity.this.finish();
            }
        });

        adapter = new TaskAdapter(getApplicationContext(), R.layout.task_item);
        adapter.addAll(tasks);

        ListView listView = (ListView) findViewById(R.id.taskListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        onChangeLocation(application.getLatitude(), application.getLongitude());

        backBtn = (Button) findViewById(R.id.backBtn);
        changeBacklabel();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task parent = parentTask.getParentTask();
                if (parent != null) {
                    parentTask = parent;
                    selectedTask = null;
                    ArrayList<Task> tasks = application.getChildTasks(parentTask);
                    adapter.clear();
                    adapter.addAll(tasks);
                    adapter.notifyDataSetChanged();
                    changeBacklabel();
                } else {
                    Intent intent = new Intent(TaskTrackListActivity.this, TaskListActivity.class);
                    intent.putExtra(CustomApplication.GENERIC_INTENT, parentTask != null ? parentTask.getDate() : Util.getDate());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void changeBacklabel() {
        if (parentTask != null) {
            String backText = parentTask.getParentTask() != null ? getString(R.string.btn_levelup) : getString(R.string.btn_root);
            backBtn.setText(backText);
        }
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
            if (parentTask != null) {
                adapter.addAll(application.getChildTasks(parentTask));
            }
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
    }

    @Override
    public void onTrackingTask() {
        parentTask = selectedTask;
        selectedTask = null;
        ArrayList<Task> tasks = application.getChildTasks(parentTask);
        adapter.clear();
        adapter.addAll(tasks);
        adapter.notifyDataSetChanged();
        changeBacklabel();
    }

    @Override
    public void onCancelTask() {
        cancelledDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(TASK_STATE, parentTask);
        super.onSaveInstanceState(outState);
    }
}
