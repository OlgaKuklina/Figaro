package figaro.oklab.com.figaro.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by olgakuklina on 8/3/17.
 */

public class Utils {

    public static final String SHARED_PREF_NAME = "com.oklab.Figaro";
    public static final String DMY_DATE_FORMAT_PATTERN = "dd-MM-yyyy";

    private Utils() {

    }

    public static DateFormat createDateFormatterWithTimeZone(Context context, String pattern) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean timeZone = sharedPref.getBoolean("timezone_switch", true);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        if (timeZone) {
            formatter.setTimeZone(TimeZone.getDefault());
        } else {
            String customTimeZone = sharedPref.getString("timezone_list", TimeZone.getDefault().getID());
            formatter.setTimeZone(TimeZone.getTimeZone(customTimeZone));
        }
        return formatter;
    }

    public static Location getLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            throw new IllegalStateException("no permissions to accsess location");
        }
        List<String> providers = lm.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = lm.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }

        return bestLocation;
    }
}
