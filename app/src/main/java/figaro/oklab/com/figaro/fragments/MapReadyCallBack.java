package figaro.oklab.com.figaro.fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import figaro.oklab.com.figaro.R;
import figaro.oklab.com.figaro.asynctasks.MediaContentLoader;
import figaro.oklab.com.figaro.data.MediaContentDataEntry;
import figaro.oklab.com.figaro.data.ParseResult;
import figaro.oklab.com.figaro.utils.TransformToCircle;
import figaro.oklab.com.figaro.utils.Utils;

/**
 * Created by olgakuklina on 8/5/17.
 */

public class MapReadyCallBack implements OnMapReadyCallback {


    private static final String TAG = MapReadyCallBack.class.getSimpleName();
    private final AppCompatActivity activity;
    private GoogleMap map;
    private Location currentLocation;
    private String token;
    private ArrayList<Target> targets;

    public MapReadyCallBack(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(TAG, "onMapReady()");
        Bundle bundle = new Bundle();
        bundle.putInt("page", 1);
        map = googleMap;
        SharedPreferences prefs = activity.getSharedPreferences(Utils.SHARED_PREF_NAME, 0);
        token = prefs.getString("token", null);
        turnMyLocationOn();

        currentLocation = Utils.getLocation(activity);
        Log.v(TAG, "currentLocation = " + currentLocation);
        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        }
        activity.getSupportLoaderManager().initLoader(5, new Bundle(), new MediaContentMapLoaderCallbacks());
    }

    public void turnMyLocationOn() {
        if (map != null && (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            map.setMyLocationEnabled(true);
        }
    }

    private void markLocations(List<MediaContentDataEntry> resultData) {
        Log.v(TAG, "markLocations");
        targets = new ArrayList<>(resultData.size());
        for (final MediaContentDataEntry entry : resultData) {
            if (entry.getLocationData() != null) {
                Log.v(TAG, "getLatitude = " + entry.getLocationData().getLatitude());
                final LatLng position = new LatLng(entry.getLocationData().getLatitude(), entry.getLocationData().getLongitude());

                if (entry.getContentData().getContentUri() == null) {
                    MarkerOptions options = new MarkerOptions().position(position).title(entry.getLocationData().getLocationName());
                    map.addMarker(options);
                } else {
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.v(TAG, "onBitmapLoaded");
                            BitmapDescriptor desc = BitmapDescriptorFactory.fromBitmap(bitmap);
                            MarkerOptions options = new MarkerOptions().position(position).title(entry.getLocationData().getLocationName()).icon(desc);
                            map.addMarker(options);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Log.v(TAG, "error onBitmapFailed");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };
                    targets.add(target);
                    Picasso.with(activity).load(entry.getContentData().getContentUri()).resize(100, 100).transform(new TransformToCircle()).into(target);
                }

            }
        }
    }

    private class MediaContentMapLoaderCallbacks implements LoaderManager.LoaderCallbacks<ParseResult> {

        @Override
        public Loader<ParseResult> onCreateLoader(int id, Bundle args) {
            Log.v(TAG, "onCreateLoader " + args);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
            String distance = sharedPref.getString("pref_distance", "5000");
            Log.v(TAG, "pref_distance = " + distance);
            String uriConnect = activity.getString(R.string.url_feeds, token, currentLocation.getLatitude(), currentLocation.getLongitude(), distance);
            return new MediaContentLoader(activity, uriConnect);
        }

        @Override
        public void onLoadFinished(Loader<ParseResult> loader, ParseResult result) {
            if (result.getMediaContentList() != null && !result.getMediaContentList().isEmpty()) {
                activity.getSupportLoaderManager().destroyLoader(loader.getId());
                markLocations(result.getMediaContentList());
                return;
            }

        }

        @Override
        public void onLoaderReset(Loader<ParseResult> loader) {
            Log.v(TAG, "onLoaderReset");
        }
    }


}



