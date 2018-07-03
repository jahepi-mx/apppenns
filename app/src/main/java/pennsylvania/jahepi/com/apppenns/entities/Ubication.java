package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 12/04/2016.
 * Entity class for an ubication
 */
public class Ubication extends Entity {

    private int id;
    private User user;
    private Coord coord;
    private boolean send;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public void setCoord(double latitude, double longitude) {
        coord = new Coord();
        coord.setLatitude(latitude);
        coord.setLongitude(longitude);
    }
}
