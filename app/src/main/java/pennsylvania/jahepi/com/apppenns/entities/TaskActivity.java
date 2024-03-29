package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 04/04/2018.
 * Entity class for a task activity
 */
public class TaskActivity extends Entity {

    private int id;
    private String name;
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
