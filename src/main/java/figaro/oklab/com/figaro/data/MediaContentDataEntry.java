package figaro.oklab.com.figaro.data;

/**
 * Created by olgakuklina on 8/3/17.
 */

public class MediaContentDataEntry {

    public static final String TAG = MediaContentDataEntry.class.getSimpleName();

    private final String entryId;
    private final UserData userData;
    private final ContentData contentData;
    private final LocationData locationData;


    public MediaContentDataEntry(String entryId, UserData userData, ContentData contentData, LocationData locationData) {
        this.entryId = entryId;
        this.userData = userData;
        this.contentData = contentData;
        this.locationData = locationData;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getEntryId() {
        return entryId;
    }

    public UserData getUserData() {
        return userData;
    }

    public ContentData getContentData() {
        return contentData;
    }

    public LocationData getLocationData() {
        return locationData;
    }
}
