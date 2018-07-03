package pennsylvania.jahepi.com.apppenns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pennsylvania.jahepi.com.apppenns.activities.LoginActivity;

/**
 * Created by javier.hernandez on 18/04/2016.
 * Class that executes the Login Activity when the device boots up
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent newIntent = new Intent(context, LoginActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
    }
}
