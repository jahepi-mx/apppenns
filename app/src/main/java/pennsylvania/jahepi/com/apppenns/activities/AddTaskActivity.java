package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.dialogs.DateDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by javier.hernandez on 04/03/2016.
 */
public class AddTaskActivity extends AuthActivity implements DialogListener {

    private final static String TAG = "AddTaskActivity";
    public final static int REQUEST_CODE = 1;

    private Button dateBtn, clientBtn;
    private DateDialog dialog;
    private Address address;

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

        clientBtn = (Button) findViewById(R.id.clientBtn);
        dateBtn = (Button) findViewById(R.id.taskDateBtn);
        dateBtn.setText(date);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, TAG);
            }
        });

        clientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddTaskActivity.this, ClientActivity.class), REQUEST_CODE);
            }
        });

        Button addButton = (Button) findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText descriptionEditText = (EditText) findViewById(R.id.taskDescEditText);

                String description = descriptionEditText.getText().toString();
                String date = dateBtn.getText().toString();

                if (address == null) {
                    toast(getString(R.string.txt_error_send));
                    return;
                }
                String address = "test";
                Task task = new Task();
                task.setUser(application.getUser());
                task.setClient(address);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (data.hasExtra(CustomApplication.GENERIC_INTENT)) {
                address = (Address) data.getSerializableExtra(CustomApplication.GENERIC_INTENT);
                TextView textView = (TextView) findViewById(R.id.clientAddressTextView);
                textView.setText(address.getClient().getName() + " " + address.getAddress());
            }
        }
    }

    @Override
    public void accept() {
        dateBtn.setText(dialog.getDate());
    }

    @Override
    public void cancel() {

    }
}
