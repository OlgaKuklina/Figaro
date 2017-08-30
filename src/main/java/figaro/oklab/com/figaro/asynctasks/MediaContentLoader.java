package figaro.oklab.com.figaro.asynctasks;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import figaro.oklab.com.figaro.data.MediaContentDataEntry;
import figaro.oklab.com.figaro.data.ParseResult;
import figaro.oklab.com.figaro.parsers.MediaContentParser;

/**
 * Created by olgakuklina on 8/3/17.
 */

public class MediaContentLoader extends AsyncTaskLoader<ParseResult> {
    private static final String TAG = MediaContentLoader.class.getSimpleName();
    private final String uri;

    public MediaContentLoader(Context context, String uri) {
        super(context);
        this.uri = uri;
    }

    @Override
    public ParseResult loadInBackground() {
        try {

            String connectUri = uri;
            HttpURLConnection connect = (HttpURLConnection) new URL(connectUri).openConnection();
            connect.setRequestMethod("GET");
            Log.v(TAG, "connectUri = " + connectUri);
            connect.connect();
            int responseCode = connect.getResponseCode();

            Log.v(TAG, "responseCode = " + responseCode);
            if (responseCode != 200) {
                return null;
            }
            InputStream inputStream = connect.getInputStream();
            String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Log.v(TAG, " response = " + response);
            JSONObject jObj = new JSONObject(response);
            return MediaContentParser.parse(jObj);

        } catch (Exception e) {
            Log.e(TAG, "Get user feeds failed", e);
            return new ParseResult(Collections.<MediaContentDataEntry>emptyList(), null);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
