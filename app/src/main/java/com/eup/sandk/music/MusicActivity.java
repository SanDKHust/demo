package com.eup.sandk.music;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.eup.sandk.music.example.MusicPlayerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicActivity extends AppCompatActivity implements MusicAdapter.OnMusicClickListener, MediaServiceListener {

    private AppCompatSeekBar seekBar;
    private TextView currentTimeTv, totalTimeTv;
    private ImageButton playPauseIb;

    private SongDatabase songDatabase;
    private RecyclerView rcvMusic;
    private MusicAdapter musicAdapter;
    private ArrayList<Song> songs;
    private boolean isStart = false;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        rcvMusic = findViewById(R.id.rcv_music);
        seekBar = findViewById(R.id.seek_bar);
        currentTimeTv = findViewById(R.id.current_time_tv);
        totalTimeTv = findViewById(R.id.total_time_tv);
        playPauseIb = findViewById(R.id.play_pause_btn);

        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize("レントゲン写真は二本の骨折した指を映し出していた。");
        for (Token token : tokens) {
            Log.d("HAHA",token.getSurface() + "\t" + token.getAllFeatures());
        }

        mHandler = new Handler();
        requestPermission();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onSongClick(int position) {
        Song song = songs.get(position);
        if (isStart) {
            if (mMediaService != null)
                mMediaService.playTrack(song);
        } else {
            isStart = true;
            Intent intent = new Intent(MusicActivity.this, MusicService.class);
            intent.setAction(MusicService.ACTION_START_MUSIC);
            intent.putExtra("selected_song", song);
            startService(intent);
        }
    }

    private MusicService mMediaService;
    private boolean mIsBound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MusicService.MediaBinder binder = (MusicService.MediaBinder) service;
            mMediaService = binder.getService();
            mMediaService.setListener(MusicActivity.this);
            if (mMediaService.isPlaying()) {
                setupSeekBar();
                updateSeekbar();
            }
            mIsBound = true;

//            getData();
//            updatePlayingDetail(mMediaService.getCurrentTrack());

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBound = false;
        }
    };

    public void requestPermission() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            };
        }
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            songDatabase = new SongDatabase();
            songs = songDatabase.getSongs(this);

            musicAdapter = new MusicAdapter(this, songs, this);
            rcvMusic.setAdapter(musicAdapter);
            rcvMusic.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                songDatabase = new SongDatabase();
                songs = songDatabase.getSongs(this);

                musicAdapter = new MusicAdapter(this, songs, this);
                rcvMusic.setAdapter(musicAdapter);
                rcvMusic.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Toast.makeText(this, "Permission deny", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mIsBound)
            unbindService(mConnection);
//        if (isStart) {
//            Intent intent = new Intent(MusicActivity.this, MusicService.class);
//            stopService(intent);
//        }
        super.onDestroy();
    }

    @Override
    public void onFail(String error) {

    }

    @Override
    public void onChangeMediaState(int mediaState) {
        if (mediaState == MediaPlayerState.PREPARED) {

            // setup duration media player
            setupSeekBar();

        }
    }

    private void setupSeekBar() {
        if (mMediaService != null) {
            int duration = mMediaService.getTotalTime();
            if (duration > 0) {

                int dSeconds = duration / 1000 % 60;
                int dMinutes = (duration / (1000 * 60)) % 60;
                totalTimeTv.setText(String.format(Locale.getDefault(), "%d:%02d", dMinutes, dSeconds));

                seekBar.setMax(duration);

                // da load xong audio, co the play
                playPauseIb.setImageResource(R.drawable.ic_play_filled);
                currentTimeTv.setText("0:00");
            }
        }
    }

    private void updateSeekbar() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMediaService != null) {
                    int time = mMediaService.getCurrentTime();
                    int dSeconds = time / 1000 % 60;
                    int dMinutes = (time / (1000 * 60)) % 60;
                    currentTimeTv.setText(String.format(Locale.getDefault(), "%d:%02d", dMinutes, dSeconds));

                    seekBar.setProgress(time);
                    mHandler.postDelayed(this, 1000);
                }
            }
        }, 0);
    }

    @Override
    public void playTrack(Song song) {
        updateSeekbar();
    }

    @Override
    public void onShuffle(int shuffle) {

    }

    @Override
    public void onLoop(int loop) {

    }
}
