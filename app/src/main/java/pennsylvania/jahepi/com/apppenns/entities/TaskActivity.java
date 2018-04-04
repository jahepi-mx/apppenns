package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 04/04/2018.
 */
public class TaskActivity extends Entity {

    private int id;
    private String name;
    private int userType;

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

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
