package figaro.oklab.com.figaro.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import figaro.oklab.com.figaro.R;
import figaro.oklab.com.figaro.data.InstContentType;
import figaro.oklab.com.figaro.data.MediaContentDataEntry;
import figaro.oklab.com.figaro.utils.Utils;

/**
 * Created by olgakuklina on 8/4/17.
 */


public class MediaContentRecyclerViewAdapter extends RecyclerView.Adapter<MediaContentRecyclerViewAdapter.MediaContentViewHolder> {

    private static final String TAG = MediaContentRecyclerViewAdapter.class.getSimpleName();

    private final ArrayList<MediaContentDataEntry> mediaContentDataEntryList = new ArrayList<>(1000);
    private final Context context;

    public MediaContentRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MediaContentRecyclerViewAdapter.MediaContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_listitem, parent, false);
        int columnCount = context.getResources().getInteger(R.integer.column_count);
        return new MediaContentViewHolder(v, parent.getMeasuredWidth() / columnCount);
    }

    @Override
    public void onBindViewHolder(MediaContentRecyclerViewAdapter.MediaContentViewHolder holder, int position) {
        MediaContentDataEntry entry = mediaContentDataEntryList.get(position);
        holder.populateMediaContentViewData(entry);
    }

    @Override
    public void onViewAttachedToWindow(MediaContentViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.videoView.getVisibility() == View.VISIBLE) {
            holder.videoView.start();
        }
    }

    @Override
    public int getItemCount() {
        return mediaContentDataEntryList.size();
    }

    public void add(List<MediaContentDataEntry> entryList) {
        if (entryList == null || entryList.isEmpty()) {
            Log.v(TAG, "MediaContentDataEntryList is empty");
            return;
        }
        mediaContentDataEntryList.addAll(entryList);
        notifyDataSetChanged();
    }

    public void resetAllData() {
        mediaContentDataEntryList.clear();
        notifyDataSetChanged();
    }

    public class MediaContentViewHolder extends RecyclerView.ViewHolder {

        private final ImageView personalProfImage;
        private final TextView login;
        private final TextView username;
        private final TextView caption;
        private final TextView createdTime;
        private final TextView location;
        private final TextView likes;
        private final TextView tags;
        private final ImageView imageView;
        private final VideoView videoView;
        private final int parentWidth;

        public MediaContentViewHolder(View v, int parentWidth) {
            super(v);
            personalProfImage = (ImageView) v.findViewById(R.id.avatar);
            username = (TextView) v.findViewById(R.id.username);
            login = (TextView) v.findViewById(R.id.login);
            caption = (TextView) v.findViewById(R.id.caption);
            createdTime = (TextView) v.findViewById(R.id.created_time);
            location = (TextView) v.findViewById(R.id.location);
            likes = (TextView) v.findViewById(R.id.likes);
            tags = (TextView) v.findViewById(R.id.tags);
            imageView = (ImageView) v.findViewById(R.id.img_content_uri);
            videoView = (VideoView) v.findViewById(R.id.video_content_uri);
            this.parentWidth = parentWidth;
        }

        private void populateMediaContentViewData(MediaContentDataEntry mediaContentDataEntry) {
            username.setText(mediaContentDataEntry.getUserData().getAuthorName());
            login.setText(mediaContentDataEntry.getUserData().getLogin());
            if (mediaContentDataEntry.getContentData().getCaption() != null) {
                caption.setText(mediaContentDataEntry.getContentData().getCaption());
                caption.setVisibility(View.VISIBLE);
            } else {
                caption.setVisibility(View.GONE);
            }

            likes.setText(Integer.toString(mediaContentDataEntry.getContentData().getLikes()));
            if (mediaContentDataEntry.getLocationData() != null) {
                location.setText(mediaContentDataEntry.getLocationData().getLocationName());
                location.setVisibility(View.VISIBLE);
            } else {
                location.setVisibility(View.GONE);
            }

            StringBuilder tag = new StringBuilder();
            if (mediaContentDataEntry.getContentData().getTags() != null) {
                for (int i = 0; i < mediaContentDataEntry.getContentData().getTags().length; i++) {
                    tag.append(mediaContentDataEntry.getContentData().getTags()[i]);
                    if (i != mediaContentDataEntry.getContentData().getTags().length - 1) {
                        tag.append(",");
                    }

                }
            }
            tags.setText(tag.toString());

            DateFormat formatter = Utils.createDateFormatterWithTimeZone(context, Utils.DMY_DATE_FORMAT_PATTERN);
            Calendar date = mediaContentDataEntry.getContentData().getCreatedTime();
            createdTime.setText(formatter.format(date.getTime()));

            Picasso pic = Picasso.with(context);
            if (mediaContentDataEntry.getUserData().getAuthorAvatarURL() == null || mediaContentDataEntry.getUserData().getAuthorAvatarURL().isEmpty()) {
                pic.load(R.drawable.ic_launcher)
                        .fit().centerCrop()
                        .into(personalProfImage);
            } else {
                pic.load(mediaContentDataEntry.getUserData().getAuthorAvatarURL())
                        .fit().centerCrop()
                        .error(R.drawable.ic_launcher)
                        .into(personalProfImage);
            }
            String mediaContentUri = mediaContentDataEntry.getContentData().getContentUri();
            int height = (int) (parentWidth / mediaContentDataEntry.getContentData().getAspectRatio());

            if (mediaContentDataEntry.getContentData().getContentType() == InstContentType.IMAGE) {
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, height));
                Log.v(TAG, "mediaContentUri = " + mediaContentUri);
                if (mediaContentUri == null || mediaContentUri.isEmpty()) {
                    pic.load(R.drawable.ic_launcher)
                            .into(imageView);
                } else {
                    pic.load(mediaContentUri)
                            .error(R.drawable.ic_launcher)
                            .into(imageView);
                }
            } else if (mediaContentDataEntry.getContentData().getContentType() == InstContentType.VIDEO) {
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setLayoutParams(new LinearLayout.LayoutParams(parentWidth, height));
                String vContentUri = mediaContentDataEntry.getContentData().getContentUri();
                Uri uri = Uri.parse(vContentUri);
                videoView.setVideoURI(uri);
                videoView.start();
            }
        }
    }

}





