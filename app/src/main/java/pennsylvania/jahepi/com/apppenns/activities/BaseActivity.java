package pennsylvania.jahepi.com.apppenns.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class BaseActivity extends AppCompatActivity {

    protected CustomApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (CustomApplication) getApplication();
    }

    protected void toast(CharSequence charSequence) {
        Toast toast = Toast.makeText(this.getApplicationContext(), charSequence, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.custom_toast);
        toast.show();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        View mainContainer = findViewById(R.id.main_container);
        if (mainContainer != null) {
            setupUI(mainContainer);
        }
    }

    protected void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(BaseActivity.this);
                    return false;
                }

            });
        }
        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public CustomApplication getCustomApplication() {
        return this.application;
    }
}
