package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import pennsylvania.jahepi.com.apppenns.R;

/**
 * Created by jahepi on 09/03/16.
 */
public class ClientSync extends AsyncTask<Void, Integer, Void> implements View.OnClickListener {

    private static final String TAG = "ClientSync";
    private ProgressDialog dialog;
    private Activity activity;

    public ClientSync(Activity activity) {
        dialog = new ProgressDialog();
        dialog.setListener(this);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog.show(activity.getFragmentManager(), TAG);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        dialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        cancel(true);
        dialog.dismiss();
        activity = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        int i = 0;
        while (i < 100) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            publishProgress(i);
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        cancel(true);
        dialog.dismiss();
        activity = null;
    }

    public static class ProgressDialog extends DialogFragment {

        private ProgressBar progressBar;
        private Button cancelBtn;
        private View.OnClickListener listener;

        public ProgressDialog() {
            super();
        }

        public void setProgress(final int progress) {
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
            cancelBtn.setOnClickListener(listener);
            return view;
        }
    }
}
