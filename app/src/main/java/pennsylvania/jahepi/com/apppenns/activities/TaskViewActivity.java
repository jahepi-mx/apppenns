package pennsylvania.jahepi.com.apppenns.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.entities.Coord;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by jahepi on 06/03/16.
 */
public class TaskViewActivity extends AuthActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final String TAG = "TaskViewActivity";
    private static final int ALPHA = 50;
    private static enum TYPE {CHECKIN, CHECKOUT};
    private TYPE type;

    private Button checkinBtn, checkoutBtn, backBtn;
    private AlertDialog.Builder checkAlert;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_view);

        Intent intent = getIntent();
        task = (Task) intent.getSerializableExtra(CustomApplication.GENERIC_INTENT);

        checkinBtn = (Button) findViewById(R.id.checkinBtn);
        checkoutBtn = (Button) findViewById(R.id.checkoutBtn);
        backBtn = (Button) findViewById(R.id.backBtn);

        if (task != null) {

            TextView dateTextView = (TextView) findViewById(R.id.taskDateValue);
            TextView clientTextView = (TextView) findViewById(R.id.taskClientValue);
            TextView descTextView = (TextView) findViewById(R.id.taskDescValue);

            clientTextView.setText(task.getClient());
            descTextView.setText(task.getDescription());
            dateTextView.setText(task.getDate());

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

        checkAlert = new AlertDialog.Builder(this);
        checkAlert.setPositiveButton(R.string.btn_yes, this);
        checkAlert.setNegativeButton(R.string.btn_no, this);
        checkAlert.setTitle(getString(R.string.txt_confirm));
        checkAlert.setIcon(R.drawable.task);

        backBtn.setOnClickListener(this);
        checkinBtn.setOnClickListener(this);
        checkoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (type == TYPE.CHECKIN) {
                checkin();
            }
            if (type == TYPE.CHECKOUT) {
                checkout();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == checkinBtn) {
            type = TYPE.CHECKIN;
            checkAlert.setMessage(getString(R.string.txt_confirm_checkin));
            checkAlert.show();
        }
        if (v == checkoutBtn) {
            type = TYPE.CHECKOUT;
            checkAlert.setMessage(getString(R.string.txt_confirm_checkout));
            checkAlert.show();
        }
        if (v == backBtn) {
            Intent intent = new Intent(this, TaskListActivity.class);
            startActivity(intent);
        }
    }

    private void checkin() {
        if (task != null) {
            if (!task.isCheckin() && !task.isCheckout()) {
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
                    startActivity(intent);
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
            if (task.isCheckin() && !task.isCheckout()) {
                task.setCheckOutDate(Util.getDateTime());
                task.setModifiedDate(Util.getDateTime());
                Coord coord = new Coord();
                coord.setLatitude(application.getLatitude());
                coord.setLongitude(application.getLongitude());
                task.setCheckOutCoord(coord);
                task.setCheckout(true);
                task.setSend(false);
                if (application.saveTask(task)) {
                    Intent intent = new Intent(this, TaskListActivity.class);
                    startActivity(intent);
                    return;
                }
            } else {
                toast(getString(R.string.txt_error_checkout));
                return;
            }
        }
        toast(getString(R.string.txt_error_task));
    }
}
