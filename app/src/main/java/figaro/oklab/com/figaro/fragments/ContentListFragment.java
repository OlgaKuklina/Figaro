package figaro.oklab.com.figaro.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import figaro.oklab.com.figaro.R;
import figaro.oklab.com.figaro.adapters.MediaContentRecyclerViewAdapter;
import figaro.oklab.com.figaro.asynctasks.MediaContentLoader;
import figaro.oklab.com.figaro.data.MediaContentType;
import figaro.oklab.com.figaro.data.ParseResult;
import figaro.oklab.com.figaro.utils.Utils;


/**
 * Created by olgakuklina on 8/4/17.
 */


public class ContentListFragment extends Fragment {

    private static final String TAG = ContentListFragment.class.getSimpleName();
    private static final String MEDIA_CONTENT_TYPE = "mediaContentType";
    private int columnCount = 1;
    private Location location;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MediaContentRecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private MediaContentType mediaContentType;
    private String token;
    private boolean loading = false;
    private String nextLoadingUri = null;

    public ContentListFragment() {
    }

    public static ContentListFragment newInstance(MediaContentType type) {
        ContentListFragment fragment = new ContentListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(MEDIA_CONTENT_TYPE, type.getMatchingMediaContentType());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");
        setRetainInstance(true);

        if (getArguments() != null) {
            mediaContentType = MediaContentType.getMediaContentType(getArguments().getInt(MEDIA_CONTENT_TYPE));
        }
        adapter = new MediaContentRecyclerViewAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_list, container, false);
        columnCount = getResources().getInteger(R.integer.column_count);
        // Set the adapter
        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.items_list_recycler_view);
        if (columnCount <= 1) {
            layoutManager = new LinearLayoutManager(context);
        } else {
            layoutManager = new GridLayoutManager(context, columnCount);
        }
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new MediaContentItemsListOnScrollListner());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                adapter.resetAllData();
                nextLoadingUri = null;
                loading = false;
                getLoaderManager().initLoader(mediaContentType.getMatchingMediaContentType(), new Bundle(), new MediaContentLoaderCallbacks());
            }
        });

        this.swipeRefreshLayout.setColorSchemeColors(this.getResources().getIntArray(R.array.swipe_to_refresh_progress_colors));

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(TAG, "onAttach ");
    }

    public void init(Location location) {
        Log.v(TAG, "Init Loader, location = " + location);
        Log.v(TAG, "Init Loader, context = " + getContext());
        this.location = location;
        SharedPreferences prefs = getContext().getSharedPreferences(Utils.SHARED_PREF_NAME, 0);
        token = prefs.getString("token", null);
        getLoaderManager().initLoader(mediaContentType.getMatchingMediaContentType(), new Bundle(), new MediaContentLoaderCallbacks());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction();
    }

    private class MediaContentLoaderCallbacks implements LoaderManager.LoaderCallbacks<ParseResult> {

        @Override
        public Loader<ParseResult> onCreateLoader(int id, Bundle args) {
            String uriConnect;
            Log.v(TAG, "onCreateLoader token = " + token);
            if (mediaContentType == MediaContentType.LOCATION_BASED_FEEDS) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String distance = sharedPref.getString("pref_distance", "5000");
                Log.v(TAG, "pref_distance = " + distance);
                uriConnect = getContext().getString(R.string.url_feeds, token, location.getLatitude(), location.getLongitude(), distance);
            } else if (mediaContentType == MediaContentType.MY_RECENT_FEEDS) {

                if (nextLoadingUri != null) {
                    uriConnect = nextLoadingUri;
                } else {
                    uriConnect = getContext().getString(R.string.url_home, token);
                }

            } else {
                throw new IllegalArgumentException();
            }

            swipeRefreshLayout.setRefreshing(true);
            return new MediaContentLoader(getContext(), uriConnect);

        }

        @Override
        public void onLoadFinished(Loader<ParseResult> loader, ParseResult parseResult) {
            Log.v(TAG, "onLoadFinished " + parseResult);
            adapter.add(parseResult.getMediaContentList());
            nextLoadingUri = parseResult.getUri();
            swipeRefreshLayout.setRefreshing(false);
            getLoaderManager().destroyLoader(loader.getId());
            loading = false;
        }

        @Override
        public void onLoaderReset(Loader<ParseResult> loader) {
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
    }

    private class MediaContentItemsListOnScrollListner extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastScrollPosition = layoutManager.findLastVisibleItemPosition();
            int itemsCount = adapter.getItemCount();
            Log.v(TAG, "lastScrollPosition = " + lastScrollPosition + ", itemsCount = " + itemsCount + ", loading = " + loading + ", nextLoadingUri = " + nextLoadingUri);
            if (lastScrollPosition == itemsCount - 1 && !loading && nextLoadingUri != null) {

                loading = true;
                getLoaderManager().initLoader(mediaContentType.getMatchingMediaContentType(), new Bundle(), new MediaContentLoaderCallbacks());
            }
        }
    }
}
