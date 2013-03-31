package com.music.musictagger.mp3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import radams.gracenote.webapi.GracenoteWebAPI;

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
