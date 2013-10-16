package com.obigo.baidumusic.standard.player;

import com.baidu.music.model.Music;

public interface PlayerListener {
    void onPreparePlayer(Music music, int position);

    void onStartPlayer();

    void onPausePlayer();

    void onResumePlayer();

    void onStopPlayer();

    void onUpdateProgress(int duration, int position);

    void onUpdateLoading(int duration, int position);
}
