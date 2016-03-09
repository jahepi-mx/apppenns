package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 09/03/2016.
 */
public class Address extends Entity {

    private int id;
    private String address;
    private Coord coord;
    private Client client;

    public Address() {
        coord = new Coord();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
