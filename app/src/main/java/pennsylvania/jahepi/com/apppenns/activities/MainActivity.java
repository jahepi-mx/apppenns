package pennsylvania.jahepi.com.apppenns.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.components.CalendarBridge;
import pennsylvania.jahepi.com.apppenns.tasks.ClientSync;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class MainActivity extends AuthActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView viewSms;
    private MessageNotifier messageNotifier;
    private Button logoutBtn, smsBtn, activityBtn, clientSyncBtn, calendarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        smsBtn = (Button) findViewById(R.id.smsBtn);
        activityBtn = (Button) findViewById(R.id.activityBtn);
        clientSyncBtn = (Button) findViewById(R.id.clientSyncBtn);
        calendarBtn = (Button) findViewById(R.id.calendarBtn);

        TextView view = (TextView) findViewById(R.id.welcomeTextView);
        view.setText(application.getUser().getName());

        viewSms = (TextView) findViewById(R.id.newSmsLabel);
        viewSms.setText(String.format(getString(R.string.label_new_sms), 0));

        logoutBtn.setOnClickListener(this);
        smsBtn.setOnClickListener(this);
        activityBtn.setOnClickListener(this);
        clientSyncBtn.setOnClickListener(this);
        calendarBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == logoutBtn) {
            application.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        if (v == smsBtn) {
            startActivity(new Intent(this, MessageListActivity.class));
            finish();
        }
        if (v == activityBtn) {
            startActivity(new Intent(this, TaskListActivity.class));
            finish();
        }
        if (v == clientSyncBtn) {
            ClientSync clientSync = new ClientSync(this);
            clientSync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (v == calendarBtn) {
            CalendarBridge.startCalendar(this, Util.getDate());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageNotifier = new MessageNotifier();
        messageNotifier.setContext(application);
        messageNotifier.setView(viewSms);
        messageNotifier.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (messageNotifier != null) {
            messageNotifier.cancel(true);
            messageNotifier.clear();
            messageNotifier = null;
        }
    }

    private static class MessageNotifier extends AsyncTask {

        private CustomApplication context;
        private TextView view;
        private Handler handler = new Handler();

        public void setContext(CustomApplication context) {
            this.context = context;
        }

        public void setView(TextView view) {
            this.view = view;
        }

        public void clear() {
            view = null;
            context = null;
            handler = null;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int total = context.getNoReadMessagesTotal();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            view.setText(String.format(context.getString(R.string.label_new_sms), total));
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    }
                });

            }
            return null;
        }
    }
}
