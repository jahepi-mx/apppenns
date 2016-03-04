package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 24/02/2016.
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
