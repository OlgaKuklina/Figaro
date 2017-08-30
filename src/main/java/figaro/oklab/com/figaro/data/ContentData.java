package figaro.oklab.com.figaro.data;

import java.util.Calendar;

/**
 * Created by olgakuklina on 8/4/17.
 */

public class ContentData {

    private final Calendar createdTime;
    private final String caption;
    private final int likes;
    private final String[] tags;
    private final InstContentType contentType;
    private final String contentUri;
    private final float aspectRatio;

    public ContentData(Calendar createdTime, String caption, int likes, String[] tags, InstContentType contenttype, String contentUri, float aspectRatio) {
        this.createdTime = createdTime;
        this.caption = caption;
        this.likes = likes;
        this.tags = tags;
        this.contentType = contenttype;
        this.contentUri = contentUri;
        this.aspectRatio = aspectRatio;
    }

    public Calendar getCreatedTime() {
        return createdTime;
    }

    public String getCaption() {
        return caption;
    }

    public int getLikes() {
        return likes;
    }

    public String[] getTags() {
        return tags;
    }

    public InstContentType getContentType() {
        return contentType;
    }

    public String getContentUri() {
        return contentUri;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }
}
