package com.music.musictagger.mp3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

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

    /**
     * An array of sample (dummy) items.
     */
    public static List<MP3File> ITEMS = new ArrayList<MP3File>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, MP3File> ITEM_MAP = new HashMap<String, MP3File>();

    static {
        // Add 3 sample items.
        addItem(new MP3File("1", "MP3 File"));
        addItem(new MP3File("2", "Item 2"));
        addItem(new MP3File("3", "Item 3"));
    }

    private static void addItem(MP3File item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    public static boolean isValidFile(String path){
    	if(path.toLowerCase().endsWith(".mp3"))
    		return true;
    	return false;
    } 

    /**
     * A dummy item representing a piece of content.
     */
    public static class MP3File {
        public String id;
        public String content;

        public MP3File(String id, String content) {
            this.id = id;
            this.content = content;
        }
        @Override
        public String toString() {
            return content;
        }
    }
}
