package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 29/02/2016.
 */
public class ToDialog extends DialogFragment implements View.OnClickListener {

    private DialogAdapter adapter;
    private Button acceptBtn;
    private DialogListener listener;
    private ArrayList<Option> options;

    public ToDialog() {
        super();
        options = new ArrayList<Option>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_to, container, false);
        getDialog().setTitle(getString(R.string.txt_users));
        acceptBtn = (Button) view.findViewById(R.id.okToBtn);
        ListView listView = (ListView) view.findViewById(R.id.toListView);
        adapter = new DialogAdapter(view.getContext(), R.layout.dialog_to_row);
        adapter.addAll(options);

        listView.setAdapter(adapter);
        acceptBtn.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                Option option = (Option) adapter.getItem(position);
                option.selected = !option.selected;
                holder.checkbox.setChecked(option.selected);
                if (option.isGroupOption()) {
                    for (Option tempOption : options) {
                        if (tempOption.getGroup().equals(option.getGroup())) {
                            tempOption.selected = option.selected;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    public void reset() {
        for (Option option : options) {
            option.selected = false;
        }
    }

    public void setUsers(ArrayList<User> users) {
        buildOptions(users);
    }

    private void buildOptions(ArrayList<User> users) {
        for (User user : users) {
            if (!hasGroupOption(user.getGroup())) {
                Option option = new Option();
                option.group = user.getGroup();
                options.add(option);
            }
            Option option = new Option();
            option.group = user.getGroup();
            option.user = user;
            options.add(option);
        }
    }

    private boolean hasGroupOption(String groupName) {
        for (Option option : options) {
            if (option.isGroupOptionName(groupName)) {
                return true;
            }
        }
        return false;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.accept(this);
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    private static class DialogAdapter extends ArrayAdapter<Option> {

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
            Option option = (Option) getItem(position);

            if (option.selected) {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }

            if (!option.isGroupOption()) {
                holder.title.setTypeface(null, Typeface.NORMAL);
                holder.title.setTextColor(Color.BLACK);
                holder.title.setBackgroundColor(Color.TRANSPARENT);
                holder.title.setText(option.user.getName());
            } else {
                holder.title.setTypeface(null, Typeface.BOLD);
                holder.title.setTextColor(Color.WHITE);
                holder.title.setBackgroundColor(Color.BLACK);
                holder.title.setText(option.group);
            }

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView title;
        CheckBox checkbox;
    }

    public static class Option implements Serializable {
        private String group;
        private boolean selected;
        private User user;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isGroupOptionName(String group) {
            return this.group.equals(group) && isGroupOption();
        }

        public boolean isGroupOption() {
            return user == null;
        }

        public boolean isSelectedOption() {
            return selected && !isGroupOptionName(group);
        }
    }
}