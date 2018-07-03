package pennsylvania.jahepi.com.apppenns.entities;

import java.io.Serializable;

/**
 * Created by javier.hernandez on 04/03/2016.
 * Entity class for a coordinate
 */
public class Coord implements Serializable {

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
