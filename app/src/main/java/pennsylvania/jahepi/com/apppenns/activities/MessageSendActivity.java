package pennsylvania.jahepi.com.apppenns.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.dialogs.ToDialog;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 29/02/2016.
 */
public class MessageSendActivity extends AuthActivity implements DialogListener {

    private static final String TAG = "MessageSendActivity";

    private ToDialog dialog;
    private ArrayList<User> users;
    private TextView toTextView;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_send);

        users = application.getUsers();
        users.remove(application.getUser());

        dialog = new ToDialog();
        dialog.setListener(this);
        dialog.setUsers(users);

        toTextView = (TextView) findViewById(R.id.toAllTextView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageSendActivity.this, MessageListActivity.class);
                startActivity(intent);
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageSendActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button toBtn = (Button) findViewById(R.id.toBtn);
        toBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, TAG);
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
    public void accept() {
        String toAll = "";
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.isSelected()) {
                toAll += user.getName() + " ";
            }
        }
        toTextView.setText(String.format(getString(R.string.txt_message_to_all), toAll));
        dialog.dismiss();
    }

    private void send() {
        ArrayList<User> tmpUsers = new ArrayList<User>();
        String messageStr = messageEditText.getText().toString();
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.isSelected()) {
                tmpUsers.add(user);
            }
        }

        if (Util.isEmpty(messageStr) || tmpUsers.size() == 0) {
            toast(this.getString(R.string.txt_error_send));
            return;
        }

        Iterator<User> tmpIterator = tmpUsers.iterator();
        while (tmpIterator.hasNext()) {
            User user = tmpIterator.next();
            Message message = new Message();
            message.setFrom(application.getUser());
            message.setTo(user);
            message.setMessage(messageStr);
            message.setModifiedDate(Util.getDateTime());
            application.saveMessage(message);
        }

        dialog.reset();
        Intent intent = new Intent(MessageSendActivity.this, MessageListActivity.class);
        startActivity(intent);
    }

    @Override
    public void cancel() {

    }
}
