package pennsylvania.jahepi.com.apppenns.activities;

import android.content.Intent;
import android.os.Bundle;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;

/**
 * Created by javier.hernandez on 24/02/2016.
 * Base authentication class which must be inherited from all classes that need session user data.
 */
public class AuthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected();
    }

    protected void isConnected() {
        if (!application.isLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(CustomApplication.GENERIC_INTENT, getString(R.string.txt_session_error));
            startActivity(intent);
        }
    }
}
