package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 04/04/2018.
 */
public class Product extends Entity {

    private String id;
    private String name;
    private User user;

    public Product() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
