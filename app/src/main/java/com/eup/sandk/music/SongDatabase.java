package com.eup.sandk.music;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class SongDatabase {

    SongDatabase() {

    }

    public ArrayList<Song> getSongs(Context context) {
        String[] projections = new String[]{
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projections,
                null,
                null,
                MediaStore.Audio.Media.TITLE + " ASC"
        );

        ArrayList<Song> songs = new ArrayList<>();

        if (cursor == null) {
            return songs;
        }
        if (cursor.getCount() == 0) {
            cursor.close();
            return songs;
        }

        int indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int indexDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int indexData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int indexAlbumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String title = cursor.getString(indexTitle);
            String artist = cursor.getString(indexArtist);
            long duration = cursor.getLong(indexDuration);
            Uri data = Uri.parse(cursor.getString(indexData));
            String album = cursor.getString(indexAlbumId);

            if (duration > 30000) {
                songs.add(new Song(title, artist, data, duration, album));
            }
            cursor.moveToNext();
        }

        cursor.close();
        return songs;
    }
}
