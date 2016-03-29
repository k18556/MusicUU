package com.qtfreet.musicuu.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.resultBean;

import java.util.List;

/**
 * Created by Bear on 2016/1/29.
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private Context mContext;
    private List<resultBean> result;
    private OnItemClickListener mListener;
    private OnItemClickListener mDown;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setDownloadMusicListener(OnItemClickListener listener) {
        this.mDown = listener;
    }

    public ContentAdapter(Context mContext, List<resultBean> items) {
        this.mContext = mContext;
        this.result = items;
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_item_layout, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContentViewHolder holder, int position) {
        holder.artist.setText(result.get(position).getArtist());
        holder.songName.setText(result.get(position).getSongName());
        holder.Album.setText(result.get(position).getAlbum());
        Log.e("TAG1111111",result.get(position).getVideoUrl());
        if (!result.get(position).getVideoUrl().equals("")) {
            holder.imageView.setVisibility(View.VISIBLE);
        }


        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v, holder.getAdapterPosition());
                }
            });

        }
        holder.btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDown.onItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        TextView artist;
        CardView cvCard;
        TextView songName;
        TextView Album;
        ImageButton btn_down;
        ImageView imageView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            artist = (TextView) itemView.findViewById(R.id.artist);
            cvCard = (CardView) itemView.findViewById(R.id.card_view);
            songName = (TextView) itemView.findViewById(R.id.songname);
            Album = (TextView) itemView.findViewById(R.id.album);
            btn_down = (ImageButton) itemView.findViewById(R.id.down_music);
            imageView = (ImageView) itemView.findViewById(R.id.exist_mv);
        }
    }

}
