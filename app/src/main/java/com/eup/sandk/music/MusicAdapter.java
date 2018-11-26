package com.eup.sandk.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private ArrayList<Song> songs;
    private Context context;
    private LayoutInflater inflater;
    private OnMusicClickListener onMusicClickListener;

    public MusicAdapter(Context context, ArrayList<Song> songs, OnMusicClickListener onMusicClickListener) {
        this.context = context;
        this.songs = songs;
        inflater = LayoutInflater.from(context);
        this.onMusicClickListener = onMusicClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Song song = songs.get(position);

        holder.bindData(song);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMusicClickListener.onSongClick(position);
            }
        });

        if(position == songs.size() - 1)
            holder.border.setVisibility(View.INVISIBLE);
        else
            holder.border.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtArtist;
        ImageView imgAlbum;
        View border;

        public ViewHolder(final View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txt_name);
            txtArtist = itemView.findViewById(R.id.txt_artist);
            imgAlbum = itemView.findViewById(R.id.img_music);
            border = itemView.findViewById(R.id.border);

        }

        void bindData(Song song) {
            txtName.setText(song.getName());
            txtArtist.setText(song.getArtist());
            new LoadImage().execute(song.getUri());
        }

        class LoadImage extends AsyncTask<Uri, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(Uri... uris) {
                return getAlbumImage(uris[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null)
                    imgAlbum.setImageBitmap(bitmap);
                else
                    imgAlbum.setImageResource(R.drawable.icon_music);
            }

            private Bitmap getAlbumImage(Uri uri) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                byte[] rawArt;

                BitmapFactory.Options bfo = new BitmapFactory.Options();

                mmr.setDataSource(context, uri);
                rawArt = mmr.getEmbeddedPicture();

                if (null != rawArt) {
                    return BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                }
                return null;
            }
        }
    }


    interface OnMusicClickListener {
        void onSongClick(int position);
    }
}
