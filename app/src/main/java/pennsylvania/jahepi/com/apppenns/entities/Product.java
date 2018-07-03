package pennsylvania.jahepi.com.apppenns.entities;

/**
 * Created by javier.hernandez on 04/04/2018.
 * Entity class for a product
 */
public class Product extends Entity {

    private String id;
    private String name;
    private int quantity;
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

    public String toString() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean equals(Product product) {
        return id == product.id && user.getId() == product.getUser().getId();
    }
}
