package pennsylvania.jahepi.com.apppenns.entities;

import java.util.ArrayList;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class User extends Entity {

    private int id;
    private String name;
    private String email;
    private String password;
    private ArrayList<String> groups = new ArrayList<String>();

    public String getName() {
        if (name != null) {
            return name;
        }
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void addGroup(String group) {
        this.groups.add(group);
    }

    @Override
    public boolean equals(Object o) {
        User user = (User) o;
        return user.getId() == id;
    }
}
