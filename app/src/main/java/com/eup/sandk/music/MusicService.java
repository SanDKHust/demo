package com.eup.sandk.music;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.io.IOException;

public class MusicService extends Service implements MediaServiceListener {
    public static final String ACTION_START_MUSIC = "start music";
    public static final String ARGUMENT_POSITION = "position";
    public static final String ARGUMENT_LIST = "list track";
    public static final String ACTION_START_ACTIVITY = "start activity";
    public static final String ACTION_PREVIOUS = "previous track";
    public static final String ACTION_PLAY_PAUSE = "play pause track";
    public static final String ACTION_NEXT = "next track";
    public static final String ACTION_STOP_SERVICE = "stop service";
    public static final int NOTIF_ID = 1;


    private RemoteViews mRemoteViews, mRemoteViewsExpanded;
    private NotificationCompat.Builder mBuilder;
    private Notification mNotification;

    private final IBinder mBinder = new MediaBinder();
    private MediaPlayer mediaPlayer;
    private MediaServiceListener mediaServiceListener;

    public MusicService() {
    }


    public void setListener(MediaServiceListener mediaServiceListener) {
        this.mediaServiceListener = mediaServiceListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }


    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            //handle intent start a track
            case ACTION_START_MUSIC:
                playTrack((Song) intent.getParcelableExtra("selected_song"));
                break;
            case ACTION_PREVIOUS:
//                playPreviousTrack();
                break;
            case ACTION_PLAY_PAUSE:
                playPauseTrack();
                break;
            case ACTION_NEXT:
//                playNextTrack();
                break;
            case ACTION_STOP_SERVICE:
                stopSelf();
                break;
        }
    }

    private void playPauseTrack() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            else
                mediaPlayer.start();
            updateNotification();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onFail(String error) {

    }

    @Override
    public void onChangeMediaState(int mediaState) {

    }

    @Override
    public void playTrack(final Song song) {
        if (song == null)
            return;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(getApplicationContext(), song.getUri());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    startNotification(song);
                    if (mediaServiceListener != null) {
                        mediaServiceListener.onChangeMediaState(MediaPlayerState.PREPARED);
                        mediaServiceListener.playTrack(song);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentTime() {
        if (mediaPlayer == null)
            return -1;
        return
                mediaPlayer.getCurrentPosition();
    }

    public int getTotalTime() {
        if (mediaPlayer == null)
            return -1;
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        if (mediaPlayer == null)
            return false;
        return mediaPlayer.isPlaying();
    }

    @Override
    public void onShuffle(int shuffle) {

    }

    @Override
    public void onLoop(int loop) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaServiceListener = null;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public class MediaBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
//
//    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
//
//    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
//
//    public static final String ACTION_PAUSE = "ACTION_PAUSE";
//
//    public static final String ACTION_PLAY = "ACTION_PLAY";
//
//    private MediaPlayer mediaPlayer;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mediaPlayer = new MediaPlayer();
//        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//            String action = intent.getAction();
//
//            switch (action) {
//                case ACTION_START_FOREGROUND_SERVICE:
//                    startForegroundService();
//                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                        mediaPlayer.reset();
//                    }
//                    final Song song = intent.getParcelableExtra("selected_song");
//                    try {
//                        mediaPlayer.setDataSource(getApplicationContext(), song.getUri());
//                        mediaPlayer.prepare();
//                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mediaPlayer) {
//                                mediaPlayer.start();
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
//                    break;
//                case ACTION_STOP_FOREGROUND_SERVICE:
//                    stopForegroundService();
//                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
//                    break;
//                case ACTION_PLAY:
//                    Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
//                    break;
//                case ACTION_PAUSE:
//                    Toast.makeText(getApplicationContext(), "You click Pause button.", Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    /* Used to build and start foreground service. */
//    private void startForegroundService() {
//
//
//
//        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");
//
//        // Create notification default intent.
//        Intent intent = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        // Create notification builder.
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Channel_id");
//
//        // Make notification show big text.
//        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
//        bigTextStyle.setBigContentTitle("Music player implemented by foreground service.");
//        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");
//        // Set big text style.
//        builder.setStyle(bigTextStyle);
//
//        builder.setWhen(System.currentTimeMillis());
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music);
//        builder.setLargeIcon(largeIconBitmap);
//        // Make the notification max priority.
//        builder.setPriority(Notification.PRIORITY_MAX);
//        // Make head-up notification.
//        builder.setFullScreenIntent(pendingIntent, true);
//
//        // Add Play button intent in notification.
//        Intent playIntent = new Intent(this, MusicService.class);
//        playIntent.setAction(ACTION_PLAY);
//        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
//        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
//        builder.addAction(playAction);
//
//        // Add Pause button intent in notification.
//        Intent pauseIntent = new Intent(this, MusicService.class);
//        pauseIntent.setAction(ACTION_PAUSE);
//        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
//        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
//        builder.addAction(prevAction);
//
//        // Build the notification.
//        Notification notification = builder.build();
//
//        // Start foreground service.
//        startForeground(1, notification);
//    }
//
//    private void stopForegroundService() {
//        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");
//
//        // Stop foreground service and remove the notification.
//        stopForeground(true);
//
//        // Stop the foreground service.
//        stopSelf();
//    }


//    private final int NOTIFICATION_MEDIA = 21;
//    private Notification noty;
//
//    private MediaPlayer mediaPlayer;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.d("HAHA", "onCreate: ");
//        mediaPlayer = new MediaPlayer();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("HAHA", "onStartCommand: ");
//        if (intent != null && intent.getAction() != null) {
//            switch (intent.getAction()) {
//                case "play_song":
//                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                        mediaPlayer.reset();
//                    }
//                    final Song song = intent.getParcelableExtra("selected_song");
//                    try {
//                        mediaPlayer.setDataSource(getApplicationContext(), song.getUri());
//                        mediaPlayer.prepare();
//                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mediaPlayer) {
//                                mediaPlayer.start();
//                                startNotification(song);
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case "next":
//                    Log.d("HAHA", "Next");
//                    break;
//                case "play_pause":
//                    Log.d("HAHA", "play_pause");
//                    break;
//                case "previous":
//                    Log.d("HAHA", "previous");
//                    break;
//            }
//        }
//        return START_NOT_STICKY;
//    }
//

    private void startNotification(Song song) {

        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_small);
        mRemoteViewsExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);

        // Add Pause button intent in notification.
        Intent playPauseIntent = new Intent(this, MusicService.class);
        playPauseIntent.setAction(ACTION_PLAY_PAUSE);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getService(this, 0, playPauseIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.play_pause_btn, pendingPlayPauseIntent);
        mRemoteViewsExpanded.setOnClickPendingIntent(R.id.play_pause_btn, pendingPlayPauseIntent);

        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.close_btn, pendingStopIntent);
        mRemoteViewsExpanded.setOnClickPendingIntent(R.id.close_btn, pendingStopIntent);

