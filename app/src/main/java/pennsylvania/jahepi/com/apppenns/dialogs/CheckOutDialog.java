package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import pennsylvania.jahepi.com.apppenns.R;

/**
 * Created by javier.hernandez on 08/03/2016.
 */
public class CheckOutDialog extends AlertDialog implements DialogInterface.OnClickListener {

    private EditText editText;
    private DialogListener listener;

    public CheckOutDialog(Context context, DialogListener listener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) inflater.inflate(R.layout.checkout_dialog, null);
        editText = (EditText) view.findViewById(R.id.taskConclusionEditText);
        setView(view);
        setTitle(R.string.txt_confirm);
        setIcon(R.drawable.task);
        setMessage(context.getString(R.string.txt_confirm_checkout));
        setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.btn_no), this);
        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.btn_yes), this);
        this.listener = listener;
    }

    public String getConclusion() {
        return editText.getText().toString();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();
        if (which == DialogInterface.BUTTON_POSITIVE) {
            listener.accept(this);
        }
    }
}
