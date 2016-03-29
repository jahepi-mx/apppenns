package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.components.filechooser.Config;
import pennsylvania.jahepi.com.apppenns.components.filechooser.activities.FileChooserActivity;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.dialogs.ToDialog;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 29/02/2016.
 */
public class MessageSendActivity extends AuthActivity implements DialogListener {

    private static final String TAG = "MessageSendActivity";

    private static final int REQUEST_CODE = 1;

    private ToDialog dialog;
    private TextView toTextView;
    private EditText messageEditText;
    private ListView attachmentList;
    private FileAttachmentAdapter fileAttachmentAdapter;
    private ArrayList<ToDialog.Option> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_send);

        fileAttachmentAdapter = new FileAttachmentAdapter(this, R.layout.file_item);

        options = new ArrayList<ToDialog.Option>();

        ArrayList<User> users = application.getUsers();
        users.remove(application.getUser());

        dialog = new ToDialog();
        dialog.setListener(this);
        dialog.setUsers(users);

        toTextView = (TextView) findViewById(R.id.toAllTextView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);

        attachmentList = (ListView) findViewById(R.id.attachmentsListView);
        attachmentList.setAdapter(fileAttachmentAdapter);

        Button filesBtn = (Button) findViewById(R.id.filesBtn);
        filesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MessageSendActivity.this, FileChooserActivity.class), REQUEST_CODE);
            }
        });

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageSendActivity.this, MessageListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageSendActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button toBtn = (Button) findViewById(R.id.toBtn);
        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (!dialog.isAdded()) {
                    dialog.show(fm, TAG);
                }
            }
        });

        Button sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    @Override
    public void accept(Object dialogParam) {
        String toAll = "";
        options = dialog.getOptions();
        for (ToDialog.Option option : options) {
            if (option.isSelectedOption()) {
                toAll += option.getUser().getName() + " ";
            }
        }
        toTextView.setText(String.format(getString(R.string.txt_message_to_all), toAll));
        dialog.dismiss();
    }

    private void send() {
        ArrayList<User> tmpUsers = new ArrayList<User>();
        String messageStr = messageEditText.getText().toString();
        for (ToDialog.Option option : options) {
            if (option.isSelectedOption()) {
                tmpUsers.add(option.getUser());
            }
        }

        if (Util.isEmpty(messageStr) || tmpUsers.size() == 0) {
            toast(this.getString(R.string.txt_error_send));
            return;
        }

        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        for (int i = 0; i < fileAttachmentAdapter.getCount(); i++) {
            Attachment attachment = fileAttachmentAdapter.getItem(i);
            if (application.saveFile(attachment.getFile())) {
                attachments.add(attachment);
            }
        }

        for (User user : tmpUsers) {
            Message message = new Message();
            message.setFrom(application.getUser());
            message.setTo(user);
            message.setMessage(messageStr);
            message.setModifiedDate(Util.getDateTime());
            message.addAttachments(attachments);
            message.setRead(true);
            application.saveMessage(message);
        }

        dialog.reset();
        Intent intent = new Intent(MessageSendActivity.this, MessageListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void cancel(Object dialogParam) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && data != null) {
            File file = (File) data.getSerializableExtra(Config.KEY_FILE_SELECTED);
            if (file != null) {
                Log.d(TAG, file.getAbsolutePath());
                Attachment.File attachmentFile = new Attachment.File();
                attachmentFile.setPath(file.getAbsolutePath());
                attachmentFile.setName(file.getName());
                String extension = "";
                String mime = "";
                try {
                    extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toURL().toString());
                } catch (MalformedURLException exp) {
                    exp.printStackTrace();;
                }
                if (extension != null) {
                    mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                attachmentFile.setMime(mime);
                attachmentFile.setModifiedDate(Util.getDateTime());
                Attachment attachment = new Attachment();
                attachment.setFile(attachmentFile);
                if (fileAttachmentAdapter.addAttachment(attachment)) {
                    fileAttachmentAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
