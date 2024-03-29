package pennsylvania.jahepi.com.apppenns.entities;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by javier.hernandez on 24/02/2016.
 * Entity class for a message
 */
public class Message extends Entity {

    private int id;
    private User from;
    private User to;
    private String message;
    private boolean delivered;
    private boolean read;
    private boolean readSync;
    private boolean send;
    private ArrayList<Attachment> attachments;
    private Type type;

    public Message() {
        attachments = new ArrayList<Attachment>();
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public void addAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
    }

    public Iterator<Attachment> getAttachmentsIterator() {
        return attachments.iterator();
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getMessage() {
        if (message != null) {
            return message;
        }
        return "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isReadSync() {
        return readSync;
    }

    public void setReadSync(boolean readSync) {
        this.readSync = readSync;
    }

    public boolean isValid() {
        if (from != null && to != null && type != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        Message msg = (Message) o;
        if (msg.getTo().getId() == to.getId() && msg.getModifiedDateString().equals(getModifiedDateString())) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "to: " + to.getId() + " from: " + from.getId() + " date: " + getModifiedDateString();
    }
}
