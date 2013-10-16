package com.obigo.baidumusic.standard.playlist;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.music.model.Channel;
import com.obigo.baidumusic.standard.R;

public class RadioItemAdapter extends ArrayAdapter<Channel> {
    private static class ViewHolder {
        private TextView mTitle;
        private ImageView mAlbum;
    }

    private Context mContext;
    private List<Channel> mItems;
    private int mLayoutID;
    private LayoutInflater mInflater;
    private int mChannel;

    public RadioItemAdapter(Context context, int textViewResourceId,
            List<Channel> items) {
        super(context, textViewResourceId, items);

        mContext = context;
        mItems = items;
        mLayoutID = textViewResourceId;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        View rowView = convertView;
        if (rowView == null) {
            rowView = mInflater.inflate(mLayoutID, null);
            holder = new ViewHolder();

            View title = rowView.findViewById(R.id.title);
            if(title instanceof TextView) {
                holder.mTitle = (TextView)title;
                holder.mTitle.setSelected(true);
            }
            
            View album = rowView.findViewById(R.id.album);
            if(album instanceof ImageView) {
                holder.mAlbum = (ImageView)album;
            }

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        Channel item = mItems.get(position);

        if (item.mName == null) {
            item.mName = "";
        }

        if (TextUtils.isEmpty(item.mName)) {
            holder.mTitle.setText("no title");
        } else {
            holder.mTitle.setText(item.mName);
        }

        /* Begin (Roger Wang) radio album icon 2013-05-08--003 */
        holder.mAlbum.setImageResource(R.drawable.ico_song);
        // set the postion of string by randy
        holder.mAlbum.setScaleType(ImageView.ScaleType.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        lp.setMargins(0, 24, 0, 0);
        holder.mTitle.setLayoutParams(lp);

        /* End Modification 2013-05-08--003 */

        /*
         * Start:Mcdon Tang Modify code 07 Descrip:listview item select and
         * focus Modify date:2013-05-8
         */
        try {
            if (mChannel == position) {
                rowView.setBackgroundResource(R.drawable.bg_list_on);
            } else {

                rowView.setBackgroundResource(R.drawable.bg_list_off);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* End:Mcdon Tang Modify code 07 */

        return rowView;
    }

    public void setChannelPosition(int pos) {
        mChannel = pos;
        this.notifyDataSetInvalidated();
    }
}
