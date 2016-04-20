package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by jahepi on 12/03/16.
 */
public class Type extends Entity {

    public final static String MESSAGE_CATEGORY = "Message";
    public final static String ACTIVITY_CATEGORY = "Activity";

    private int id;
    private String name;
    private String category;
    private String color;

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

    @Override
    public String toString() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
