package figaro.oklab.com.figaro.data;

/**
 * Created by olgakuklina on 8/5/17.
 */

public enum MediaContentType {
    LOCATION_BASED_FEEDS(0),
    MY_RECENT_FEEDS(1);

    private final int matchingMediaContentType;

    MediaContentType(int matchingMediaContentType) {
        this.matchingMediaContentType = matchingMediaContentType;

    }

    public static MediaContentType getMediaContentType(int mediaContentType) {
        for (MediaContentType type : values()) {
            if (type.matchingMediaContentType == mediaContentType) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown repo content type: " + mediaContentType);
    }

    public int getMatchingMediaContentType() {
        return matchingMediaContentType;
    }
}
