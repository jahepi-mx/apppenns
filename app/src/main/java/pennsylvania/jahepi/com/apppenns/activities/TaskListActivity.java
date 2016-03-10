package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.TaskAdapter;
import pennsylvania.jahepi.com.apppenns.dialogs.DateDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by jahepi on 05/03/16.
 */
public class TaskListActivity extends AuthActivity implements DialogListener, AdapterView.OnItemClickListener, CustomApplication.ApplicationNotifierListener {

    private final static String TAG = "TaskListActivity";

    private DateDialog dialog;
    private Button dateBtn;
    private TaskAdapter adapter;
    private ArrayList<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);

        Intent intent = getIntent();
        String date = intent.getStringExtra(CustomApplication.GENERIC_INTENT);
        if (date == null) {
            date = Util.getDate();
        }

        dialog = new DateDialog();
        dialog.setDate(date);
        dialog.setListener(this);

        tasks = application.getTasks(date);

        Button addTask = (Button) findViewById(R.id.newTaskBtn);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
                intent.putExtra(CustomApplication.GENERIC_INTENT, dateBtn.getText());
                startActivity(intent);
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        dateBtn = (Button) findViewById(R.id.dateBtn);
        dateBtn.setText(date);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, TAG);
            }
        });

        adapter = new TaskAdapter(this, R.layout.task_item);
        adapter.addAll(tasks);

        ListView listView = (ListView) findViewById(R.id.taskListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        onOnChangeLocation(application.getLatitude(), application.getLongitude());
    }

    @Override
    protected void onStart() {
        super.onStart();
        application.addMessageNotifierListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        application.removeMessageNotifierListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = (Task) adapter.getItem(position);
        Log.d(TAG, task.getClient().getName());
        Intent intent = new Intent(this, TaskViewActivity.class);
        intent.putExtra(CustomApplication.GENERIC_INTENT, task);
        startActivity(intent);
    }

    @Override
    public void accept() {
        String date = dialog.getDate();
        dateBtn.setText(date);
        adapter.clear();
        adapter.addAll(application.getTasks(date));
    }

    @Override
    public void cancel() {

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
                Iterator<Task> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
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
    public void onOnChangeLocation(final double latitude, final double longitude) {
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
}
