package pennsylvania.jahepi.com.apppenns.tasks;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.dialogs.ProgressDialog;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;

/**
 * Created by jahepi on 22/03/16.
 * Asynchronous task for downloading attachments from tasks
 */
public class AttachmentTask extends AsyncTask<Void, AttachmentTask.DownloadInfo, Void> {

    private static final String TAG = "AttachmentTask";
    private static AttachmentTask self;

    private ProgressDialog dialog;
    private Context context;
    private Attachment.File file;
    private boolean downloadFlag;
    private AttachmentTaskListener listener;
    private DownloadInfo downloadInfo;
    private FragmentManager manager;

    private AttachmentTask(Context context) {
        this.context = context;
        downloadInfo = new DownloadInfo();
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
                manager = null;
            }
        });
    }

    public static AttachmentTask getInstance(Context context) {
        if (self != null && !self.isCancelled() && (self.getStatus() == Status.RUNNING || self.getStatus() == Status.PENDING)) {
            return self;
        } else {
            self = new AttachmentTask(context);
        }
        return self;
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }

    public boolean isRunning() {
        return self.getStatus() == Status.RUNNING && !isCancelled();
    }

    public void setFile(Attachment.File file) {
        this.file = file;
    }

    public Attachment.File getFile() {
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
            dialog.show(manager, TAG);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        File downloadFile = new File(file.getPathNoName(), file.getName());
        listener.onFinish(downloadFile.exists(), downloadFlag, file);
        try {
            dialog.dismiss();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        cancel(true);
        context = null;
        listener = null;
        manager = null;
    }

    @Override
    protected void onProgressUpdate(DownloadInfo... values) {
        try {
            DownloadInfo info = values[0];
            dialog.setStatus(String.format(context.getString(R.string.txt_sync_progress), info.downloadedBytes, info.size));
            dialog.setTitle(String.format(context.getString(R.string.txt_sync_status), info.percentage + "%"));
            dialog.setProgress(info.percentage);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
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
                    int downloadedBytes = downloaded;
                    float percentage = (float) downloaded / (float) size * 100;
                    downloadInfo.percentage = (int) percentage;
                    downloadInfo.size = size / 1024;
                    downloadInfo.downloadedBytes = downloadedBytes / 1024;
                    publishProgress(downloadInfo);
                }
                fOut.close();
                file.setModifiedDate(Util.getDateTime());
                file.setPath(CustomApplication.SDCARD_PATH + File.separator + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                downloadFile.delete();
                downloadFlag = false;
            }
        }
        return null;
    }

    public static interface AttachmentTaskListener {
        public void onFinish(boolean success, boolean downloaded, Attachment.File file);
    }

    public static class DownloadInfo {
        int percentage;
        float size, downloadedBytes;
    }
}
