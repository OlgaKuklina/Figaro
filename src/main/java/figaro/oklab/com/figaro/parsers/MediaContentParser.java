package figaro.oklab.com.figaro.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

import figaro.oklab.com.figaro.data.ContentData;
import figaro.oklab.com.figaro.data.InstContentType;
import figaro.oklab.com.figaro.data.LocationData;
import figaro.oklab.com.figaro.data.MediaContentDataEntry;
import figaro.oklab.com.figaro.data.ParseResult;
import figaro.oklab.com.figaro.data.UserData;

import static figaro.oklab.com.figaro.data.InstContentType.getInstContentType;

/**
 * Created by olgakuklina on 8/3/17.
 */

public final class MediaContentParser {

    private static final String TAG = MediaContentParser.class.getSimpleName();

    private MediaContentParser() {
    }

    public static ParseResult parse(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            return null;
        }
        String uri = null;
        JSONObject pagination = jsonObject.has("pagination") ? jsonObject.getJSONObject("pagination") : null;
        if (pagination != null) {
            uri = pagination.has("next_url") ? pagination.getString("next_url") : null;
        }
        Log.v(TAG, "uri = " + uri);
        JSONArray dataArray = jsonObject.getJSONArray("data");

        if (dataArray == null || dataArray.length() == 0) {
            return new ParseResult(Collections.<MediaContentDataEntry>emptyList(), uri);
        }
        ArrayList<MediaContentDataEntry> mediaContentList = new ArrayList<MediaContentDataEntry>(dataArray.length());
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataItem = dataArray.getJSONObject(i);
            String id = dataItem.getString("id");
            JSONObject userDataObj = dataItem.getJSONObject("user");
            UserData userData = getUserData(userDataObj);
            JSONObject locationDataObj = !dataItem.get("location").equals(JSONObject.NULL) ? dataItem.getJSONObject("location") : null;
            LocationData locationData = getLocationData(locationDataObj);
            ContentData imagesContentData = getContentData(dataItem);
            MediaContentDataEntry entry = new MediaContentDataEntry(id, userData, imagesContentData, locationData);
            mediaContentList.add(entry);
        }

        return new ParseResult(mediaContentList, uri);
    }

    private static UserData getUserData(JSONObject jsonObject) throws JSONException {

        return new UserData(jsonObject.getString("full_name")
                , jsonObject.getString("profile_picture")
                , jsonObject.getString("username"));
    }


    private static LocationData getLocationData(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            return null;
        }
        return new LocationData(jsonObject.getDouble("latitude")
                , jsonObject.getDouble("longitude")
                , jsonObject.getString("name"));
    }


    private static ContentData getContentData(JSONObject jsonObject) throws JSONException {
        long time = jsonObject.getLong("created_time");
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTimeInMillis(time * 1000);

        JSONObject likesObj = jsonObject.getJSONObject("likes");
        int likeCount = likesObj != null ? likesObj.getInt("count") : 0;
        JSONArray tagsArray = jsonObject.getJSONArray("tags");
        String[] tags = null;
        if (tagsArray != null && tagsArray.length() != 0) {
            tags = new String[tagsArray.length()];
            for (int i = 0; i < tagsArray.length(); i++) {
                tags[i] = tagsArray.getString(i);
            }
        }
        String contentUri = null;
        float aspectRatio = 1.0f;
        InstContentType type = getInstContentType(jsonObject.getString("type"));
        if (type == InstContentType.IMAGE) {
            JSONObject imagesObj = jsonObject.getJSONObject("images");
            if (imagesObj != null) {
                JSONObject lowResolutionImage = imagesObj.getJSONObject("low_resolution");
                if (lowResolutionImage != null) {
                    contentUri = lowResolutionImage.getString("url");
                    int width = lowResolutionImage.getInt("width");
                    int height = lowResolutionImage.getInt("height");
                    aspectRatio = ((float) width) / height;
                }
            }
        } else if (type == InstContentType.VIDEO) {
            JSONObject videosObj = jsonObject.getJSONObject("videos");
            if (videosObj != null) {
                JSONObject lowResolutionVideo = videosObj.getJSONObject("low_resolution");
                if (lowResolutionVideo != null) {
                    contentUri = lowResolutionVideo.getString("url");
                    int width = lowResolutionVideo.getInt("width");
                    int height = lowResolutionVideo.getInt("height");
                    aspectRatio = ((float) width) / height;
                }
            }
        }
        String captionText = null;
        if (!jsonObject.get("caption").equals(JSONObject.NULL)) {
            JSONObject captionObj = jsonObject.getJSONObject("caption");
            captionText = captionObj.getString("text");
        }

        return new ContentData(c
                , captionText
                , likeCount
                , tags
                , getInstContentType(jsonObject.getString("type"))
                , contentUri
                , aspectRatio);
    }
}