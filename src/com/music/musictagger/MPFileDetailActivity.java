package com.music.musictagger;


import java.util.Iterator;
import java.util.ListIterator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.music.musictagger.mp3.MP3List;
import com.music.musictagger.mp3.MP3List.MP3File;

public class MPFileDetailActivity extends FragmentActivity {
	private String mp3_id;
	public static final String ARG_ITEM_ID = "item_id";

	private boolean isPlaying;
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

	//current mp3 file
	private static MP3File currentMP3;
	/**
	 * The dummy content this fragment is presenting.
	 */
	private MP3List.MP3File mItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_mpfile_detail);
        Intent intent = getIntent();
        mp3_id = intent.getStringExtra(MPFileDetailFragment.ARG_ITEM_ID);
    	Iterator<MP3List.MP3File> iterator = MP3List.ITEMS.iterator();
    	while (iterator.hasNext()) {
    		currentMP3 = iterator.next();
    		if(currentMP3.getFilename().equals(mp3_id) && iterator.hasNext()){
    			mp3_id = currentMP3.getFilename();
    			break;
    		}
    	} 
    	//currentMP3.switchTrack();
        //init music info and play current music
        												

        isPlaying = true;
        
        mTrackName = (TextView)findViewById(R.id.audio_player_track);
        mAlbumArtistName = (TextView)findViewById(R.id.audio_player_album_artist);      
        mTotalTime = (TextView)findViewById(R.id.audio_player_total_time);
        mCurrentTime = (TextView)findViewById(R.id.audio_player_current_time);
        mAlbumArt = (ImageView)findViewById(R.id.audio_player_album_art);
        mRepeat = (ImageButton)findViewById(R.id.audio_player_repeat);
        mPrev = (ImageButton)findViewById(R.id.audio_player_prev);
        mPlay = (ImageButton)findViewById(R.id.audio_player_play);
        mNext = (ImageButton)findViewById(R.id.audio_player_next);
        mShuffle = (ImageButton)findViewById(R.id.audio_player_shuffle);
       
        mPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	prevTrack();
            }
        });
        mNext.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
            	nextTrack();
            }
        });
        mPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
    			synchronized(this){
    				if(isPlaying){
    					isPlaying = false;
    					//mPlay.setBackgroundResource(R.drawable.apollo_holo_light_play);
    					mPlay.setImageResource(R.drawable.apollo_holo_light_play);
    					currentMP3.pause();
    				} else{
    					isPlaying = true;
    					mPlay.setImageResource(R.drawable.apollo_holo_light_pause);
    					currentMP3.play();
    				}
    			}
            }
        });

       


        mProgress = (SeekBar)findViewById(android.R.id.progress);
        if (mProgress instanceof SeekBar) {
            SeekBar seeker = mProgress;
            //seeker.setOnSeekBarChangeListener(mSeekListener);
        }
        mProgress.setMax(1000);
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) findViewById(R.id.mpfile_detail)).setText(mItem.getParent() );

        }
        
        //init musicInfo when first entered.
        updateMusicInfo();
        
        // Show the Up button in the action bar.
        //getActionBar().setDisplayHomeAsUpEnabled(true);

//        if (savedInstanceState == null) {
//            // Create the detail fragment and add it to the activity
//            // using a fragment transaction.
//            Bundle arguments = new Bundle();
//            arguments.putString(MPFileDetailFragment.ARG_ITEM_ID,
//                    getIntent().getStringExtra(MPFileDetailFragment.ARG_ITEM_ID));
//            MPFileDetailFragment fragment = new MPFileDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.mpfile_detail_container, fragment)
//                    .commit();
//        }
    }
    private void nextTrack(){
    	Iterator<MP3List.MP3File> iterator = MP3List.ITEMS.iterator();
    	while (iterator.hasNext()) {
    		String tempName = iterator.next().getFilename();
    		if(tempName.equals(mp3_id) && iterator.hasNext()){
    			currentMP3.dispose();
    			currentMP3 = iterator.next();
    			mp3_id = currentMP3.getFilename();
    			//mp3_id = currentMP3.getFilename();
    			updateMusicInfo();
    			break;
    		}
    	}
    }
    private void prevTrack(){
    	ListIterator<MP3List.MP3File> iterator = MP3List.ITEMS.listIterator(MP3List.ITEMS.size());
    	while (iterator.hasPrevious()) {
    		String tempName = iterator.previous().getFilename();
    		if(tempName.equals(currentMP3.getFilename()) && iterator.hasPrevious()){
    			currentMP3.dispose();
    			currentMP3 = iterator.previous();
    			updateMusicInfo();
    			break;
    		}
    	}
    }
    
    //needed when new track is loaded
    private void updateMusicInfo(){
    	//TODO: add title, artist, cover image, time etc..
    	mTrackName.setText(currentMP3.getFilename());  
    	currentMP3.play();
    	isPlaying = true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, MPFileListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.player_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
   
}
