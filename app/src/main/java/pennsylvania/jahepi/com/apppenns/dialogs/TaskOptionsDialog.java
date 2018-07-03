package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import pennsylvania.jahepi.com.apppenns.R;

/**
 * Created by javier.hernandez on 01/06/2016.
 * Task Option dialog for tracking and canceling tasks.
 */
public class TaskOptionsDialog extends DialogFragment {

    private TaskOptionDialogListener listener;

    public TaskOptionsDialog() {
        super();
        setCancelable(false);
    }

    public void setListener(TaskOptionDialogListener listener) {
        this.listener = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_options_dialog, container);
        Button exitBtn = (Button) view.findViewById(R.id.exitBtn);
        Button trackingBtn = (Button) view.findViewById(R.id.trackingTaskBtn);
        Button cancelBtn = (Button) view.findViewById(R.id.cancelTaskBtn);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        trackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onTrackingTask();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onCancelTask();
            }
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public interface TaskOptionDialogListener {
        public void onTrackingTask();
        public void onCancelTask();
    }
}
