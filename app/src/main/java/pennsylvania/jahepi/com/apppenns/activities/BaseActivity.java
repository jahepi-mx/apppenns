package pennsylvania.jahepi.com.apppenns.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class BaseActivity extends Activity {

    private Menu menu;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        MenuItem item = this.menu.findItem(R.id.item1);
        if (application.isSyncActive()) {
            item.setIcon(R.drawable.active);
            item.setTitle(this.getString(R.string.txt_sync));
        } else {
            item.setIcon(R.drawable.inactive);
            item.setTitle(this.getString(R.string.txt_sync));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.item1) {
            if (application.isSyncActive() == false) {
                // startService(new Intent(this, Sync.class));
            }
        }
        return true;
    }
}
