package com.music.musictagger;

import com.music.musictagger.mp3.MP3List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicAdapter extends ArrayAdapter<MP3List.MP3File> {
	private int layoutResource;
	public MusicAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.layoutResource = textViewResourceId;
	}

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // We need to get the best view (re-used if possible) and then
        // retrieve its corresponding ViewHolder, which optimizes lookup efficiency
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final MP3List.MP3File entry = getItem(position);

        // Setting the title view is straightforward
        viewHolder.titleView.setText(entry.getTitle());

        // Setting the subTitle view requires a tiny bit of formatting
        final String formattedSubTitle = entry.getAlbumArtist();
        //entry.getArtist();
        // String.format("By %s on %s",
        //                                                entry.getAuthor(),
        //                                                DateFormat.getDateInstance(DateFormat.SHORT).format(entry.getPostDate())
        //                                                );

        viewHolder.subTitleView.setText(formattedSubTitle);

        // Setting image view is also simple
        byte[] bmap = entry.getArt();
		if (bmap != null) {
			Bitmap bm = BitmapFactory.decodeByteArray(bmap, 0, bmap.length);
			viewHolder.imageView.setImageBitmap(bm);
		} else {
			viewHolder.imageView.setImageResource(R.drawable.default_album_art);
		}
        //        viewHolder.imageView.setImageResource(entry.getArt());

        return view;
    }


	private View getWorkingView(final View convertView) {
		// The workingView is basically just the convertView re-used if possible
		// or inflated new if not possible
		View workingView = null;

		if(null == convertView) {
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater)context.getSystemService
		      (Context.LAYOUT_INFLATER_SERVICE);

			workingView = inflater.inflate(layoutResource, null);
		} else {
			workingView = convertView;
		}

		return workingView;
	}

	private ViewHolder getViewHolder(final View workingView) {
		// The viewHolder allows us to avoid re-looking up view references
		// Since views are recycled, these references will never change
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;


		if(null == tag || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();

			viewHolder.titleView = (TextView) workingView.findViewById(R.id.news_entry_title);
			viewHolder.subTitleView = (TextView) workingView.findViewById(R.id.news_entry_subtitle);
			viewHolder.imageView = (ImageView) workingView.findViewById(R.id.news_entry_icon);

			workingView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) tag;
		}

		return viewHolder;
	}

	/**
	 * ViewHolder allows us to avoid re-looking up view references
	 * Since views are recycled, these references will never change
	 */
	private static class ViewHolder {
		public TextView titleView;
		public TextView subTitleView;
		public ImageView imageView;
	}

}
