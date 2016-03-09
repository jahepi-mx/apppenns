package pennsylvania.jahepi.com.apppenns.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.tasks.ClientSync;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class MainActivity extends AuthActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView viewSms;
    private MessageNotifier messageNotifier;
    private Button logoutBtn, smsBtn, activityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        smsBtn = (Button) findViewById(R.id.smsBtn);
        activityBtn = (Button) findViewById(R.id.activityBtn);

        TextView view = (TextView) findViewById(R.id.welcomeTextView);
        view.setText(application.getUser().getName());

        viewSms = (TextView) findViewById(R.id.newSmsLabel);
        viewSms.setText(String.format(getString(R.string.label_new_sms), 0));

        logoutBtn.setOnClickListener(this);
        smsBtn.setOnClickListener(this);
        activityBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == logoutBtn) {
            application.logout();
            startActivity(new Intent(this, LoginActivity.class));
            //ClientSync clientSync = new ClientSync(this);
            //clientSync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (v == smsBtn) {
            startActivity(new Intent(this, MessageListActivity.class));
        }
        if (v == activityBtn) {
            startActivity(new Intent(this, TaskListActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageNotifier = new MessageNotifier();
        messageNotifier.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        messageNotifier.cancel(true);
    }

    private class MessageNotifier extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int total = application.getNoReadMessagesTotal();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewSms.setText(String.format(getString(R.string.label_new_sms), total));
                    }
                });
            }
            return null;
        }
    }
}
