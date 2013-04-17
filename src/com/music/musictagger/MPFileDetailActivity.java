package com.music.musictagger;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

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

	// media player
	private MediaPlayer mediaPlayer;
	private boolean isPrepared = false;

	// Controls
	private ImageButton mRepeat, mPlay, mShuffle, mPrev, mNext;
	// Progress
	private SeekBar mProgress;

	// Where we are in the track
	private long mDuration, mLastSeekEventTime, mPosOverride = -1,
			mStartSeekPos = 0;

	private boolean mFromTouch, paused = false, shuffleOn = false,
			repeatOn = false;

	// Handler
	private static final int REFRESH = 1, UPDATEINFO = 2;

	// current mp3 file
	static MP3File currentMP3;
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
			if (currentMP3.getFilename().equals(mp3_id) && iterator.hasNext()) {
				mp3_id = currentMP3.getFilename();
				break;
			}
		}
		// currentMP3.switchTrack();
		// init music info and play current music

		// mediaPlayer = new MediaPlayer();
		isPlaying = true;

		mTrackName = (TextView) findViewById(R.id.audio_player_track);
		mAlbumArtistName = (TextView) findViewById(R.id.audio_player_album_artist);
		mTotalTime = (TextView) findViewById(R.id.audio_player_total_time);
		mCurrentTime = (TextView) findViewById(R.id.audio_player_current_time);
		mAlbumArt = (ImageView) findViewById(R.id.audio_player_album_art);
		mRepeat = (ImageButton) findViewById(R.id.audio_player_repeat);
		mPrev = (ImageButton) findViewById(R.id.audio_player_prev);
		mPlay = (ImageButton) findViewById(R.id.audio_player_play);
		mNext = (ImageButton) findViewById(R.id.audio_player_next);
		mShuffle = (ImageButton) findViewById(R.id.audio_player_shuffle);

		mRepeat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cycleRepeat();
			}
		});
		mShuffle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleShuffle();
			}
		});
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
				synchronized (this) {
					if (isPlaying) {
						isPlaying = false;
						// mPlay.setBackgroundResource(R.drawable.apollo_holo_light_play);
						mPlay.setImageResource(R.drawable.apollo_holo_light_play);
						pause();
					} else {
						isPlaying = true;
						mPlay.setImageResource(R.drawable.apollo_holo_light_pause);
						play();
					}
				}
			}
		});

		mProgress = (SeekBar) findViewById(android.R.id.progress);
		if (mProgress instanceof SeekBar) {
			SeekBar seeker = mProgress;
			mProgress.setOnSeekBarChangeListener(mSeekListener);
		}
		mProgress.setMax(1000);
		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) findViewById(R.id.mpfile_detail)).setText(mItem
					.getParent());

		}

		// init musicInfo when first entered.
		updateMusicInfo();

	}

	private void nextTrack() {
		if (shuffleOn) {
			Random generator = new Random();
			Object[] values = MP3List.ITEM_MAP.values().toArray();
			MP3File randomValue = (MP3File) values[generator
					.nextInt(values.length)];
			while (randomValue.getFilename() == mp3_id) {
				randomValue = (MP3File) values[generator.nextInt(values.length)];
			}
			dispose();
			currentMP3 = randomValue;
			mp3_id = currentMP3.getFilename();
			updateMusicInfo();
		} else {
			Iterator<MP3List.MP3File> iterator = MP3List.ITEMS.iterator();
			while (iterator.hasNext()) {
				MP3File tmp = iterator.next();
				String tempName = tmp.getFilename();
				if (tempName.equals(mp3_id) && iterator.hasNext()) {
					dispose();
					currentMP3 = iterator.next();
					mp3_id = currentMP3.getFilename();
					updateMusicInfo();
					return;
				}
			}
			Toast.makeText(this, "Reached last track", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void prevTrack() {
		if (shuffleOn) {
			Random generator = new Random();
			Object[] values = MP3List.ITEM_MAP.values().toArray();
			MP3File randomValue = (MP3File) values[generator
					.nextInt(values.length)];
			while (randomValue.getFilename() == mp3_id) {
				randomValue = (MP3File) values[generator.nextInt(values.length)];
			}
			dispose();
			currentMP3 = randomValue;
			mp3_id = currentMP3.getFilename();
			updateMusicInfo();
		} else {

			ListIterator<MP3List.MP3File> iterator = MP3List.ITEMS
					.listIterator(MP3List.ITEMS.size());
			while (iterator.hasPrevious()) {
				String tempName = iterator.previous().getFilename();
				if (tempName.equals(currentMP3.getFilename())
						&& iterator.hasPrevious()) {
					dispose();
					currentMP3 = iterator.previous();
					mp3_id = currentMP3.getFilename();
					updateMusicInfo();
					return;
				}
			}
			Toast.makeText(this, "Reached first track", Toast.LENGTH_SHORT)
					.show();
		}
	}

	// needed when new track is loaded
	private void updateMusicInfo() {
		// TODO: add title, artist, cover image, time etc..
		mediaPlayer = new MediaPlayer();
		try {
			FileInputStream fis = new FileInputStream(currentMP3.getMusic());
			FileDescriptor fileDescriptor = fis.getFD();
			mediaPlayer.setDataSource(fileDescriptor);
			mediaPlayer.prepare();
			isPrepared = true;
		} catch (Exception e) {
			// too lazy to do error handling
		}
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer mp) {
						isPrepared = false;
						mediaPlayer.reset();
						if (repeatOn) {
							updateMusicInfo();
						} else {
							nextTrack();
						}
					}
				});
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {

				mProgress.setMax(mediaPlayer.getDuration());
				mProgress.postDelayed(onEverySecond, 1000);
			}
		});
		mTrackName.setText(currentMP3.getTitle());

		mAlbumArtistName.setText(currentMP3.getAlbumArtist());
		byte[] bmap = currentMP3.getArt();
		if (bmap != null) {
			Bitmap bm = BitmapFactory.decodeByteArray(bmap, 0, bmap.length);
			mAlbumArt.setImageBitmap(bm);
		} else {
			mAlbumArt.setImageResource(R.drawable.default_album_art);
		}
		mTotalTime.setText(makeTimeString(currentMP3.getTotalTime()));
		mCurrentTime.setText(makeTimeString(0));
		mProgress.setProgress(0);
		// setSeekBar();
		// mProgress.setMax((int)currentMP3.getTotalTime());

		play();
		isPlaying = true;
	}

	// helper function to format music length
	public static String makeTimeString(long longVal) {
		int hours = (int) longVal / 3600;
		int remainder = (int) longVal - hours * 3600;
		int mins = remainder / 60;
		remainder = remainder - mins * 60;
		int sec = remainder;
		if (sec < 10)
			return Integer.toString(mins) + ":" + "0" + Integer.toString(sec);
		return Integer.toString(mins) + ":" + Integer.toString(sec);
	}

	private Runnable onEverySecond = new Runnable() {
		@Override
		public void run() {
			if (mProgress != null) {
				mProgress.setProgress(mediaPlayer.getCurrentPosition());
				mCurrentTime.post(new Runnable() {
					@Override
					public void run() {
						mCurrentTime.setText(makeTimeString(mediaPlayer
								.getCurrentPosition() / 1000));
					}
				});
			}
			if (isPlaying) {
				mProgress.postDelayed(onEverySecond, 1000);
			}

		}
	};

	// helper function to set seekbar
	public void setSeekBar() {
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(final MediaPlayer mp) {
				// mProgress.setMax(mp.getDuration());
				new Thread(new Runnable() {

					@Override
					public void run() {
						int currentPosition = 0;
						int total = mp.getDuration();
						mProgress.setMax(total);
						while (mediaPlayer != null && currentPosition < total) {
							try {
								Thread.sleep(1000);
								currentPosition = mp.getCurrentPosition();
							} catch (InterruptedException e) {
								return;
							} catch (Exception e) {
								return;
							}
							mProgress.setProgress(currentPosition);
							mCurrentTime.post(new Runnable() {
								@Override
								public void run() {
									mCurrentTime.setText(makeTimeString(mediaPlayer
											.getCurrentPosition() / 1000));
								}
							});
						}
					}
				}).start();
			}
		});
	}

	// seekbar listener
	private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			if (arg2 && isPlaying) {
				mediaPlayer.seekTo(arg1);
				mCurrentTime.setText(makeTimeString(arg1 / 1000));
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

	private void toggleShuffle() {
		if (shuffleOn) {
			mShuffle.setImageResource(R.drawable.apollo_holo_light_shuffle_normal);
			shuffleOn = false;
		} else {
			mShuffle.setImageResource(R.drawable.apollo_holo_light_shuffle_on);
			shuffleOn = true;
		}
	}

	private void cycleRepeat() {
		if (repeatOn) {
			mRepeat.setImageResource(R.drawable.apollo_holo_light_repeat_normal);
			repeatOn = false;
		} else {
			mRepeat.setImageResource(R.drawable.apollo_holo_light_repeat_all);
			repeatOn = true;
		}
	}

	public void switchTracks() {
		mediaPlayer.seekTo(0);
		mediaPlayer.pause();
	}

	public void pause() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}

	public void stop() {
		mediaPlayer.stop();
		synchronized (this) {
			isPrepared = false;
		}
	}

	public void play() {
		if (mediaPlayer.isPlaying()) {
			return;
		}
		try {
			synchronized (this) {
				if (!isPrepared) {
					// mediaPlayer.prepare();
					try {
						FileInputStream fis = new FileInputStream(
								currentMP3.getMusic());
						FileDescriptor fileDescriptor = fis.getFD();
						mediaPlayer.setDataSource(fileDescriptor);
						mediaPlayer.prepare();
						isPrepared = true;
					} catch (Exception e) {
						// too lazy to do error handling
					}
					mediaPlayer
							.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
								public void onCompletion(MediaPlayer mp) {
									isPrepared = false;
									mediaPlayer.reset();
									nextTrack();
								}
							});
				}
				mediaPlayer.start();
			}
		} catch (IllegalStateException ex) {
			ex.printStackTrace();
		}
		// catch(IOException ex){
		// ex.printStackTrace();
		// }
	}

	public void dispose() {
		if (mediaPlayer.isPlaying()) {
			// stop();
		}
		mediaPlayer.stop();
		mediaPlayer.reset();
		mediaPlayer = null;
		isPlaying = false;
		isPrepared = false;
		// mediaPlayer.release();

	}

	@Override
	public void onPause() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this,
					MPFileListActivity.class));
			//		break;
		case R.id.fix_manually:
				Log.w("menu","fix manually");
				Intent nextActivity = new Intent(this, MPFileTagInfo.class);
				startActivity(nextActivity);
				break;
		case R.id.fix_tag:
			Log.w("menu","fix tag");
			break;
		case R.id.find_lyrics:
			Log.w("menu","find lyrics");
			break;
		case R.id.fix_album:
			Log.w("menu","fixalbum");
			break;

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