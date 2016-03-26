package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;

/**
 * Created by jahepi on 22/03/16.
 */
public class ProgressDialog extends DialogFragment {

    private ProgressBar progressBar;
    private Button cancelBtn;
    private TextView statusTextView;
    private View.OnClickListener listener;

    public ProgressDialog() {
        super();
        setCancelable(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.txt_sync_start));
    }

    public void setTitle(String title ) {
        getDialog().setTitle(title);
    }

    public void setStatus(String status) {
        statusTextView.setText(status);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_dialog, container);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        statusTextView = (TextView) view.findViewById(R.id.statusTextView);
        cancelBtn.setOnClickListener(listener);
        return view;
    }
}

