package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.dialogs.DateDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by javier.hernandez on 04/03/2016.
 */
public class AddTaskActivity extends AuthActivity implements DialogListener {

    private final static String TAG = "AddTaskActivity";

    private Button dateBtn;
    private DateDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_add);

        Intent intent = getIntent();
        String date = intent.getStringExtra(CustomApplication.GENERIC_INTENT);
        if (date == null) {
            date = Util.getDate();
        }

        dialog = new DateDialog();
        dialog.setDate(date);
        dialog.setListener(this);

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this, TaskListActivity.class);
                intent.putExtra(CustomApplication.GENERIC_INTENT, dateBtn.getText());
                startActivity(intent);
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        dateBtn = (Button) findViewById(R.id.taskDateBtn);
        dateBtn.setText(date);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, TAG);
            }
        });

        Button addButton = (Button) findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText clientEditText = (EditText) findViewById(R.id.clientEditText);
                EditText descriptionEditText = (EditText) findViewById(R.id.taskDescEditText);

                String client = clientEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String date = dateBtn.getText().toString();

                if (Util.isEmpty(client) || Util.isEmpty(description)) {
                    toast(getString(R.string.txt_error_send));
                    return;
                }

                Task task = new Task();
                task.setUser(application.getUser());
                task.setClient(client);
                task.setDescription(description);
                task.setDate(date);
                task.setModifiedDate(Util.getDateTime());
                task.setConclusion("");
                if (application.saveTask(task)) {
                    Intent intent = new Intent(AddTaskActivity.this, TaskListActivity.class);
                    intent.putExtra(CustomApplication.GENERIC_INTENT, dateBtn.getText());
                    startActivity(intent);
                } else {
                    toast(getString(R.string.txt_error_database));
                }
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
