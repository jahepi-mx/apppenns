package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Entity;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 29/02/2016.
 */
public class ToDialog extends Dialog implements View.OnClickListener {

    private ArrayList<User> users;
    private DialogAdapter adapter;
    private Button acceptBtn;
    private DialogListener listener;

    public ToDialog(Context context, ArrayList<User> users, DialogListener listener) {
        super(context);
        setContentView(R.layout.dialog_to);
        setTitle(context.getString(R.string.txt_users));
        this.users = users;
        acceptBtn = (Button) findViewById(R.id.okToBtn);
        ListView listView = (ListView) findViewById(R.id.toListView);
        adapter = new DialogAdapter(context, R.layout.dialog_to_row);
        adapter.addAll(users);

        listView.setAdapter(adapter);
        this.listener = listener;
        acceptBtn.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                User user = (User) adapter.getItem(position);
                user.setSelected(!user.isSelected());
                holder.checkbox.setChecked(user.isSelected());
            }
        });
    }

    public void reset() {
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            user.setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {
        listener.accept();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    private static class DialogAdapter extends ArrayAdapter<Entity> {

        public DialogAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null || convertView.getTag() == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.dialog_to_row, parent, false);
                TextView title = (TextView) convertView.findViewById(R.id.toTextView);
                CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.toCheckBox);

                ViewHolder holder = new ViewHolder();
                holder.checkbox = checkbox;
                holder.title = title;

                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            User user = (User) getItem(position);

            if (user.isSelected()) {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }
            holder.title.setText(user.getName());

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView title;
        CheckBox checkbox;
    }
}