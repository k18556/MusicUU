package com.qtfreet.musicuu.ui.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by qtfreet on 2016/3/23.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;
    private String url;
    private MyBinder myBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        url = intent.getExtras().getString("url");

        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);


    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
        mp.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public class MyBinder extends Binder {
        public int getProgress() {
            return mediaPlayer.getCurrentPosition();
        }

        public MediaPlayer mediaPlayer() {
            return mediaPlayer;
        }

        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        public int getMaxLength() {
            return mediaPlayer.getDuration();
        }

        public void pause() {
            mediaPlayer.pause();
        }

        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress);
        }

        public void start() {

            mediaPlayer.start();

        }


    }
}
