package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 04/03/2016.
 */
public class Task extends Entity {

    private int id;
    private String client;
    private String description;
    private Coord checkIn;
    private Coord checkOut;
    private User user;
    private boolean send;

    public Task() {
        checkIn = new Coord();
        checkOut = new Coord();
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

    public Coord getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Coord checkIn) {
        this.checkIn = checkIn;
    }

    public Coord getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Coord checkOut) {
        this.checkOut = checkOut;
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
}
