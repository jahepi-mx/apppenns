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

    private DialogAdapter userAdapter;
    private DialogAdapter groupAdapter;
    private Button acceptBtn, groupBtn, userBtn;
    private ListView userListView, groupListView;
    private DialogListener listener;
    private ArrayList<Option> userOptions;
    private ArrayList<Option> groupOptions;

    public ToDialog() {
        super();
        userOptions = new ArrayList<Option>();
        groupOptions = new ArrayList<Option>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_to, container, false);
        getDialog().setTitle(getString(R.string.txt_to_users));
        acceptBtn = (Button) view.findViewById(R.id.okToBtn);
        userBtn = (Button) view.findViewById(R.id.userBtn);
        groupBtn = (Button) view.findViewById(R.id.groupBtn);
        userListView = (ListView) view.findViewById(R.id.toUserListView);
        userAdapter = new DialogAdapter(view.getContext(), R.layout.dialog_to_row);
        userAdapter.addAll(userOptions);
        groupListView = (ListView) view.findViewById(R.id.toGroupListView);
        groupAdapter = new DialogAdapter(view.getContext(), R.layout.dialog_to_row);
        groupAdapter.addAll(groupOptions);

        userListView.setAdapter(userAdapter);
        groupListView.setAdapter(groupAdapter);
        userListView.setVisibility(View.GONE);
        groupListView.setVisibility(View.VISIBLE);

        acceptBtn.setOnClickListener(this);
        userBtn.setOnClickListener(this);
        groupBtn.setOnClickListener(this);

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                Option groupOption = (Option) groupAdapter.getItem(position);
                groupOption.selected = !groupOption.selected;
                holder.checkbox.setChecked(groupOption.selected);
                for (Option userOption : userOptions) {
                    if (userOption.getGroups().contains(groupOption.getText())) {
                        userOption.selected = groupOption.selected;
                    }
                }
                userAdapter.notifyDataSetChanged();
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                Option option = (Option) userAdapter.getItem(position);
                option.selected = !option.selected;
                holder.checkbox.setChecked(option.selected);
            }
        });

        return view;
    }

    public void reset() {
        for (Option option : userOptions) {
            option.selected = false;
        }
        for (Option option : groupOptions) {
            option.selected = false;
        }
    }

    public void setUsers(ArrayList<User> users) {
        buildOptions(users);
    }

    private void buildOptions(ArrayList<User> users) {
        for (User user : users) {
            ArrayList<String> groups = user.getGroups();
            for (String group : groups) {
                if (!hasGroupOption(group)) {
                    Option option = new Option();
                    option.addGroup(group);
                    groupOptions.add(option);
                }
            }
            Option option = new Option();
            option.groups = groups;
            option.user = user;
            userOptions.add(option);
        }
    }

    private boolean hasGroupOption(String groupName) {
        for (Option option : groupOptions) {
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
        if (v == acceptBtn) {
            listener.accept(this);
        } else if (v == userBtn) {
            userListView.setVisibility(View.VISIBLE);
            groupListView.setVisibility(View.GONE);
        } else if (v == groupBtn) {
            userListView.setVisibility(View.GONE);
            groupListView.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<Option> getUserOptions() {
        return userOptions;
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

            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.title.setTextColor(Color.BLACK);
            holder.title.setBackgroundColor(Color.TRANSPARENT);
            holder.title.setText(option.getText());
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView title;
        CheckBox checkbox;
    }

    public static class Option implements Serializable {
        private ArrayList<String> groups = new ArrayList<String>();
        private boolean selected;
        private User user;

        public ArrayList<String> getGroups() {
            return groups;
        }

        public void addGroup(String group) {
            this.groups.add(group);
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
            if (isGroupOption()) {
                if (groups.size() >= 1) {
                    String name = groups.get(0);
                    if (name.equals(group)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isGroupOption() {
            return user == null;
        }

        public String getText() {
            if (!isGroupOption()) {
                return user.getName();
            } else {
                if (groups.size() >= 1) {
                    return groups.get(0);
                }
            }
            return "";
        }

        public boolean isSelectedOption() {
            return selected && !isGroupOption();
        }
    }
}