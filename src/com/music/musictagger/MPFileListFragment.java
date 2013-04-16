package com.music.musictagger;

import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.Toast;

import com.music.musictagger.mp3.MP3List;
import com.music.musictagger.mp3.MP3List.MP3File;

/**
 * A list fragment representing a list of MP3 Files. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link MPFileDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MPFileListFragment extends ListFragment {

	private static MP3File currentMP3;
	
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
    
    private static ArrayAdapter adapter;
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
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
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
					fixDialog.setMessage("are you sure to fix?").setPositiveButton("yes", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							Toast.makeText(getActivity(), "Fix Tag clicked",
									Toast.LENGTH_SHORT).show();
							Iterator<MP3List.MP3File> iterator = MP3List.ITEMS.iterator();
							while (iterator.hasNext()) {
								currentMP3 = iterator.next();
								//                		TODO: currentMP3.fix();
							} 
							mode.finish();
							
						}
						})
						.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// user canceled 
							}
						});
					fixDialog.create();
					fixDialog.show();

					
					break;
				case R.id.delete:

					AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
					deleteDialog.setMessage("are you sure to delete?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Toast.makeText(getActivity(), "Delete clicked",
									Toast.LENGTH_SHORT).show();
							SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
							int topPosition = checkedItems.keyAt(0) - 1;
							int counter = 0;
							for (int i = 0; i < checkedItems.size(); i++) {
								if(checkedItems.valueAt(i)) {
									MP3List.ITEMS.remove(i-(counter++));
									nr--;
								}
							}
							adapter.notifyDataSetChanged();
							getListView().clearChoices();
							for (int i = 0; i < getListView().getChildCount(); i++) {
								View c = getListView().getChildAt(i);
								if (c instanceof Checkable) {
									((Checkable) c).setChecked(false);
								}
							}
							mode.finish();

						}
						
					})
					.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//user canceled
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

        // TODO: replace with a real list adapter.
        adapter = new ArrayAdapter<MP3List.MP3File>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                MP3List.ITEMS);
        setListAdapter(adapter);
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
