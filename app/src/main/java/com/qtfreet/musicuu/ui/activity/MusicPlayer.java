package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.MusicBean;
import com.qtfreet.musicuu.utils.util;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.mobiwise.playerview.MusicPlayerView;

/**
 * Created by qtfreet on 2016/3/20.
 */
public class MusicPlayer extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    private MusicPlayerView mpv;
    private MediaPlayer media;
    @Bind(R.id.mv)
    ImageButton mv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        pic =  MusicBean.pic;
        MvUrl = MusicBean.videoUrl;
        url = MusicBean.listenUrl;
        time = (int) util.getIntTime(MusicBean.time);
        initview();
    }

    String pic;
    String MvUrl;
    int time;
    String url;

    private void loadCover(final String pic, final String url, int time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mpv.setCoverBitmap(Picasso.with(MusicPlayer.this)
                            .load(pic)
                            .resize(300, 300)
                            .centerCrop().get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (time != 0) {
            mpv.setMax(time);
        } else {
            mpv.setProgressVisibility(false);
        }
        media = new MediaPlayer();
        media.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            media.setDataSource(url);
            media.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initview() {
        ButterKnife.bind(this);
        mpv = (MusicPlayerView) findViewById(R.id.mpv);
        if (MvUrl.equals("")) {
            mv.setVisibility(View.INVISIBLE);
        }
        loadCover(pic, url, time);

        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    media.pause();
                } else {
                    mpv.start();
                    media.start();
                }
            }
        });

        media.setOnCompletionListener(this);
        mv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MusicPlayer.this, VideoActivity.class);
                startActivity(i);
                if (mpv.isRotating()) {
                    mpv.stop();
                    media.pause();

                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (media != null) {
            media.release();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mpv.isRotating()) {
            media.reset();
            mpv.stop();
            media.pause();
        }
    }
}
