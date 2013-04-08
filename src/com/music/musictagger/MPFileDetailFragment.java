package com.music.musictagger;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andrew.apollo.R;
import com.andrew.apollo.ui.widgets.RepeatingImageButton;
import com.andrew.apollo.utils.MusicUtils;
import com.andrew.apollo.utils.ThemeUtils;
import com.music.musictagger.mp3.MP3List;

/**
 * A fragment representing a single MP3 File detail screen.
 * This fragment is either contained in a {@link MPFileListActivity}
 * in two-pane mode (on tablets) or a {@link MPFileDetailActivity}
 * on handsets.
 */
public class MPFileDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    // Track, album, and artist name
    private TextView mTrackName, mAlbumArtistName;

    // Total and current time
    private TextView mTotalTime, mCurrentTime;

    // Album art
    private ImageView mAlbumArt;

    // Controls
    private ImageButton mRepeat, mPlay, mShuffle, mPrev, mNext;
 // Progress
    private SeekBar mProgress;

    // Where we are in the track
    private long mDuration, mLastSeekEventTime, mPosOverride = -1, mStartSeekPos = 0;

    private boolean mFromTouch, paused = false;

    // Handler
    private static final int REFRESH = 1, UPDATEINFO = 2;

    /**
     * The dummy content this fragment is presenting.
     */
    private MP3List.MP3File mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MPFileDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = MP3List.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mpfile_detail, container, false);
        mTrackName = (TextView)root.findViewById(R.id.audio_player_track);
        mTrackName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //tracksBrowser();
            }
        });
        mAlbumArtistName = (TextView)root.findViewById(R.id.audio_player_album_artist);
        mAlbumArtistName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //tracksBrowserArtist();
            }
        });

        mTotalTime = (TextView)root.findViewById(R.id.audio_player_total_time);
        mCurrentTime = (TextView)root.findViewById(R.id.audio_player_current_time);

        mAlbumArt = (ImageView)root.findViewById(R.id.audio_player_album_art);

        mRepeat = (ImageButton)root.findViewById(R.id.audio_player_repeat);
        mPrev = (ImageButton)root.findViewById(R.id.audio_player_prev);
        mPlay = (ImageButton)root.findViewById(R.id.audio_player_play);
        mNext = (ImageButton)root.findViewById(R.id.audio_player_next);
        mShuffle = (ImageButton)root.findViewById(R.id.audio_player_shuffle);

        mRepeat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cycleRepeat();
            }
        });

        mPrev.setRepeatListener(mRewListener, 260);
        mPrev.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MusicUtils.mService == null)
                    return;
                try {
                    if (MusicUtils.mService.position() < 2000) {
                        MusicUtils.mService.prev();
                    } else {
                        MusicUtils.mService.seek(0);
                        MusicUtils.mService.play();
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doPauseResume();
            }
        });

        mNext.setRepeatListener(mFfwdListener, 260);
        mNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MusicUtils.mService == null)
                    return;
                try {
                    MusicUtils.mService.next();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mShuffle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleShuffle();
            }
        });

        mProgress = (SeekBar)root.findViewById(android.R.id.progress);
        if (mProgress instanceof SeekBar) {
            SeekBar seeker = mProgress;
            seeker.setOnSeekBarChangeListener(mSeekListener);
        }
        mProgress.setMax(1000);

        // Theme chooser
        ThemeUtils.setImageButton(getActivity(), mPrev, "apollo_previous");
        ThemeUtils.setImageButton(getActivity(), mNext, "apollo_next");
        ThemeUtils.setProgessDrawable(getActivity(), mProgress, "apollo_seekbar_background");
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.mpfile_detail)).setText(mItem.getParent() );

        }

        return root;
    }
}
