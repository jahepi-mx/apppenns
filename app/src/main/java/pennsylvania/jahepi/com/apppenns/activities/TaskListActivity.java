package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.dialogs.DateDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;

/**
 * Created by jahepi on 05/03/16.
 */
public class TaskListActivity extends AuthActivity implements DialogListener {

    private final static String TAG = "TaskListActivity";

    private DateDialog dialog;
    private Button dateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);

        dialog = new DateDialog();
        dialog.setListener(this);

        Button addTask = (Button) findViewById(R.id.newTaskBtn);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
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

        String date = Util.getDate();
        dateBtn = (Button) findViewById(R.id.dateBtn);
        dateBtn.setText(date);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, TAG);
            }
        });
    }

    @Override
    public void accept() {
        dateBtn.setText(dialog.getDate());
    }

    @Override
    public void cancel() {

    }
}
