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
    private int type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        User user = (User) o;
        return user.getId() == id;
    }
}
