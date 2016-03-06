package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 04/03/2016.
 */
public class Task extends Entity {

    private int id;
    private String client;
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

    public Task() {
        checkInCoord = new Coord();
        checkOutCoord = new Coord();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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

    @Override
    public boolean equals(Object o) {
        Task task = (Task) o;
        if (task.getId() == id && task.getUser().getId() == user.getId()) {
            return true;
        }
        return false;
    }
}
