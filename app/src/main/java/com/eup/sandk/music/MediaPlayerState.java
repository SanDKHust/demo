package com.eup.sandk.music;

import android.support.annotation.IntDef;

public interface MediaPlayerState {
    int IDLE = 0;
    int PLAYING = 1;
    int PREPARED = 2;
    int PREPARING = 3;
    int PAUSED = 4;
    int STOPPED = 5;
    int ERROR = 6;
    int LOOP_NO = 0;
    int LOOP_ONE = 1;
    int LOOP_ALL = 2;
    int SHUFFLE = 1;
    int NO_SHUFFLE = 0;

    @IntDef({IDLE, PLAYING, PREPARING, PREPARED, PAUSED, STOPPED, ERROR})
    @interface MediaState {
    }

    @IntDef({LOOP_NO, LOOP_ONE, LOOP_ALL})
    @interface LoopState {
    }

    @IntDef({SHUFFLE, NO_SHUFFLE})
    @interface ShuffleState {
    }
}