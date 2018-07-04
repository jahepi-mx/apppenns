package pennsylvania.jahepi.com.apppenns.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.tasks.UserSync;

/**
 * Created by javier.hernandez on 24/02/2016.
 * Class activity to log in the user.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText userInput, passInput;
    private Button loginBtn;
    private UserSync userSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (application.isLogged()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.login);

        Intent intent = getIntent();
        String res = intent.getStringExtra(CustomApplication.GENERIC_INTENT);
        if (!Util.isEmpty(res)) {
            toast(res);
        }

        userInput = (EditText) findViewById(R.id.userInput);
        passInput = (EditText) findViewById(R.id.passwordInput);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        TextView versionTextView = (TextView) findViewById(R.id.versionTxt);
        versionTextView.setText(String.format(getString(R.string.txt_version), CustomApplication.VERSION));

        userInput.setText(application.getStoredUserEmail());

        loginBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userSync == null) {
            userSync = new UserSync(application);
            userSync.execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userSync != null) {
            userSync.cancel(true);
            userSync = null;
        }
    }

    @Override
    public void onClick(View v) {
        String email = userInput.getText().toString();
        String password = passInput.getText().toString();

        if (application.login(email, password)) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            toast(getString(R.string.txt_login_error));
        }
    }
}
