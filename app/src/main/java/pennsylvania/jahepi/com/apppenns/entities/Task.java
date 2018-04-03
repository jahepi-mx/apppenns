package pennsylvania.jahepi.com.apppenns.entities;

import java.util.ArrayList;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.Util;

/**
 * Created by javier.hernandez on 04/03/2016.
 */
public class Task extends Entity {

    public static final String STATUS_RESCHEDULED = "rescheduled";
    public static final String STATUS_TRACKING = "tracking";
    public static final String STATUS_NONE = "none";

    private int id;
    private Address address;
    private String description;
    private Coord checkInCoord;
    private String checkInDate;
    private String checkOutDate;
    private Coord checkOutCoord;
    private boolean checkin;
    private boolean checkout;
    private User user;
    private boolean send;
    private String date;
    private String conclusion;
    private float distance;
    private Type type;
    private String startTime;
    private String endTime;
    private boolean cancelled;
    private int eventId;
    private String emails;
    private Task parentTask;
    private ArrayList<Attachment> attachments;
    private ArrayList<Notification> notifications;
    private String fingerprint;
    private boolean updateAllState;
    private String status;
    private boolean hasChildren;

    public Task() {
        checkInCoord = new Coord();
        checkOutCoord = new Coord();
        attachments = new ArrayList<Attachment>();
        notifications = new ArrayList<Notification>();
        updateAllState = true;
        status = STATUS_NONE;
    }

    public void setCheckOutLatitude(double latitude) {
        checkOutCoord.setLatitude(latitude);
    }

    public void setCheckOutLongitude(double longitude) {
        checkOutCoord.setLongitude(longitude);
    }

    public void setCheckInLatitude(double latitude) {
        checkInCoord.setLatitude(latitude);
    }

    public void setCheckInLongitude(double longitude) {
        checkInCoord.setLongitude(longitude);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return address.getClient();
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Coord getCheckInCoord() {
        return checkInCoord;
    }

    public void setCheckInCoord(Coord checkInCoord) {
        this.checkInCoord = checkInCoord;
    }

    public Coord getCheckOutCoord() {
        return checkOutCoord;
    }

    public void setCheckOutCoord(Coord checkOutCoord) {
        this.checkOutCoord = checkOutCoord;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCheckin() {
        return checkin;
    }

    public void setCheckin(boolean checkin) {
        this.checkin = checkin;
    }

    public boolean isCheckout() {
        return checkout;
    }

    public void setCheckout(boolean checkout) {
        this.checkout = checkout;
    }

    public String getConclusion() {
        if (conclusion == null) {
            return "";
        }
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getCheckInDate() {
        if (checkInDate != null) {
            return checkInDate;
        }
        return "";
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        if (checkOutDate != null) {
            return checkOutDate;
        }
        return "";
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(double latitude, double longitude) {
        distance = Util.getDistance(latitude, longitude, address.getCoord().getLatitude(), address.getCoord().getLongitude());
    }

    public String getStartDateTime() {
        return date + " " + startTime;
    }

    public String getEndDateTime() {
        return date + " " + endTime;
    }

    public int getParentTaskId() {
        if (parentTask != null) {
            return parentTask.getId();
        }
        return 0;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public String getEmails() {
        if (emails == null) {
            return "";
        }
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public void addNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    public Iterator<Notification> getNotificationsIterator() {
        return notifications.iterator();
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
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

    public ArrayList<Attachment> getAttachmentsFromConclusion() {
        ArrayList<Attachment> attachments = new ArrayList<>();
        Iterator<Attachment> iterator = getAttachmentsIterator();
        while (iterator.hasNext()) {
            Attachment attachment = iterator.next();
            if (attachment.isFromConclusion()) {
                attachments.add(attachment);
            }
        }
        return attachments;
    }

    public ArrayList<Attachment> getAttachmentsFromNonConclusion() {
        ArrayList<Attachment> attachments = new ArrayList<>();
        Iterator<Attachment> iterator = getAttachmentsIterator();
        while (iterator.hasNext()) {
            Attachment attachment = iterator.next();
            if (!attachment.isFromConclusion()) {
                attachments.add(attachment);
            }
        }
        return attachments;
    }

    public boolean isValid() {
        if (type != null && address != null && address.getClient() != null && user != null) {
            return true;
        }
        return false;
    }

    public boolean updateAllState() {
        return updateAllState;
    }

    public void setUpdateAllState(boolean updateRemainingState) {
        this.updateAllState = updateRemainingState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public void copy(Task task) {
        setUser(task.getUser());
        setAddress(task.getAddress());
        setDescription(task.getDescription());
        setDate(task.getDate());
        setModifiedDate(task.getModifiedDateString());
        setType(task.getType());
        setStartTime(task.getStartTime());
        setEndTime(task.getEndTime());
        setSend(task.isSend());
        addAttachments(task.getAttachments());
        // addNotifications(task.getNotifications());
    }

    @Override
    public boolean equals(Object o) {
        Task task = (Task) o;
        if (task.getUser().getId() == user.getId() && task.getFingerprint().equals(fingerprint)) {
            return true;
        }
        return false;
    }
}
