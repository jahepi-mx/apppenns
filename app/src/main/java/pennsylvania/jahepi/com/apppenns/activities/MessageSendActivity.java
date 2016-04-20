package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.components.AudioRecorder;
import pennsylvania.jahepi.com.apppenns.components.TypeSpinner;
import pennsylvania.jahepi.com.apppenns.components.filechooser.Config;
import pennsylvania.jahepi.com.apppenns.components.filechooser.activities.FileChooserActivity;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.dialogs.ToDialog;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Type;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 29/02/2016.
 */
public class MessageSendActivity extends AuthActivity implements DialogListener, View.OnClickListener {

    private final static String TAG = "MessageSendActivity";

    private final static int REQUEST_CODE = 1;
    private final static int REQUEST_IMAGE_CAPTURE = 2;

    private static final String OPTIONS_STATE = "options_state";
    private static final String ATTACHMENTS_STATE = "attachments_state";
    private static final String PHOTO_STATE = "photo_state";
    private static final String MESSAGE_STATE = "message_state";
    private static final String TYPE_STATE = "type_state";

    private ToDialog dialog;
    private TextView toTextView;
    private EditText messageEditText;
    private ListView attachmentList;
    private FileAttachmentAdapter fileAttachmentAdapter;
    private ArrayList<ToDialog.Option> options;
    private File photoFile;
    private Button photoBtn, filesBtn, backBtn, toBtn, sendBtn, audioBtn;
    private ImageButton homeBtn;
    private AudioRecorder audioRecorder;
    private TypeSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_send);

        spinner = (TypeSpinner) findViewById(R.id.typeSpinner);
        ArrayList<Type> types = application.getTypes(Type.MESSAGE_CATEGORY);
        ArrayAdapter<Type> typeAdapter = new ArrayAdapter<Type>(this, R.layout.type_item, types);
        spinner.setAdapter(typeAdapter);

        fileAttachmentAdapter = new FileAttachmentAdapter(this, R.layout.file_item);

        options = new ArrayList<ToDialog.Option>();

        audioRecorder = new AudioRecorder(application.getAndroidId(), new AudioRecorder.AudioRecorderListener() {
            @Override
            public void onStartRecording() {
                audioBtn.setText(R.string.txt_recording);
                audioBtn.setBackgroundResource(R.drawable.custom_button_red);
            }

            @Override
            public void onStopRecording(final File audio) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        audioBtn.setText(R.string.btn_audio);
                        audioBtn.setBackgroundResource(R.drawable.custom_button_green);
                        fileAttachmentAdapter.add(Util.buildAttachment(audio));
                        fileAttachmentAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onRecordingError() {
                audioBtn.setText(R.string.btn_audio);
                audioBtn.setBackgroundResource(R.drawable.custom_button_green);
                toast(getString(R.string.txt_audio_error));
            }

            @Override
            public void onRecordingTime(final String time) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        audioBtn.setText(getString(R.string.txt_recording) + " " + time);
                    }
                });
            }
        });

        ArrayList<User> users = application.getUsers();
        users.remove(application.getUser());

        dialog = new ToDialog();
        dialog.setListener(this);
        dialog.setUsers(users);

        toTextView = (TextView) findViewById(R.id.toAllTextView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);

        attachmentList = (ListView) findViewById(R.id.attachmentsListView);
        attachmentList.setAdapter(fileAttachmentAdapter);

        photoBtn = (Button) findViewById(R.id.photoBtn);
        filesBtn = (Button) findViewById(R.id.filesBtn);
        backBtn = (Button) findViewById(R.id.backBtn);
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        toBtn = (Button) findViewById(R.id.toBtn);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        audioBtn = (Button) findViewById(R.id.audioBtn);

        photoBtn.setOnClickListener(this);
        filesBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        toBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
        audioBtn.setOnClickListener(this);

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (audioRecorder.isRecording()) {
            audioRecorder.stop();
        }
    }

    @Override
    public void accept(Object dialogParam) {
        options = dialog.getUserOptions();
        displaySelectedUsers(options);
        dialog.dismiss();
    }

    private void displaySelectedUsers(ArrayList<ToDialog.Option> options) {
        String toAll = "";
        for (ToDialog.Option option : options) {
            if (option.isSelectedOption()) {
                toAll += option.getUser().getName() + " ";
            }
        }
        toTextView.setText(String.format(getString(R.string.txt_message_to_all), toAll));
    }

    private void send() {
        ArrayList<User> tmpUsers = new ArrayList<User>();
        String messageStr = messageEditText.getText().toString();
        for (ToDialog.Option option : options) {
            if (option.isSelectedOption()) {
                tmpUsers.add(option.getUser());
            }
        }

        if (Util.isEmpty(messageStr) || tmpUsers.size() == 0 || spinner.getSelectedItem() == null) {
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
            message.setType((Type) spinner.getSelectedItem());
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoFile != null) {
                Attachment attachment = Util.buildAttachment(photoFile);
                photoFile = null;
                if (fileAttachmentAdapter.addAttachment(attachment)) {
                    fileAttachmentAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == REQUEST_CODE && data != null) {
            File file = (File) data.getSerializableExtra(Config.KEY_FILE_SELECTED);
            if (file != null) {
                Attachment attachment = Util.buildAttachment(file);
                if (fileAttachmentAdapter.addAttachment(attachment)) {
                    fileAttachmentAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == photoBtn) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                photoFile = Util.createImageFile(application.getAndroidId());
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else if (v == filesBtn) {
            startActivityForResult(new Intent(MessageSendActivity.this, FileChooserActivity.class), REQUEST_CODE);
        } else if (v == backBtn) {
            Intent intent = new Intent(MessageSendActivity.this, MessageListActivity.class);
            startActivity(intent);
            finish();
        } else if (v == homeBtn) {
            Intent intent = new Intent(MessageSendActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (v == toBtn) {
            FragmentManager fm = getFragmentManager();
            if (!dialog.isAdded()) {
                dialog.show(fm, TAG);
            }
        } else if (v == sendBtn) {
            send();
        } else if (v == audioBtn) {
            if (audioRecorder.isRecording()) {
                audioRecorder.stop();
            } else {
                audioRecorder.start();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(OPTIONS_STATE, options);
        outState.putSerializable(ATTACHMENTS_STATE, fileAttachmentAdapter.getAttachments());
        outState.putSerializable(PHOTO_STATE, photoFile);
        outState.putString(MESSAGE_STATE, messageEditText.getText().toString());
        outState.putSerializable(TYPE_STATE, (Type) spinner.getSelectedItem());
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        options = (ArrayList<ToDialog.Option>) savedInstanceState.get(OPTIONS_STATE);
        displaySelectedUsers(options);
        ArrayList<Attachment> attachments = (ArrayList<Attachment>)savedInstanceState.get(ATTACHMENTS_STATE);
        File photoFile = (File) savedInstanceState.get(PHOTO_STATE);
        if (photoFile != null) {
            Attachment attachment = Util.buildAttachment(photoFile);
            attachments.add(attachment);
        }
        fileAttachmentAdapter.addAll(attachments);
        fileAttachmentAdapter.notifyDataSetChanged();
        messageEditText.setText(savedInstanceState.getString(MESSAGE_STATE));
        spinner.setSelectedItem((Type) savedInstanceState.get(TYPE_STATE));
    }
}
