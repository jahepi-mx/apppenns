package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by jahepi on 20/04/16.
 */
public class Notification extends Entity {

    private int id;
    private User from;
    private User to;
    private String notification;
    private int eventId;
    private int minutes;
    private String eventDate;
    private String fingerprint;

    public Notification() {
        eventId = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        try {
            String[] parts = eventDate.split(" ");
            return parts[1];
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return "";
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public boolean isValid() {
        if (from != null && to != null) {
            return true;
        }
        return false;
    }
}
