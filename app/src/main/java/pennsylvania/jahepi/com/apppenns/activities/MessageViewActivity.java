package pennsylvania.jahepi.com.apppenns.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.entities.Message;

/**
 * Created by javier.hernandez on 29/02/2016.
 */
public class MessageViewActivity extends AuthActivity {

    private static final String TAG = "MessageViewActivity";

    private FileAttachmentAdapter fileAttachmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_view);

        Message message = (Message) getIntent().getSerializableExtra(CustomApplication.GENERIC_INTENT);

        if (message != null) {

            fileAttachmentAdapter = new FileAttachmentAdapter(this, R.layout.file_item);
            fileAttachmentAdapter.setHideDeleteOption(true);

            Iterator<Message.Attachment> iterator = message.getAttachmentsIterator();
            while (iterator.hasNext()) {
                Message.Attachment attachment = iterator.next();
                fileAttachmentAdapter.add(attachment.getFile());
            }

            ListView attachmentList = (ListView) findViewById(R.id.attachmentsListView);
            attachmentList.setAdapter(fileAttachmentAdapter);

            TextView date = (TextView) findViewById(R.id.messageDateValue);
            TextView from = (TextView) findViewById(R.id.messageFromValue);
            TextView to = (TextView) findViewById(R.id.messageToValue);
            TextView msg = (TextView) findViewById(R.id.messageValue);

            date.setText(message.getModifiedDateString());
            from.setText(message.getFrom().getName());
            to.setText(message.getTo().getName());
            msg.setText(message.getMessage());
        }

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageViewActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageViewActivity.this, MessageListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
