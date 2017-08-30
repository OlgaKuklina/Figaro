package figaro.oklab.com.figaro.data;

import java.util.List;

/**
 * Created by olgakuklina on 8/6/17.
 */

public class ParseResult {

    private final List<MediaContentDataEntry> mediaContentList;
    private final String uri;

    public ParseResult(List<MediaContentDataEntry> mediaContentList, String uri) {
        this.mediaContentList = mediaContentList;
        this.uri = uri;
    }

    public List<MediaContentDataEntry> getMediaContentList() {
        return mediaContentList;
    }

    public String getUri() {
        return uri;
    }
}
