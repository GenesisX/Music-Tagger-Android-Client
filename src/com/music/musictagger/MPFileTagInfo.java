package com.music.musictagger;

import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MPFileTagInfo extends Activity {
	public MP3File mp3;
	public AbstractID3v2 id3v2tag;
	public EditText title_text, artist_text, album_text, year_text;
	public Button save_button, cancel_button;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mpfile_info);
		title_text = (EditText) findViewById(R.id.song_edit_text);
		artist_text = (EditText) findViewById(R.id.artist_edit_text);
		album_text = (EditText) findViewById(R.id.album_edit_text);
		year_text = (EditText) findViewById(R.id.year_edit_text);
		save_button = (Button) findViewById(R.id.widget45);
		cancel_button = (Button) findViewById(R.id.widget46);
		save_button = (Button) findViewById(R.id.widget45);
		cancel_button = (Button) findViewById(R.id.widget46);
		
		try {
			mp3 = new MP3File(MPFileDetailActivity.currentMP3.getMusic());
			id3v2tag = mp3.getID3v2Tag();
			title_text.setText(id3v2tag.getSongTitle());
			artist_text.setText(id3v2tag.getLeadArtist());
			album_text.setText(id3v2tag.getAlbumTitle());
			year_text.setText(id3v2tag.getYearReleased());
		} catch (Exception e) {
			title_text.setText("");
			artist_text.setText("");
			album_text.setText("");
			year_text.setText("");
		}

		
		save_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				id3v2tag.setSongTitle(title_text.getText().toString());
				id3v2tag.setLeadArtist(artist_text.getText().toString());
				id3v2tag.setAlbumTitle(album_text.getText().toString());
				id3v2tag.setYearReleased(year_text.getText().toString());
				MPFileDetailActivity.currentMP3.setTitle(title_text.getText().toString());
				MPFileDetailActivity.currentMP3.setAlbum(album_text.getText().toString());
				MPFileDetailActivity.currentMP3.setArtist(artist_text.getText().toString());
				MPFileDetailActivity.currentMP3.setYear(year_text.getText().toString());
				mp3.setID3v1Tag(id3v2tag);
				mp3.setID3v2Tag(id3v2tag);
				
				try {
					mp3.save();
				} catch (TagException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finish();
			}
		});
		
		cancel_button.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				finish();
			}
		});

	}
}