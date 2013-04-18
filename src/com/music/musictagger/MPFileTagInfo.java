package com.music.musictagger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MPFileTagInfo extends Activity {
	private File mp3;
	private MusicMetadataSet dataset = null;
	private EditText title_text, artist_text, album_text, year_text;
	private Button save_button, cancel_button;
	private String parent, filename;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mpfile_info);
		// initialize EditTexts and Buttons
		
		title_text = (EditText) findViewById(R.id.song_edit_text);
		//debug
		//System.out.println(R);
		artist_text = (EditText) findViewById(R.id.artist_edit_text);
		album_text = (EditText) findViewById(R.id.album_edit_text);
		year_text = (EditText) findViewById(R.id.year_edit_text);
		save_button = (Button) findViewById(R.id.widget45);
		cancel_button = (Button) findViewById(R.id.widget46);
		parent = MPFileDetailActivity.currentMP3.getParent();
		filename = MPFileDetailActivity.currentMP3.getFilename();
		mp3 = new File(parent, filename);
		
		try
		{
			dataset = new MyID3().read(mp3);
			IMusicMetadata data = dataset.getSimplified();
			title_text.setText(data.getSongTitle());
			artist_text.setText(data.getArtist());
			album_text.setText(data.getAlbum());
			year_text.setText(data.getYear());
		} catch ( IOException e) {
			title_text.setText("");
			artist_text.setText("");
			album_text.setText("");
			year_text.setText("");
		}
		
		save_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MusicMetadata set=new MusicMetadata("new");
				set.setSongTitle(title_text.getText().toString());
				set.setAlbum(album_text.getText().toString());
				set.setArtist(artist_text.getText().toString());
				set.setYear(year_text.getText().toString());
				MPFileDetailActivity.currentMP3.setName(title_text.getText().toString()+".mp3");
				MPFileDetailActivity.currentMP3.setTitle(title_text.getText().toString());
				MPFileDetailActivity.currentMP3.setAlbum(album_text.getText().toString());
				MPFileDetailActivity.currentMP3.setArtist(artist_text.getText().toString());
				MPFileDetailActivity.currentMP3.setYear(year_text.getText().toString());
				
				
				// create a new file with same name
				File temp = new File( parent, title_text.getText().toString() + ".mp3" );
				String dstpath = parent + "/" + filename;
				
				try {
					temp.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				try {
					new MyID3().write(mp3, temp, dataset, set);
					mp3.delete();
					temp.renameTo(new File(dstpath));
					MPFileDetailActivity.currentMP3.setMusic(parent, set.getSongTitle()+".mp3");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ID3WriteException e) {
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
	
	@Override
	protected void onResume() {
		super.onResume();
		try
		{
			dataset = new MyID3().read(mp3);
			IMusicMetadata data = dataset.getSimplified();
			title_text.setText(data.getSongTitle());
			artist_text.setText(data.getArtist());
			album_text.setText(data.getAlbum());
			year_text.setText(data.getYear());
		} catch ( IOException e) {
			title_text.setText("");
			artist_text.setText("");
			album_text.setText("");
			year_text.setText("");
		}
	}

}