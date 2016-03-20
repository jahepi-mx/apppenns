package pennsylvania.jahepi.com.apppenns.components.filechooser.activities;

/**
 * Created by jahepi on 20/03/16.
 */
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.components.filechooser.Config;
import pennsylvania.jahepi.com.apppenns.components.filechooser.FileInfo;
import pennsylvania.jahepi.com.apppenns.components.filechooser.adapters.FileArrayAdapter;

public class FileChooserActivity extends ListActivity {

    private static final String TAG = "FileChooserActivity";

    private File currentFolder;
    private FileArrayAdapter fileArrayListAdapter;
    private FileFilter fileFilter;
    private File fileSelected;
    private ArrayList<String> extensions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getStringArrayList(Config.KEY_FILTER_FILES_EXTENSIONS) != null) {
                extensions = extras.getStringArrayList(Config.KEY_FILTER_FILES_EXTENSIONS);
                fileFilter = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return ((pathname.isDirectory()) || (pathname.getName().contains(".") ? extensions.contains(pathname.getName().substring(pathname.getName().lastIndexOf("."))) : false));
                    }
                };
            }
        }
        currentFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fill(currentFolder);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((!currentFolder.getName().equals(Environment.getExternalStorageDirectory().getName())) && (currentFolder.getParentFile() != null)) {
                currentFolder = currentFolder.getParentFile();
                fill(currentFolder);
            } else {
                Log.i(TAG, "cancelled");
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void fill(File f) {
        File[] folders = null;
        if (fileFilter != null) {
            folders = f.listFiles(fileFilter);
        } else {
            folders = f.listFiles();
        }
        this.setTitle(getString(R.string.txt_current_dir) + ": " + f.getName());
        List<FileInfo> dirs = new ArrayList<FileInfo>();
        List<FileInfo> files = new ArrayList<FileInfo>();
        try {
            for (File file : folders) {
                if (file.isDirectory() && !file.isHidden()) {
                    dirs.add(new FileInfo(file.getName(), Config.FOLDER, file.getAbsolutePath(), true, false));
                } else {
                    if (!file.isHidden()) {
                        files.add(new FileInfo(file.getName(), getString(R.string.txt_file_size) + ": " + file.length(), file.getAbsolutePath(), false, false));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(dirs);
        Collections.sort(files);
        dirs.addAll(files);
        if (!f.getName().equalsIgnoreCase(Environment.getExternalStorageDirectory().getName())) {
            if (f.getParentFile() != null) {
                dirs.add(0, new FileInfo("..", Config.PARENT_FOLDER, f.getParent(), false, true));
            }
        }
        fileArrayListAdapter = new FileArrayAdapter(FileChooserActivity.this, R.layout.file_row, dirs);
        this.setListAdapter(fileArrayListAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FileInfo fileDescriptor = fileArrayListAdapter.getItem(position);
        if (fileDescriptor.isFolder() || fileDescriptor.isParent()) {
            currentFolder = new File(fileDescriptor.getPath());
            fill(currentFolder);
        } else {
            fileSelected = new File(fileDescriptor.getPath());
            Intent intent = new Intent();
            intent.putExtra(Config.KEY_FILE_SELECTED, fileSelected.getAbsolutePath());
            setResult(Activity.RESULT_OK, intent);
            Log.i(TAG, "result ok");
            finish();
        }
    }
}
