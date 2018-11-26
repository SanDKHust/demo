package com.eup.sandk.music;


public interface MediaServiceListener {
    void onFail(String error);

    void onChangeMediaState(@MediaPlayerState.MediaState int mediaState);

    void playTrack(Song song);

    void onShuffle(int shuffle);

    void onLoop(int loop);
}
