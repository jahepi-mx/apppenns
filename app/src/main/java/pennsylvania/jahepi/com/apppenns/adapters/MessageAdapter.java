package pennsylvania.jahepi.com.apppenns.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.entities.Entity;
import pennsylvania.jahepi.com.apppenns.entities.Message;
import pennsylvania.jahepi.com.apppenns.entities.Type;
import pennsylvania.jahepi.com.apppenns.entities.User;

/**
 * Created by javier.hernandez on 26/02/2016.
 * Class adapter for messages.
 */
public class MessageAdapter extends ArrayAdapter<Entity> {

    private final static int MESSAGE_LENGTH = 35;
    private final static String URI_IN =  "@drawable/anonymus_in";
    private final static String URI_OUT =  "@drawable/anonymus_out";
    private final static String TICK = "✔";

    private User user;
    private int mResource;

    public MessageAdapter(Context context, int resource) {
        super(context, resource);
        this.user = ((CustomApplication) context).getUser();
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null || convertView.getTag() == null) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
            TextView title = (TextView) convertView.findViewById(R.id.messageTitle);
            TextView type = (TextView) convertView.findViewById(R.id.messageType);
            TextView text = (TextView) convertView.findViewById(R.id.messageText);
            TextView to = (TextView) convertView.findViewById(R.id.messageTextTo);
            TextView date = (TextView) convertView.findViewById(R.id.messageDate);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            int imageResourceIn = getContext().getResources().getIdentifier(URI_IN, null, getContext().getPackageName());
            Drawable drawableIn = getContext().getResources().getDrawable(imageResourceIn);
            int imageResourceOut = getContext().getResources().getIdentifier(URI_OUT, null, getContext().getPackageName());
            Drawable drawableOut = getContext().getResources().getDrawable(imageResourceOut);
            RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.messageContainer);

            ViewHolder holder = new ViewHolder();
            holder.text = text;
            holder.type = type;
            holder.title = title;
            holder.to = to;
            holder.date = date;
            holder.imageView = imageView;
            holder.drawableIn = drawableIn;
            holder.drawableOut = drawableOut;
            holder.layout = layout;

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        Message message = (Message) getItem(position);

        Type type = message.getType();
        if (type != null) {
            holder.layout.setBackgroundColor(Color.parseColor(type.getColor()));
        }

        if (message.isRead()) {
            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.title.setBackgroundColor(Color.TRANSPARENT);
            holder.title.setTextColor(Color.BLACK);
            holder.title.setText(String.format(getContext().getString(R.string.txt_new_message_from), message.getFrom().getName()));
            holder.type.setTypeface(null, Typeface.NORMAL);
            holder.type.setBackgroundColor(Color.TRANSPARENT);
            holder.type.setTextColor(Color.BLACK);
            holder.type.setText(message.getType().getName());
        } else {
            holder.title.setTypeface(null, Typeface.BOLD);
            holder.title.setBackgroundColor(Color.TRANSPARENT);
            holder.title.setTextColor(Color.BLACK);
            holder.title.setText(String.format(getContext().getString(R.string.txt_new_message), message.getFrom().getName()));
            holder.type.setTypeface(null, Typeface.BOLD);
            holder.type.setBackgroundColor(Color.TRANSPARENT);
            holder.type.setTextColor(Color.BLACK);
            holder.type.setText(message.getType().getName());
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
        holder.date.setText(message.getModifiedDateString());
        holder.to.setText(String.format(getContext().getString(R.string.txt_new_message_to), message.getTo().getName()));
        return convertView;
    }

    public Message getMessage(Message message) {
        for (int i = 0; i < getCount(); i++) {
            Message msg = (Message) getItem(i);
            if (message.equals(msg)) {
                return msg;
            }
        }
        return null;
    }

    public static class ViewHolder {
        TextView type, title, text, date, to;
        ImageView imageView;
        Drawable drawableIn;
        Drawable drawableOut;
        RelativeLayout layout;
        public TextView getTitle() {
            return title;
        }
    }
}
