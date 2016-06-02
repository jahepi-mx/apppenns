package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;

/**
 * Created by javier.hernandez on 08/03/2016.
 */
public class CheckOutDialog extends AlertDialog implements View.OnClickListener {

    private EditText editText;
    private MultiAutoCompleteTextView emailTextiew;
    private DialogListener listener;
    private AutoCompleteEmailAdapter adapter;
    private Button yesBtn, noBtn;

    public CheckOutDialog(Context context, DialogListener listener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) inflater.inflate(R.layout.checkout_dialog, null);

        editText = (EditText) view.findViewById(R.id.taskConclusionEditText);
        adapter = new AutoCompleteEmailAdapter(context, R.layout.generic_item);
        emailTextiew = (MultiAutoCompleteTextView) view.findViewById(R.id.emailText);
        emailTextiew.setAdapter(adapter);
        emailTextiew.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        setView(view);
        setTitle(R.string.txt_confirm);
        setIcon(R.drawable.ubication_black);
        setMessage(context.getString(R.string.txt_confirm_checkout));
        noBtn = (Button) view.findViewById(R.id.noBtn);
        yesBtn = (Button) view.findViewById(R.id.yesBtn);
        this.listener = listener;

        noBtn.setOnClickListener(this);
        yesBtn.setOnClickListener(this);
    }

    public String getConclusion() {
        return editText.getText().toString();
    }

    public String getEmails() {
        return emailTextiew.getText().toString();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == yesBtn) {
            listener.accept(this);
        }
    }

    private static class AutoCompleteEmailAdapter extends ArrayAdapter<String> {

        public AutoCompleteEmailAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public String getItem(int position) {
            try {
                return super.getItem(position);
            } catch (Exception exp) {
                return null;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null || convertView.getTag() == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.generic_item, null);
                TextView textViewName = (TextView) convertView.findViewById(R.id.name);
                ViewHolder holder = new ViewHolder();
                holder.textViewName = textViewName;
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position);
            if (item != null) {
                holder.textViewName.setText(getItem(position));
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint != null) {
                        String keyword = constraint.toString();
                        ArrayList<String> emails = ((CustomApplication) getContext().getApplicationContext()).getUserEmails(keyword);
                        if (emails.size() > 0) {
                            results.values = emails;
                            results.count = emails.size();
                        }
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        clear();
                        addAll((ArrayList<String>) results.values);
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        static class ViewHolder {
            TextView textViewName;
        }
    }
}
