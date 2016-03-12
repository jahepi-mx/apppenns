package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.dialogs.DateDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Task;
import pennsylvania.jahepi.com.apppenns.entities.Type;

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

        ArrayList<Type> types = application.getTypes();
        final Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        ArrayAdapter<Type> adapter = new ArrayAdapter<Type>(this, R.layout.support_simple_spinner_dropdown_item, types);
        typeSpinner.setAdapter(adapter);

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
                if (!dialog.isAdded()) {
                    FragmentManager fm = getFragmentManager();
                    dialog.show(fm, TAG);
                }
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
                task.setType(type);
                task.setStartTime("");
                task.setEndTime("");
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
            if (data != null && data.hasExtra(CustomApplication.GENERIC_INTENT)) {
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
