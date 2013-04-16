package com.music.musictagger;


import java.util.Iterator;
import java.util.ListIterator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
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
import android.widget.SeekBar.OnSeekBarChangeListener;


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
    			mp3_id = currentMP3.getFilename();
    			updateMusicInfo();
    			break;
    		}
    	}
    }
    
    //needed when new track is loaded
    private void updateMusicInfo(){
    	//TODO: add title, artist, cover image, time etc..
    	mTrackName.setText(currentMP3.getTitle());  
    	mAlbumArtistName.setText(currentMP3.getAlbumArtist());
    	byte[] bmap = currentMP3.getArt();
    	if(bmap != null){
    		Bitmap bm = BitmapFactory.decodeByteArray(bmap, 0, bmap.length);
    		mAlbumArt.setImageBitmap(bm);
    	}else{
    		mAlbumArt.setImageResource(R.drawable.default_album_art);
    	}
    	mTotalTime.setText(makeTimeString(currentMP3.getTotalTime()));
    	mCurrentTime.setText(makeTimeString(0));
    	//setSeekBar();
    	mProgress.setProgress(0);
    	mProgress.setMax((int)currentMP3.getTotalTime());
    	mProgress.setOnSeekBarChangeListener(mSeekListener);    	
    	currentMP3.play();
    	isPlaying = true;
    }
    //helper function to format music length
    public static String makeTimeString(long longVal) {
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int sec = remainder;
        if(sec < 10)
        	return Integer.toString(mins)+":"+"0"+Integer.toString(sec);
    	return Integer.toString(mins)+":"+Integer.toString(sec);
    }
    
    //helper function to set seekbar
    public void setSeekBar(){
    	MediaPlayer mediaPlayer = currentMP3.getMP();
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() 
        {
          @Override
          public void onPrepared(final MediaPlayer mp) 
          {
        	  mProgress.setMax(mp.getDuration());
        	  new Thread(new Runnable() {

        		  @Override
        		  public void run() {
        			  while(mp!=null && mp.getCurrentPosition()<mp.getDuration())
        			  {
        				  mProgress.setProgress(mp.getCurrentPosition());
        				  Message msg=new Message();
        				  int millis = mp.getCurrentPosition();
        				  mCurrentTime.setText(makeTimeString(millis/1000));
        			  }
        		  }
        	  }).start();

          }
        }); 
    }
    
    
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            // TODO Auto-generated method stub
            if (arg2 && isPlaying) {
                //myProgress = oprogress;
                currentMP3.seekTo(arg1*1000);
                mCurrentTime.setText(makeTimeString(arg1));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }
    };
    
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
