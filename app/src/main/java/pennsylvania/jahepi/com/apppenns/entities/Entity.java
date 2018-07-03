package pennsylvania.jahepi.com.apppenns.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by javier.hernandez on 24/02/2016.
 * Base entity class that must be inherited from all entitiy class objects
 */
public class Entity implements Serializable {

    private Date modifiedDate;
    private boolean active;

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.modifiedDate = date.parse(modifiedDate);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public boolean isGreaterDate(Entity entity) {
        if (entity.getModifiedDate() != null && modifiedDate != null) {
            return entity.getModifiedDate().after(modifiedDate);
        }
        return false;
    }

    public String getModifiedDateString() {
        if (modifiedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(modifiedDate);
        }
        return null;
    }

    public String getModifiedDateNoTimeString() {
        if (modifiedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(modifiedDate);
        }
        return null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
