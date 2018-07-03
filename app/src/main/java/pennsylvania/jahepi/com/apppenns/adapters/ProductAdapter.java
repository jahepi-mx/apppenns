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
import pennsylvania.jahepi.com.apppenns.entities.Product;

/**
 * Created by javier.hernandez on 09/03/2016.
 * Class adapter for products.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    private int mResource;

    public ProductAdapter(Context context, int resource) {
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
        Product product = getItem(position);
        if (product != null) {
            holder.textView.setText(product.getId() + " - " + product.getName());
        }
        return convertView;
    }

    @Override
    public Product getItem(int position) {
        try {
            return super.getItem(position);
        } catch (Exception exp) {
            return null;
        }
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    ArrayList<Product> products = ((CustomApplication) getContext()).getProducts(constraint.toString());
                    results.count = products.size();
                    results.values = products;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    clear();
                    addAll((ArrayList<Product>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private static class ViewHolder {
        TextView textView;
    }
}
