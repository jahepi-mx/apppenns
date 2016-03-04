package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.entities.Entity;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 26/02/2016.
 */
public class MessageAdapter extends ArrayAdapter<Entity> {

    private final static int MESSAGE_LENGTH = 35;
    private final static String URI_IN =  "@drawable/anonymus_in";
    private final static String URI_OUT =  "@drawable/anonymus_out";
    private final static String TICK = "✔";

    private User user;

    public MessageAdapter(Context context, int resource, User user) {
        super(context, resource);
        this.user = user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null || convertView.getTag() == null) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_item, parent, false);
            TextView title = (TextView) convertView.findViewById(R.id.messageTitle);
            TextView text = (TextView) convertView.findViewById(R.id.messageText);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            int imageResourceIn = getContext().getResources().getIdentifier(URI_IN, null, getContext().getPackageName());
            Drawable drawableIn = getContext().getResources().getDrawable(imageResourceIn);
            int imageResourceOut = getContext().getResources().getIdentifier(URI_OUT, null, getContext().getPackageName());
            Drawable drawableOut = getContext().getResources().getDrawable(imageResourceOut);

            ViewHolder holder = new ViewHolder();
            holder.text = text;
            holder.title = title;
            holder.imageView = imageView;
            holder.drawableIn = drawableIn;
            holder.drawableOut = drawableOut;

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        Message message = (Message) getItem(position);

        if (message.isRead()) {
            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.title.setText(message.getFrom().getName());
        } else {
            holder.title.setTypeface(null, Typeface.BOLD);
            holder.title.setText(String.format(getContext().getString(R.string.txt_new_message), message.getFrom().getName()));
        }
        if (message.isSend()) {
            holder.title.setText(holder.title.getText() + TICK);
        }
        if (message.isDelivered()) {
            holder.title.setText(holder.title.getText() + TICK);
        }
        if (!user.equals(message.getFrom())) {
            holder.imageView.setImageDrawable(holder.drawableIn);
        } else {
            holder.imageView.setImageDrawable(holder.drawableOut);
        }

        holder.text.setText(Util.abbreviate(message.getMessage(), MESSAGE_LENGTH));
        return convertView;
    }

    public static class ViewHolder {
        TextView title;
        TextView text;
        ImageView imageView;
        Drawable drawableIn;
        Drawable drawableOut;
        public TextView getTitle() {
            return title;
        }
    }
}
