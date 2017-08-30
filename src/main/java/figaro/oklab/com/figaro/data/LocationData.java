package figaro.oklab.com.figaro.data;

/**
 * Created by olgakuklina on 8/4/17.
 */

public class LocationData {

    private final double latitude;
    private final double longitude;
    private final String locationName;

    public LocationData(double latitude, double longitude, String locationName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationName() {
        return locationName;
    }
}
