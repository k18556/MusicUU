package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.MusicBean;
import com.qtfreet.musicuu.utils.MyThreadPool;
import com.qtfreet.musicuu.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.mobiwise.playerview.MusicPlayerView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by qtfreet on 2016/3/20.
 */
public class MusicPlayer extends AppCompatActivity implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener {
    private MusicPlayerView mpv;

    @Bind(R.id.mv)
    ImageButton mv;

    private IjkMediaPlayer ijkExoMediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play);
        pic = MusicBean.pic;
        MvUrl = MusicBean.videoUrl;
        url = MusicBean.listenUrl;
        if (url.equals("")) {
            Toast.makeText(this, "未找到播放链接", Toast.LENGTH_SHORT).show();
            return;
        }
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
        if (ijkExoMediaPlayer == null) {
            return;
        }
        if (!isShowCurrentTime) {
            return;
        }
        int currentTime = (int) ijkExoMediaPlayer.getCurrentPosition();
        tv_current_time.setText(SystemUtil.generateTime(ijkExoMediaPlayer.getCurrentPosition()));
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
        ijkExoMediaPlayer = new IjkMediaPlayer();
        ijkExoMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            ijkExoMediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ijkExoMediaPlayer.prepareAsync();
        ijkExoMediaPlayer.setOnPreparedListener(this);
        ijkExoMediaPlayer.setOnBufferingUpdateListener(this);
        ijkExoMediaPlayer.setOnCompletionListener(this);
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
                    ijkExoMediaPlayer.seekTo(progress);

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
                    ijkExoMediaPlayer.pause();
                } else {
                    mpv.start();
                    ijkExoMediaPlayer.start();
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
                    ijkExoMediaPlayer.pause();

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
        if (ijkExoMediaPlayer != null) {
            ijkExoMediaPlayer.reset();
            ijkExoMediaPlayer.release();
            mpv.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mpv.isRotating()) {
            mpv.stop();
            ijkExoMediaPlayer.pause();
        }
    }


    @Override
    public void onPrepared(IMediaPlayer mp) {
        mp.pause();
        mpv.setVisibility(View.VISIBLE);
        mSearchProgressBar.setVisibility(View.GONE);
        mseekBar.setMax((int) mp.getDuration());
        tv_duration_time.setText(SystemUtil.generateTime(mp.getDuration()));
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        isShowCurrentTime = false;
        if (mpv.isRotating()) {
            mp.seekTo(0);
            mpv.stop();
            mp.pause();
            mseekBar.setProgress(0);
        }
    }
}
