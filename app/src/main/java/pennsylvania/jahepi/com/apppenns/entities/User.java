package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 24/02/2016.
 */
public class User extends Entity {

    private int id;
    private String name;
    private String email;
    private String password;
    private boolean selected;

    public String getName() {
        return name;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isNotDefined() {
        return name == null || id == 0;
    }

    @Override
    public boolean equals(Object o) {
        User user = (User) o;
        return user.getId() == id;
    }
}
