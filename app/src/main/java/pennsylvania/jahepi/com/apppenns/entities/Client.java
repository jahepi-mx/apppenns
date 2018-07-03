package pennsylvania.jahepi.com.apppenns.entities;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by javier.hernandez on 09/03/2016.
 * Entity class for a client
 */
public class Client extends Entity {

    private int id;
    private String name;
    private String kepler;
    private User user;
    private ArrayList<Address> addresses;

    public Client() {
        addresses = new ArrayList<Address>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKepler() {
        return kepler;
    }

    public void setKepler(String kepler) {
        this.kepler = kepler;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }

    public Iterator<Address> getAddressIterator() {
        return addresses.iterator();
    }

    @Override
    public String toString() {
        return name;
    }
}
