package pennsylvania.jahepi.com.apppenns.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.adapters.MessageAdapter;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.dialogs.DateDialog;
import pennsylvania.jahepi.com.apppenns.dialogs.DialogListener;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Task;

/**
 * Created by javier.hernandez on 26/02/2016.
 */
public class MessageListActivity extends AuthActivity implements DialogListener, AdapterView.OnItemLongClickListener, CustomApplication.ApplicationNotifierListener {

    private final static String TAG = "MessageListActivity";

    private ArrayList<Message> messages;
    private MessageAdapter adapter;
    private ListView listView;
    private DateDialog dateDialog;
    private Button dateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list);

        Intent intent = getIntent();
        String date = intent.getStringExtra(CustomApplication.GENERIC_INTENT);
        if (date == null) {
            date = Util.getDate();
        }

        dateDialog = new DateDialog();
        dateDialog.setDate(date);
        dateDialog.setListener(this);

        listView = (ListView) findViewById(R.id.messageListView);
        messages = application.getMessages(date);

        adapter = new MessageAdapter(getApplicationContext(), R.layout.message_item);
        adapter.addAll(messages);

        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = (Message) adapter.getItem(position);
                if (!message.isRead()) {
                    message.setRead(true);
                    application.updateMessageField(message, "read", "1");
                }
                MessageAdapter.ViewHolder holder = (MessageAdapter.ViewHolder) view.getTag();
                holder.getTitle().setTypeface(null, Typeface.NORMAL);
                holder.getTitle().setBackgroundColor(Color.TRANSPARENT);
                Intent intent = new Intent(MessageListActivity.this, MessageViewActivity.class);
                intent.putExtra(CustomApplication.GENERIC_INTENT, message);
                startActivity(intent);
            }
        });

        Button newMessageBtn = (Button) findViewById(R.id.newMessageBtn);
        newMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageListActivity.this, MessageSendActivity.class);
                startActivity(intent);
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        dateBtn = (Button) findViewById(R.id.dateBtn);
        dateBtn.setText(date);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateDialog.isAdded()) {
                    FragmentManager fm = getFragmentManager();
                    dateDialog.show(fm, TAG);
                }
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Message message = (Message) adapter.getItem(position);
        Log.d(TAG, message.getMessage());
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        application.addMessageNotifierListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        application.removeMessageNotifierListener(this);
    }

    @Override
    public void onNewMessages(final ArrayList<Message> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessageListActivity self = MessageListActivity.this;
                Iterator<Message> iterator = messages.iterator();
                while (iterator.hasNext()) {
                    Message message = iterator.next();
                    if (!self.messages.contains(message)) {
                        adapter.insert(message, 0);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onMessagesSend(final ArrayList<Message> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Iterator<Message> iterator = messages.iterator();
                while (iterator.hasNext()) {
                    Message message = iterator.next();
                    Message adapterMessage = adapter.getMessage(message);
                    if (adapterMessage != null) {
                        adapterMessage.setSend(true);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onMessagesRead(final ArrayList<Message> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Iterator<Message> iterator = messages.iterator();
                while (iterator.hasNext()) {
                    Message message = iterator.next();
                    Message adapterMessage = adapter.getMessage(message);
                    if (adapterMessage != null) {
                        adapterMessage.setDelivered(true);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onTasksSend(ArrayList<Task> tasks) {

    }

    @Override
    public void onChangeLocation(double latitude, double longitude) {

    }

    @Override
    public void accept(Object dialog) {
        String date = dateDialog.getDate();
        dateBtn.setText(date);
        adapter.clear();
        adapter.addAll(application.getMessages(date));
    }

    @Override
    public void cancel(Object dialog) {

    }
}
