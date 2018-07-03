package pennsylvania.jahepi.com.apppenns.adapters;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

/**
 * Created by javier.hernandez on 01/04/2016.
 * Custom alert dialog component.
 */
public class CustomAlertDialog extends AlertDialog.Builder implements DialogInterface.OnDismissListener {

    private boolean added;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public CustomAlertDialog(Context context) {
        super(context);
        setOnDismissListener(this);
    }

    @Override
    public AlertDialog show() {
        if (!added) {
            added = true;
            return super.show();
        }
        return null;
    }

    public boolean isAdded() {
        return added;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        added = false;
    }
}
