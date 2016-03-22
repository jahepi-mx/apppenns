package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.dialogs.ProgressDialog;
import pennsylvania.jahepi.com.apppenns.entities.Message;

/**
 * Created by jahepi on 22/03/16.
 */
public class AttachmentTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "AttachmentTask";

    private ProgressDialog dialog;
    private Context context;
    private Message.File file;
    private boolean downloadFlag;
    private AttachmentTaskListener listener;

    public AttachmentTask(Context context) {
        this.context = context;
        dialog = new ProgressDialog();

        dialog.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadFlag) {
                    File downloadFile = new File(CustomApplication.SDCARD_PATH, file.getName());
                    downloadFile.delete();
                    dialog.dismiss();
                }
                cancel(true);
                AttachmentTask.this.context = null;
                listener = null;
            }
        });
    }

    public void setFile(Message.File file) {
        this.file = file;
    }

    public Message.File getFile() {
        return file;
    }

    public void setListener(AttachmentTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        File fileRef = new File(file.getPathNoName(), file.getName());
        downloadFlag = !fileRef.exists();
        if (downloadFlag) {
            if (!dialog.isAdded()) {
                dialog.show(((Activity) context).getFragmentManager(), TAG);
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        File downloadFile = new File(file.getPathNoName(), file.getName());
        listener.onFinish(downloadFile.exists() && (downloadFile.length() / 1024) > 1, downloadFlag, file);
        if (downloadFlag) {
            dialog.dismiss();
        }
        cancel(true);
        context = null;
        listener = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (downloadFlag) {
            File downloadFile = new File(CustomApplication.SDCARD_PATH, file.getName());
            try {
                downloadFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(downloadFile);
                URL url = new URL(CustomApplication.SERVICE_URL + "files/" + file.getName());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();

                int size = connection.getContentLength();
                int downloaded = 0;

                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(buffer)) > 0) {

                    fOut.write(buffer, 0, length);
                    downloaded += length;

                    final float percentage = (float) downloaded / (float) size * 100;
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dialog.setStatus(String.format(context.getString(R.string.txt_sync_status), (int) percentage + "%"));
                                dialog.setTitle(String.format(context.getString(R.string.txt_sync_status), (int) percentage + "%"));
                                dialog.setProgress((int) percentage);
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        }
                    });

                }
                fOut.close();
                file.setModifiedDate(Util.getDateTime());
                file.setPath(CustomApplication.SDCARD_PATH + File.separator + file.getName());
            } catch (IOException e) {
                e.printStackTrace();
                downloadFile.delete();
            }
        }
        return null;
    }

    public static interface AttachmentTaskListener {
        public void onFinish(boolean success, boolean downloaded, Message.File file);
    }
}
