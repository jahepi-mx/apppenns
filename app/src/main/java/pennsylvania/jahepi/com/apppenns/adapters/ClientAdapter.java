package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Client;

/**
 * Created by javier.hernandez on 09/03/2016.
 */
public class ClientAdapter extends ArrayAdapter<Client> {

    public ClientAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.generic_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.name);
            ViewHolder holder = new ViewHolder();
            holder.textView = textView;
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Client client = getItem(position);
        holder.textView.setText(client.getName());
        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    ArrayList<Client> clients = ((CustomApplication) getContext()).getClients(constraint.toString());
                    results.count = clients.size();
                    results.values = clients;
                    if (clients.size() > 0) {
                        ClientAdapter.this.clear();
                        ClientAdapter.this.addAll(clients);
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }
}
