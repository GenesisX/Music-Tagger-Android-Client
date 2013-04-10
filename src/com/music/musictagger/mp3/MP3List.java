package com.music.musictagger.mp3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNOperationStatusChanged;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;
import com.gracenote.mmid.MobileSDK.GNStatus;


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
//    static {
//        // Add 3 sample items.
//        searchDir("/sdcard");
//        searchDir("/sdcard1");
//    }

    private static void addItem(MP3File item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getTitle(), item);
    }
    public static boolean isValidFile(String path){
    	if(path.toLowerCase().endsWith(".mp3"))
    		return true;
    	return false;
    } 
    
    public static void searchDir(String searchpath){
    	String[] filenames;
    	String fileparent;
    	File folder = new File(searchpath);
		if (folder.exists()) {
			if(folder.isDirectory()){
				filenames = folder.list();
				fileparent = folder.getAbsolutePath();
			}
			else{
				filenames=new String[]{folder.getAbsolutePath()};
				fileparent = folder.getParent();
			}
			boolean hasFiles = false;
			for (int i=0;i<filenames.length;++i) {
				 	if(isValidFile(filenames[i])&&!(ITEM_MAP.containsKey(filenames))){
						addItem(new MP3File(filenames[i],fileparent));
					}
			}
			if(hasFiles){

			}
			else
			{
		//		updateStatus("Files not Supported.we currently support only mp3 files.",true);
			}
			
		}
		//else
		//	Toast.makeText(getApplicationContext(), "No File/Folder", Toast.LENGTH_LONG).show();
	}
    	
    


    public static class MP3File implements OnCompletionListener {
    	private TextView status;
    	private String filename,fileparent;
        private String title,artist,album;
        private File mp3;
        private boolean isPrepared = false;
        private MediaPlayer mediaPlayer;
        private byte[] album_art;
        
        public MP3File(String filename, String fileparent) {
            this.filename = filename;
            this.fileparent = fileparent;
            mp3 = new File(fileparent+"/"+filename);
            mediaPlayer = new MediaPlayer();
    		initialize();
        }

        private void initialize (){
        	try{
    			FileInputStream fis = new FileInputStream(mp3);
    			FileDescriptor fileDescriptor = fis.getFD();
    			mediaPlayer.setDataSource(fileDescriptor);
    			mediaPlayer.prepare();
    			isPrepared = true;
    			mediaPlayer.setOnCompletionListener(this);
    		} catch(Exception ex){
    			throw new RuntimeException("Couldn't load music, uh oh!");
    		}
        }
        public File getMusic(){
        	return mp3;
        }
        public String getFilename(){
        	return filename;
        }
        public String getTitle(){
        	return this.title;
        }
        public void setTitle(String title){
        	this.title = title;
        }
        public void setName(String path){
        	this.filename = path;
        }
        public String getParent(){
        	return this.fileparent;
        }
        public void setParent(String parent){
        	this.fileparent = parent;
        }
        
        @Override
        public String toString() {
            return filename;
        }
        

        public void switchTracks(){
    		mediaPlayer.seekTo(0);
    		mediaPlayer.pause();
    	}
        
        public void pause(){
        	if(mediaPlayer.isPlaying()){
        		mediaPlayer.pause();
        	}
        }
        
        public void stop(){
        	mediaPlayer.stop();
    		synchronized(this){
    			isPrepared = false;
    		}
        }
        public void play(){
        	if(mediaPlayer.isPlaying()){
    			return;
    		}
    		try{
    			synchronized(this){
    				if(!isPrepared){
    					//mediaPlayer.prepare();
    					initialize();
    				}
    				mediaPlayer.start();
    			}
    		} catch(IllegalStateException ex){
    			ex.printStackTrace();
    		} 
    			//catch(IOException ex){
//    			ex.printStackTrace();
//    		}	
        }

        public void dispose() {
    		if(mediaPlayer.isPlaying()){
    			//stop();
    		}
    		mediaPlayer.stop();
    		mediaPlayer.reset();
    		isPrepared = false;
    		//mediaPlayer.release();
    		
    	}
		@Override
		public void onCompletion(MediaPlayer arg0) {
			synchronized(this){
				isPrepared = false;
				mediaPlayer.reset();
	    		//mediaPlayer.release();
	    		
			}
		}
    }
}
