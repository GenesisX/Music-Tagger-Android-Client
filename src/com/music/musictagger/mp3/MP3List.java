package com.music.musictagger.mp3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.TextView;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MP3List {
	public static List<MP3File> ITEMS = new ArrayList<MP3File>();
	public static Map<String, MP3File> ITEM_MAP = new HashMap<String, MP3File>();
	private TextView liststatus;

	// static {
	// // Add 3 sample items.
	// searchDir("/sdcard");
	// searchDir("/sdcard1");
	// }

	private static void addItem(MP3File item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.getTitle(), item);
	}

	public static boolean isValidFile(String path) {
		if (path.toLowerCase().endsWith(".mp3"))
			return true;
		return false;
	}

	public static void searchDir(String searchpath) {
		String[] filenames;
		String fileparent;
		File folder = new File(searchpath);
		if (folder.exists()) {
			if (folder.isDirectory()) {
				filenames = folder.list();
				fileparent = folder.getAbsolutePath();
			} else {
				filenames = new String[] { folder.getAbsolutePath() };
				fileparent = folder.getParent();
			}
			boolean hasFiles = false;
			for (int i = 0; i < filenames.length; ++i) {
				if (isValidFile(filenames[i])
						&& !(ITEM_MAP.containsKey(filenames))) {
					addItem(new MP3File(filenames[i], fileparent));
				}
			}
			if (hasFiles) {

			} else {
				// updateStatus("Files not Supported.we currently support only mp3 files.",true);
			}

		}
		// else
		// Toast.makeText(getApplicationContext(), "No File/Folder",
		// Toast.LENGTH_LONG).show();
	}

	public static class MP3File implements OnCompletionListener {
		private TextView status;
		private String title, fileparent;
		private String filename, artist, album, track, year;
		private long totalTime;
		private File mp3;
		private byte[] album_art = null;

		public MP3File(String filename, String fileparent ) {
			this.filename = filename;
			this.fileparent = fileparent;
			mp3 = new File(fileparent + "/" + filename);
			// use of mp3agic to retrive metadata etc

			initialize();
		}

		private void initialize() {
			// use mp3agic to retrieve tag info
			try {
				Mp3File mp3file = new Mp3File(mp3.getAbsolutePath());
				totalTime = mp3file.getLengthInSeconds();
				if (mp3file.hasId3v1Tag()) {
					getID3v1Tag(mp3file);
				} else if (mp3file.hasId3v2Tag()) {
					getID3v2Tag(mp3file);
				}
			} catch (UnsupportedTagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/* use mp3agic to retrive id3v1 info* */
		private void getID3v1Tag(Mp3File mp3file) {

			ID3v1 id3v1Tag = mp3file.getId3v1Tag();
			this.track = id3v1Tag.getTrack();
			this.year = id3v1Tag.getYear();
			this.artist = id3v1Tag.getArtist();
			this.title = id3v1Tag.getTitle();
			this.album = id3v1Tag.getAlbum();
		}

		private void getID3v2Tag(Mp3File mp3file) {

			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
			this.track = id3v2Tag.getTrack();
			this.year = id3v2Tag.getYear();
			this.artist = id3v2Tag.getArtist();
			this.title = id3v2Tag.getTitle();
			this.album = id3v2Tag.getAlbum();
			this.album_art = id3v2Tag.getAlbumImage();
			if (this.album_art != null) {
				// TODO: handle here if image exist
			}
		}

		public File getMusic() {
			return mp3;
		}

		public String getParent() {
			return this.fileparent;
		}

		public String getFilename() {
			return this.filename;
		}

		public String getTitle() {
			return this.title;
		}
		
		public String getArtist() {
			return this.artist;
		}
		
		public String getAlbum() {
			return this.album;
		}
		
		public String getYear() {
			return this.year;
		}

		public long getTotalTime() {
			return this.totalTime;
		}

		public String getAlbumArtist() {
			return this.album + "-" + this.artist;
		}

		public byte[] getArt() {
			return this.album_art;
		}
		
		public void setMusic(String parent, String name) {
			this.mp3 = new File(parent, name);
		}

		public void setParent(String fileparent) {
			this.fileparent = fileparent;
		}

		public void setName(String name) {
			this.filename = name;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		
		public void setArtist(String artist) {
			this.artist = artist;
		}
		
		public void setAlbum(String album) {
			this.album = album;
		}
		
		public void setYear(String year) {
			this.year = year;
		}

		@Override
		public String toString() {
			return title + "\n" ;
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub

		}
	}
}
