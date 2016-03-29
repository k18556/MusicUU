package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.MusicBean;
import com.qtfreet.musicuu.utils.MyThreadPool;
import com.qtfreet.musicuu.utils.SystemUtil;
import com.qtfreet.musicuu.utils.util;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.mobiwise.playerview.MusicPlayerView;

/**
 * Created by qtfreet on 2016/3/20.
 */
public class MusicPlayer extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {
    private MusicPlayerView mpv;

    @Bind(R.id.mv)
    ImageButton mv;
    private MediaPlayer media;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play);

        pic = MusicBean.pic;
        MvUrl = MusicBean.videoUrl;
        url = MusicBean.listenUrl;
        time = MusicBean.time;
        initview();
        initThread();
    }


    String pic;
    String MvUrl;
    String url;
    String time;
    @Bind(R.id.tv_current_time)
    TextView tv_current_time;
    @Bind(R.id.tv_duration_time)
    TextView tv_duration_time;

    private SeekBar mseekBar;


    private void initThread() {
        ExecutorService e = MyThreadPool.getInstance().getMyExecutorService();
        e.execute(new Runnable() {
            @Override
            public void run() {
                while (isShowCurrentTime) {
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            updataProgressBar();
                        }
                    });

                }

            }
        });

    }


    private boolean isShowCurrentTime = true;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title_name)
    TextView toolbarTitle;
    @Bind(R.id.pb_search_wait)
    ProgressBar mSearchProgressBar;

    private void updataProgressBar() {
        if (media == null) {
            return;
        }
        if (!isShowCurrentTime) {
            return;
        }
        int currentTime = (media.getCurrentPosition());
        Log.e("TAG", currentTime + "");
        tv_current_time.setText(SystemUtil.generateTime(media.getCurrentPosition()));
        mseekBar.setProgress(currentTime);
    }

    private void loadCover(final String pic, final String url) {
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
        mpv.setProgressVisibility(false);
        mpv.setVisibility(View.INVISIBLE);
        media = new MediaPlayer();
        media.reset();
        media.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            media.setDataSource(url);
            media.prepareAsync();
            media.setOnPreparedListener(this);
            media.setOnBufferingUpdateListener(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
        media.setOnCompletionListener(this);
    }

    private void initview() {
        ButterKnife.bind(this);
        mSearchProgressBar.setVisibility(View.VISIBLE);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbarTitle.setText(MusicBean.songName + " - " + MusicBean.artist);
            }
        }
        mseekBar = (SeekBar) findViewById(R.id.seek_bar);
        mpv = (MusicPlayerView) findViewById(R.id.mpv);
        if (MvUrl.equals("")) {
            mv.setVisibility(View.INVISIBLE);
        }

        loadCover(pic, url);

        mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                if (seekBar == mseekBar) {
                    media.seekTo(progress);
                    Log.e("TAG", progress + " ");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShowCurrentTime = false;
        if (media != null) {
            media.reset();
            media.release();
            mpv.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mpv.isRotating()) {
            mpv.stop();
            media.pause();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isShowCurrentTime = false;
        if (mpv.isRotating()) {
            mp.seekTo(0);
            mpv.stop();
            mp.pause();
            mseekBar.setProgress(0);
        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mseekBar.setMax(mp.getDuration());
        tv_duration_time.setText(SystemUtil.generateTime(mp.getDuration()));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mpv.setVisibility(View.VISIBLE);
        mSearchProgressBar.setVisibility(View.GONE);

    }
}
