package com.music.musictagger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;
import com.music.musictagger.mp3.MP3List;
import com.music.musictagger.mp3.MP3List.MP3File;

/**
 * An activity representing a list of MP3 Files. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link MPFileDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MPFileListFragment} and the item details (if present) is a
 * {@link MPFileDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link MPFileListFragment.Callbacks} interface to listen for item selections.
 */
public class MPFileListActivity extends FragmentActivity implements
		MPFileListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
/*	private GNConfig config;
	private File mp3;
	private String filename, fileparent;
	private String title, artist, album;
	private RecognizeFileOperation op;
	private GNSearchResult result;
	private GNSearchResponse bestResponse;

	// get tag info from a given MP3File
	private void fix() {
		filename = "ccc.mp3";
		fileparent = "/sdcard";
		mp3 = new File("/sdcard/ccc.mp3");
		op = new RecognizeFileOperation();
		op.GNResultReady(result);
		GNOperations.recognizeMIDFileFromFile(op, config, "/sdcard/ccc.mp3");
		// try to print out album title
//		return result.getBestResponse().getAlbumTitle();
	}

	// container for metadata
	private class RecognizeFileOperation implements GNSearchResultReady {
		@Override
		public void GNResultReady(GNSearchResult result) {
			if (result.isFingerprintSearchNoMatchStatus()) {
				// TODO : return null
			} else {
				// fix
				bestResponse = result.getBestResponse();
				update(bestResponse);
			}
		}
	}

	// write metadata to file
	private void update(final GNSearchResponse bestResponse) {
		title = bestResponse.getTrackTitle();
		artist = bestResponse.getArtist();
		album = bestResponse.getAlbumTitle();
		MusicMetadataSet dataset = null;
		String dstpath = fileparent + "/" + title + ".mp3";

		File temp = new File(fileparent + "/temp.mp3");
		try {
			temp.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		MusicMetadata set = new MusicMetadata("new");
		set.setSongTitle(title);
		set.setArtist(artist);
		set.setAlbum(album);

		try {
			dataset = new MyID3().read(mp3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			new MyID3().write(mp3, temp, dataset, set);
			mp3.delete();
			temp.renameTo(new File(dstpath));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ID3WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//search mp3 file under Galaxy S3 external sd card
    	if(MP3List.ITEM_MAP.size() == 0){
    		MP3List.searchDir("/mnt/extSdCard/");
    		MP3List.searchDir("/sdcard");
    	}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpfile_list);
		
/*        if (findViewById(R.id.mpfile_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

 
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((MPFileListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mpfile_list))
                    .setActivateOnItemClick(true);
        }*/


//        fix();
        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link MPFileListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(MPFileDetailFragment.ARG_ITEM_ID, id);
            MPFileDetailFragment fragment = new MPFileDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mpfile_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, MPFileDetailActivity.class);
            detailIntent.putExtra(MPFileDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     MenuInflater menuInflater = getMenuInflater();
           menuInflater.inflate(R.menu.list_menu, menu);
           return super.onCreateOptionsMenu(menu);
    }
}