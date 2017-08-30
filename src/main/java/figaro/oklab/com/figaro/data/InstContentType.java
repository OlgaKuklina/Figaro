package figaro.oklab.com.figaro.data;

/**
 * Created by olgakuklina on 8/4/17.
 */

public enum InstContentType {

    IMAGE("image"),
    VIDEO("video");


    private final String matchingInstContentType;


    InstContentType(String matchingInstContentType) {
        this.matchingInstContentType = matchingInstContentType;
    }

    public static InstContentType getInstContentType(String instContentType) {
        for (InstContentType type : values()) {
            if (type.matchingInstContentType.equals(instContentType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown instagram content type: " + instContentType);
    }

}
