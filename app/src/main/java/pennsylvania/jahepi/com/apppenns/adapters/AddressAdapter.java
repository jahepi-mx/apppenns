package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Address;

/**
 * Created by javier.hernandez on 09/03/2016.
 * Class adapter for addresses
 */
public class AddressAdapter extends ArrayAdapter<Address> {

    private int mResource;

    public AddressAdapter(Context context, int resource) {
        super(context, resource);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, null);
            TextView textView = (TextView) convertView.findViewById(R.id.name);
            ViewHolder holder = new ViewHolder();
            holder.textView = textView;
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Address address = getItem(position);
        holder.textView.setText(address.getAddress());
        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }
}
