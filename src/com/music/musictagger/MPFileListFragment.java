package com.music.musictagger;

import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNOperationStatusChanged;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;
import com.gracenote.mmid.MobileSDK.GNStatus;
import com.music.musictagger.mp3.MP3List;

/**
 * A list fragment representing a list of MP3 Files. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link MPFileDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.*
 */
public class MPFileListFragment extends ListFragment {

	private GNConfig config;
	private String filename, fileparent;
	private String title, artist, album, year;
	private RecognizeFileOperation op;
	private SparseBooleanArray checkedItems;
	private int lastIndex;
	private int numOfFixings;
	private ProgressDialog progress;
	RecognizeFilesTask task;

	public void fix() throws IOException, TagException {	

		
		lastIndex = checkedItems.keyAt(getListView().getCheckedItemCount() - 1);
		numOfFixings = getListView().getCheckedItemCount();
		for (int i = 0; i < numOfFixings; i++) {
			int index = checkedItems.keyAt(i);
			// TODO : fix map
//			MP3List.ITEM_MAP.remove(MP3List.ITEMS.get(index).getFilename());
//			MP3List.ITEMS.remove(index);
			filename = MP3List.ITEMS.get(index).getFilename();
			fileparent = MP3List.ITEMS.get(index).getParent();
			op = new RecognizeFileOperation(index);
			GNOperations.recognizeMIDFileFromFile(op, config, fileparent + "/" + filename);
		}
		
		new ArrayAdapter<MP3List.MP3File>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1,
				MP3List.ITEMS);
		
		musicAdapter = new MusicAdapter(getActivity(),R.layout.list_item);
		for(final MP3List.MP3File entry :MP3List.ITEMS) {
			musicAdapter.add(entry);
		}
		
		setListAdapter(musicAdapter);
		musicAdapter.notifyDataSetChanged();
	}

	// container for metadata
	private class RecognizeFileOperation implements GNSearchResultReady,GNOperationStatusChanged {
		
		private MP3File mp3;
		private AbstractID3v2 id3v2tag;
		private GNSearchResponse bestResponse;
		private int index;
		
		public RecognizeFileOperation( int index ) throws IOException, TagException {
			this.index = index;
			this.mp3 = new MP3File(MP3List.ITEMS.get(index).getMusic());
		}
		
		@Override
		public void GNStatusChanged(GNStatus status) {
			updateProgress(status.getMessage());
		}
		
		@Override
		public void GNResultReady(GNSearchResult result) {
			if (result.isFailure()) {
				// return error message
				String errmsg = String.format("[%d] %s", result.getErrCode(),result.getErrMessage());
				updateProgress(errmsg);

			} else {
				// fix
				id3v2tag = mp3.getID3v2Tag();
				bestResponse = result.getBestResponse();
				title = bestResponse.getTrackTitle();
				artist = bestResponse.getArtist();
				album = bestResponse.getAlbumTitle();
				year = bestResponse.getAlbumReleaseYear();
				id3v2tag.setSongTitle(title);
				id3v2tag.setLeadArtist(artist);
				id3v2tag.setAlbumTitle(album);
				id3v2tag.setYearReleased(year);
				MP3List.ITEMS.get(index).setTitle(title);
				MP3List.ITEMS.get(index).setArtist(artist);
				MP3List.ITEMS.get(index).setAlbum(album);
				MP3List.ITEMS.get(index).setYear(year);
				mp3.setID3v1Tag(id3v2tag);
				mp3.setID3v2Tag(id3v2tag);
				
				task = new RecognizeFilesTask(mp3);
				task.execute();

			}

			if (index == lastIndex)
			{
				String successmsg = String.format("Sucessfully Fixed %d Music Files", numOfFixings); 
				Toast.makeText(getActivity(), successmsg, Toast.LENGTH_SHORT).show();
				progress.dismiss();
			}
		}
	}
	
    public class RecognizeFilesTask extends AsyncTask<Object , Object , String> {
    	
    	MP3File mp3;
    	
    	private RecognizeFilesTask(MP3File mp3) {
    		this.mp3 = mp3;
    	}
    	
    	@Override
        protected void onPreExecute() {
			String successmsg = String.format("Sucessfully Fixed Tags for \"%s\"", title); 
			Toast.makeText(getActivity(), successmsg, Toast.LENGTH_SHORT).show();
        }

    	@Override
        protected String doInBackground(Object... params) {
			try {
				mp3.save();
			} catch (TagException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		return null;
    	}      

        @Override
        protected void onPostExecute(String result) {
        }
    }
	
	private void updateProgress(String msg) {
    	progress.setProgress( progress.getProgress()+1 );
    	progress.setMessage(msg);
	}

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public static MusicAdapter musicAdapter;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);

		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MPFileListFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);

		getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {	    

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				((ListView) parent).setItemChecked(position,
						((ListView) parent).isItemChecked(position));
				return false;
			}
		});

		getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

			private int nr = 0;

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				getActivity().getMenuInflater().inflate(R.menu.action_menu,
						menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.fix_tag:

					AlertDialog.Builder fixDialog = new AlertDialog.Builder(getActivity());
					fixDialog.setMessage("Are you sure to fix?").setPositiveButton("yes", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							checkedItems = getListView().getCheckedItemPositions();
							try {
					    		progress=new ProgressDialog(getActivity());
					    		progress.setMessage("Starting to FingerPrint...");
					    		progress.show();
								fix();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (TagException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mode.finish();							
						}
					})
					.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
					fixDialog.create();
					fixDialog.show();
					break;

				case R.id.delete:

					AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
					deleteDialog.setMessage("Are you sure to delete?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							checkedItems = getListView().getCheckedItemPositions();
							for (int i = getListView().getCheckedItemCount() - 1; i >= 0; --i) {
								int index = checkedItems.keyAt(i);
								MP3List.ITEM_MAP.remove(MP3List.ITEMS.get(index).getFilename());
								MP3List.ITEMS.remove(index);
							}
				            MPFileListFragment.musicAdapter.clear();
				            for(final MP3List.MP3File entry :MP3List.ITEMS) {
				            	MPFileListFragment.musicAdapter.add(entry);
				            }
							musicAdapter.notifyDataSetChanged();
							mode.finish();
						}
					})
					.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
					deleteDialog.create();
					deleteDialog.show();
					break;
				}
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				nr = 0;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked) {
					nr++;
				} else {
					nr--;
				}
				mode.setTitle(nr + " selected");
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new ArrayAdapter<MP3List.MP3File>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1,
				MP3List.ITEMS);
		
		musicAdapter = new MusicAdapter(getActivity(),R.layout.list_item);
		for(final MP3List.MP3File entry :MP3List.ITEMS) {
			musicAdapter.add(entry);
		}
		setListAdapter(musicAdapter);
		
		config = GNConfig.init("224512-544A82B56BFA252D79DDD53B4EC00ED3", getActivity().getApplicationContext());
	}



	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(MP3List.ITEMS.get(position).getFilename() );
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != AdapterView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	@Override
	public void onResume() {
		super.onResume();		
		new ArrayAdapter<MP3List.MP3File>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1,
				MP3List.ITEMS);
		
		musicAdapter = new MusicAdapter(getActivity(),R.layout.list_item);
		for(final MP3List.MP3File entry :MP3List.ITEMS) {
			musicAdapter.add(entry);
		}
		setListAdapter(musicAdapter);
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick
				? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == AdapterView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}
