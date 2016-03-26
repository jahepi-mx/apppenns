package pennsylvania.jahepi.com.apppenns.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText userInput, passInput;
    private Button loginBtn;

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

        userInput.setText(application.getStoredUserEmail());

        loginBtn.setOnClickListener(this);
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
