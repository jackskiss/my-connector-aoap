package com.obigo.baidumusic.standard.playlist;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.music.model.Music;
import com.obigo.baidumusic.standard.MusicApplication;
import com.obigo.baidumusic.standard.R;
import com.obigo.baidumusic.standard.util.AsyncImageLoader;
import com.obigo.baidumusic.standard.util.AsyncImageLoader.ImageCallback;
import com.obigo.baidumusic.standard.util.Util;
import com.obigo.baidumusic.standard.util.ViewCache;

public class SongItemAdapter extends ArrayAdapter<Music> {
    private Context mContext;
    private List<Music> mItems;
    private int mLayoutID;
    private LayoutInflater mInflater;
    private AsyncImageLoader asyncImageLoader;
    private ListView mListView;

    public SongItemAdapter(Context context, int textViewResourceId,
            List<Music> items, ListView listView) {
        super(context, textViewResourceId, items);

        mContext = context;
        mItems = items;
        mLayoutID = textViewResourceId;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mListView = listView;
        asyncImageLoader = AsyncImageLoader.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewCache viewCache;

        if (rowView == null) {
            rowView = mInflater.inflate(mLayoutID, null);
            viewCache = new ViewCache(rowView);
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }

        Music item = mItems.get(position);

        String title = (item.mTitle == null || item.mTitle.trim().isEmpty()) ? "no title"
                : item.mTitle;
        String artist = (item.mArtist == null || item.mArtist.trim().isEmpty()) ? "no name"
                : item.mArtist;
        TextView textView = viewCache.getTextView();
        textView.setText(String.format("%s - %s", title, artist));
        textView.setSelected(true);

        String imageUrl = TextUtils.isEmpty(item.mPicSmall) ? item.mPicBig
                : item.mPicSmall;

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            imageUrl = Util.imageUrlCheck(imageUrl);

            ImageView imageView = viewCache.getImageView();
            imageView.setTag(imageUrl);
            Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl,
                    new ImageCallback() {
                        public void imageLoaded(Drawable imageDrawable,
                                String imageUrl) {
                            ImageView imageViewByTag = null;
                            
                            View v = mListView.findViewWithTag(imageUrl);
                            if(v instanceof ImageView) {
                                imageViewByTag = (ImageView)v;
                            }
                            
                            if (imageViewByTag != null) {
                                if(imageDrawable != null) {
                                    imageViewByTag.setImageDrawable(imageDrawable);
                                } else {
                                    imageViewByTag.setImageResource(R.drawable.img_none);
                                }
                            }
                        }
                    });

            if (cachedImage == null) {
                imageView.setImageResource(R.drawable.img_none);
            } else {
                imageView.setImageDrawable(cachedImage);
            }
        } else {
            ImageView imageView = viewCache.getImageView();
            imageView.setTag(imageUrl);
            imageView.setImageResource(R.drawable.img_none);
        }

        // End Modification SongItemAdapter-003

        try {
            if (MusicApplication.getPlayer().getCurrentPosition() == position) {
                rowView.setBackgroundResource(R.drawable.bg_list_on);
            } else {
                rowView.setBackgroundResource(R.drawable.bg_list_off);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowView;
    }
    /* End Modification 2013-05-29--001 */

}