//        // Add Play button intent in notification.
//        Intent playIntent = new Intent(this, MusicService.class);
//        playIntent.setAction(ACTION_START_MUSIC);
//        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
//        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);


//        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);


        mBuilder = new NotificationCompat.Builder(this, "channel_id");
        mNotification = mBuilder.setAutoCancel(false)
                .setContentTitle(song.getName())
                .setContentText(song.getArtist())
                .setSmallIcon(R.drawable.icon_music)
                .setCustomContentView(mRemoteViews)
                .setCustomBigContentView(mRemoteViewsExpanded)
//                .addAction(playAction)
//                .addAction(prevAction)
//                .addAction(android.R.drawable.ic_media_next, "next", pendingIntentNext)
//                .setContentIntent(pendingIntentContent)
                .setChannelId("channel_id")
                .build();
//        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
//        nm.notify(NOTIF_ID, noty);
        startForeground(NOTIF_ID, mNotification);
    }

    // use this method to update the Notification's UI
    private void updateNotification() {
        // update the icon
        mRemoteViews.setImageViewResource(R.id.play_pause_btn, mediaPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play_filled);
        mRemoteViewsExpanded.setImageViewResource(R.id.play_pause_btn, mediaPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play_filled);
        // update the title
        mRemoteViews.setTextViewText(R.id.title_tv, "Title");
        mRemoteViewsExpanded.setTextViewText(R.id.title_tv, "Title");
        // update the content
        mRemoteViews.setTextViewText(R.id.description_tv, mediaPlayer.isPlaying() ? "Playing" : "Stop");
        mRemoteViewsExpanded.setTextViewText(R.id.description_tv, mediaPlayer.isPlaying() ? "Playing" : "Stop");

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        // update the notification
        mNotificationManager.notify(NOTIF_ID, mBuilder.build());
    }
}