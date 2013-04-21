package com.music.musictagger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.UnsupportedEncodingException;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//search mp3 file under Galaxy S3 external sd card
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpfile_list);
/*		if (findViewById(R.id.mpfile_detail_container) != null) {
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

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.sort_by_title:
	            MP3List.sortByTitle();
	            MPFileListFragment.musicAdapter.clear();
	            for(final MP3List.MP3File entry :MP3List.ITEMS) {
	            	MPFileListFragment.musicAdapter.add(entry);
	            }
	            
	            MPFileListFragment.musicAdapter.notifyDataSetChanged();
	            return true;
	        case R.id.sort_by_artist:
	        	MP3List.sortByArtist();
	        	MPFileListFragment.musicAdapter.clear();
	            for(final MP3List.MP3File entry :MP3List.ITEMS) {
	            	MPFileListFragment.musicAdapter.add(entry);
	            }
	            
	            MPFileListFragment.musicAdapter.notifyDataSetChanged();
	        	//setAdapter();
	            return true;
	        case R.id.sort_by_album:
	        	MP3List.sortByAlbum();
	        	MPFileListFragment.musicAdapter.clear();
	            for(final MP3List.MP3File entry :MP3List.ITEMS) {
	            	MPFileListFragment.musicAdapter.add(entry);
	            }
	            
	            MPFileListFragment.musicAdapter.notifyDataSetChanged();
	        	//setAdapter();
	            return true;
	        case R.id.browse:
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	        	alert.setTitle("Search a Folder for MP3");
	        	alert.setMessage("Enter a folder path contains MP3 files");

	        	// Set an EditText view to get user input 
	        	final EditText input = new EditText(this);
	        	alert.setView(input);
	        	alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int whichButton) {
		        		MP3List.searchDir(getApplicationContext(),input.getText().toString());
			        	MPFileListFragment.musicAdapter.clear();
				        for(final MP3List.MP3File entry :MP3List.ITEMS) {
				            	MPFileListFragment.musicAdapter.add(entry);
				            }
				        MPFileListFragment.musicAdapter.notifyDataSetChanged();
		        	  }
		        	} );

	        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        	  public void onClick(DialogInterface dialog, int whichButton) {
	        	  }
	        	});

	        	alert.show();
	        	return true;
	        case R.id.sync_by_lan:
	        	AlertDialog.Builder sync_alert = new AlertDialog.Builder(this);
	        	LinearLayout lila1= new LinearLayout(this);
	            lila1.setOrientation(1);

	        	sync_alert.setTitle("Sync With Lan");

	        	// Set an EditText view to get user input 
	        	final EditText input1 = new EditText(this);
	        	final EditText input2 = new EditText(this);
	        	TextView msg1 = new TextView(this);
	        	msg1.setText("IP Adress: ");
	        	TextView msg2 = new TextView(this);
	        	msg2.setText("Port: ");
	        	lila1.addView(msg1);
	            lila1.addView(input1);
	            lila1.addView(msg2);
	            lila1.addView(input2);
	        	sync_alert.setView(lila1);
	        	sync_alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
		        	public void onClick(DialogInterface dialog, int whichButton) {
		        		//Add operation here
		        		
		        		Socket socket;
					try {
						String ipadress = ;
						int port = ;
						socket = new Socket(ipadress,port);
						DataInputStream datainput = new DataInputStream(socket.getInputStream());
						int count = datainput.readInt();
						for( int i = 0 ; i < count ; i++ ){
							String filename = new String();
							int namelength = datainput.readInt();
							for( int j = 0 ; j < namelength ; j++ ){
								filename = filename + datainput.readChar();
							}
							long size = datainput.readLong();
							byte data;
							File file = new File(filename);
							FileOutputStream outfile = new FileOutputStream(file);
							for( int j = 0 ; j < size ; j++ ){
								data = datainput.readByte();
								outfile.write(data);
							}
							outfile.close();
						}
						datainput.close();
						socket.close();
				
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        		
			        	MPFileListFragment.musicAdapter.clear();
				        for(final MP3List.MP3File entry :MP3List.ITEMS) {
				            	MPFileListFragment.musicAdapter.add(entry);
				            }
				        MPFileListFragment.musicAdapter.notifyDataSetChanged();
		        	  }
		        	} );

	        	sync_alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        	  public void onClick(DialogInterface dialog, int whichButton) {
	        	  //Cancelled   
	        	  }
	        	});
	        	sync_alert.show();
	        	return true;
	        case R.id.sync:
	        	ProgressDialog progress = new ProgressDialog(this);
	        	progress.setTitle("Syncing");
	        	progress.setMessage("Wait while Syncing...");
	        	progress.show();
	        	//Add Operation here
	        	
	       
			Socket socket;
			try {
				socket = new Socket("sslab04.cs.purdue.edu",8080);
				DataOutputStream dataoutput = new DataOutputStream(socket.getOutputStream());
				dataoutput.writeInt(0);
				DataInputStream datainput = new DataInputStream(socket.getInputStream());
				int count = datainput.readInt();
				for( int i = 0 ; i < count ; i++ ){
					String filename = new String();
					int namelength = datainput.readInt();
				
					for( int j = 0 ; j < namelength ; j++ ){
						filename = filename + datainput.readChar();
					}
					long size = datainput.readLong();
					byte data;
					File file = new File(filename);
					FileOutputStream outfile = new FileOutputStream(file);
					for( int j = 0 ; j < size ; j++ ){
						data = datainput.readByte();
						outfile.write(data);
					}
					outfile.close();
				}
				datainput.close();
				dataoutput.close();
				socket.close();
			
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        	
	        	// To dismiss the dialog
	        	progress.dismiss();
	        	return true;
	     }
	    return super.onOptionsItemSelected(item);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.list_menu, menu);
		return;
	}

}
