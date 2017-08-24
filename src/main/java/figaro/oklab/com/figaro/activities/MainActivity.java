package figaro.oklab.com.figaro.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.SupportMapFragment;

import figaro.oklab.com.figaro.R;
import figaro.oklab.com.figaro.data.MediaContentType;
import figaro.oklab.com.figaro.fragments.AuthenticationFragment;
import figaro.oklab.com.figaro.fragments.ContentListFragment;
import figaro.oklab.com.figaro.fragments.MapReadyCallBack;
import figaro.oklab.com.figaro.utils.Utils;

/**
 * Created by olgakuklina on 8/4/17.
 */

public class MainActivity extends AppCompatActivity implements ContentListFragment.OnListFragmentInteractionListener
        , AuthenticationFragment.OAuthListener
        , ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView navigation;
    private SharedPreferences prefs;
    private MapReadyCallBack mapReadycallback;
    private boolean radiusChanged = false;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    MainActivity.this.findViewById(R.id.content_home_fragment_container).setVisibility(View.VISIBLE);
                    MainActivity.this.findViewById(R.id.content_around_fragment_container).setVisibility(View.GONE);
                    MainActivity.this.findViewById(R.id.content_map_fragment_container).setVisibility(View.GONE);
                    return true;
                case R.id.navigation_byCurrentLocation:
                    MainActivity.this.findViewById(R.id.content_around_fragment_container).setVisibility(View.VISIBLE);
                    MainActivity.this.findViewById(R.id.content_home_fragment_container).setVisibility(View.GONE);
                    MainActivity.this.findViewById(R.id.content_map_fragment_container).setVisibility(View.GONE);
                    return true;
                case R.id.navigation_map:
                    MainActivity.this.findViewById(R.id.content_map_fragment_container).setVisibility(View.VISIBLE);
                    MainActivity.this.findViewById(R.id.content_around_fragment_container).setVisibility(View.GONE);
                    MainActivity.this.findViewById(R.id.content_home_fragment_container).setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_sign_out:
                    SharedPreferences prefs = getSharedPreferences(Utils.SHARED_PREF_NAME, 0);
                    SharedPreferences.Editor e = prefs.edit();
                    e.remove("token");
                    e.apply();
                    finish();
                    Intent newIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(newIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("navigation", navigation.getSelectedItemId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (radiusChanged) {
            initFragments();
            radiusChanged = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = this.getSharedPreferences(Utils.SHARED_PREF_NAME, 0);
        String sessionData = prefs.getString("token", null);
        Log.v(TAG, "sessionData  " + sessionData);
        mapReadycallback = new MapReadyCallBack(MainActivity.this);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.v(TAG, "onSharedPreferenceChanged key = " + key);
                if (key.equals("pref_distance")) {
                    radiusChanged = true;
                }
            }
        };
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        if (savedInstanceState == null) {
            ContentListFragment contentListFragment = ContentListFragment.newInstance(MediaContentType.LOCATION_BASED_FEEDS);
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.setRetainInstance(true);
            ContentListFragment myRecentMediaFragment = ContentListFragment.newInstance(MediaContentType.MY_RECENT_FEEDS);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_around_fragment_container, contentListFragment, "contentListFragment")
                    .replace(R.id.content_home_fragment_container, myRecentMediaFragment, "myRecentMediaFragment")
                    .replace(R.id.content_map_fragment_container, supportMapFragment, "supportMapFragment")
                    .commit();
            if (sessionData != null && !sessionData.isEmpty()) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        checkPermisions();
                    }
                });

            } else {
                showAuthDialog();
            }
            navigation.setSelectedItemId(R.id.navigation_home);
        } else {
            navigation.setSelectedItemId(savedInstanceState.getInt("navigation"));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void showAuthDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AuthenticationFragment authFragment = new AuthenticationFragment();
        authFragment.setOAuthlistener(this);
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, authFragment)
                .addToBackStack(null)
                .commit();
    }

    private void initFragments() {
        ((ContentListFragment) getSupportFragmentManager().findFragmentByTag("contentListFragment")).init(Utils.getLocation(this));
        ((ContentListFragment) getSupportFragmentManager().findFragmentByTag("myRecentMediaFragment")).init(Utils.getLocation(this));
        ((SupportMapFragment) getSupportFragmentManager().findFragmentByTag("supportMapFragment")).getMapAsync(mapReadycallback);
    }

    private void checkPermisions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 111);
        } else {
            initFragments();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 111) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            initFragments();
        }
    }

    @Override
    public void onTokenReceived(String token) {
        Log.v(TAG, "Client token received = " + token);
        if (token != null) {
            SharedPreferences prefs = this.getSharedPreferences(Utils.SHARED_PREF_NAME, 0);
            SharedPreferences.Editor e = prefs.edit();
            e.putString("token", token.toString()); // save "value" to the SharedPreferences
            e.apply();
            checkPermisions();
        }
    }

    @Override
    public void onErrorReceived(int errorCode) {
        Log.v(TAG, "Client onErrorReceived = " + errorCode);
    }

    @Override
    public void onListFragmentInteraction() {

    }
}
