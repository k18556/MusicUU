package com.qtfreet.musicuu.ui.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.MusicBean;
import com.qtfreet.musicuu.utils.MyThreadPool;
import com.qtfreet.musicuu.utils.SystemUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

public class VideoActivity extends Activity implements OnClickListener, OnCheckedChangeListener, OnSeekBarChangeListener, View.OnSystemUiVisibilityChangeListener, MediaPlayer.OnCompletionListener, OnPreparedListener {
    private VideoView videoView;
    private View controlView;
    private TextView tvDuration;
    private TextView tvCurrentTime;
    private TextView tvName;
    private ImageView ivBack;
    private ImageView ivPlayState;
    private ImageView ivMore;
    private SeekBar seekBar;
    private PopupWindow popupWindow;
    private RadioButton rbOriginSize;
    private RadioButton rbAllSize;
    private RadioButton rbFixAllSize;
    private SeekBar sbVolume;
    private SeekBar sbLight;
    private String videoPath;
    private String videoName;
    private View main;
    @Bind(R.id.pb_search_wait)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        videoName = MusicBean.songName + " " + MusicBean.artist;
        videoPath = MusicBean.videoUrl;
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }

        main = getLayoutInflater().inflate(R.layout.activity_mv, null);
        setContentView(main);
        main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        hideNag();


        initControlView();

        initVideoView();

        initControlListener();

        initPopupWindow();

        initThread();
    }

    private void hideNag() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                m.sendEmptyMessage(0);

            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 5000);
    }


    private android.os.Handler m = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (main.getSystemUiVisibility() == View.SYSTEM_UI_FLAG_VISIBLE) {
                        main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    }
                    if (isControlShowing) {
                        m.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isControlShowing) {
                                    dismissControlView();
                                }
                            }
                        }, 3000);
                    }

                    break;
            }
            return true;
        }
    });
    private boolean isShowCurrentTime = true;
    ExecutorService e;

    private void initThread() {
        e = Executors.newSingleThreadExecutor();
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

    @Override
    protected void onDestroy() {
        isShowCurrentTime = false;
        super.onDestroy();
        if (videoView != null) {
            
            videoView = null;
        }


    }

    private void updataProgressBar() {
        if (videoView == null) {
            return;
        }
        if (!isShowCurrentTime) {
            return;
        }
        int currentTime = (int) (videoView.getCurrentPosition() / 1000);
        tvCurrentTime.setText(SystemUtil.generateTime(videoView.getCurrentPosition()));
        seekBar.setProgress(currentTime);
    }


    private void initVideoView() {
        ButterKnife.bind(this);
        progressBar.setVisibility(View.VISIBLE);
        videoView = (VideoView) findViewById(R.id.vv);
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);

    }


    private void initControlView() {
        controlView = findViewById(R.id.control_rl);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivPlayState = (ImageView) findViewById(R.id.iv_mode);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        tvDuration = (TextView) findViewById(R.id.tv_duration_time);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivMore = (ImageView) findViewById(R.id.iv_more);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        tvName.setText(videoName);
    }

    private void initControlListener() {
        ivBack.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        ivPlayState.setOnClickListener(this);
        tvName.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void initPopupWindow() {
        final View view = View.inflate(this, R.layout.pop_window, null);
        rbOriginSize = (RadioButton) view.findViewById(R.id.radio2);
        rbAllSize = (RadioButton) view.findViewById(R.id.radio0);
        rbFixAllSize = (RadioButton) view.findViewById(R.id.radio1);
        rbOriginSize.setOnCheckedChangeListener(this);
        rbAllSize.setOnCheckedChangeListener(this);
        rbFixAllSize.setOnCheckedChangeListener(this);

        sbVolume = (SeekBar) view.findViewById(R.id.sb_volume);
        sbLight = (SeekBar) view.findViewById(R.id.sb_light);
        sbLight.setMax(100);
        sbLight.setProgress(90);
        SystemUtil.setScreeBrightness(90, this);
        int max = SystemUtil.getMaxVolume(this);
        sbVolume.setMax(max);
        int progress = SystemUtil.getVolume(this);
        sbVolume.setProgress(progress);
        sbLight.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);

        final View root = findViewById(android.R.id.content);

        root.post(new Runnable() {
            @Override
            public void run() {
                int hei = root.getMeasuredHeight();
                int wid = root.getMeasuredWidth();
                popupWindow = new PopupWindow(view, wid / 2, hei);
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_mode:
                boolean isPlaying = videoView.isPlaying();
                if (isPlaying) {
                    videoView.pause();
                    ivPlayState.setSelected(true);
                } else {
                    videoView.start();
                    ivPlayState.setSelected(false);
                }
                break;
            case R.id.iv_more:
                popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.RIGHT, 0, 0);
                dismissControlView();
                break;
            default:
                break;
        }
    }

    private boolean isControlShowing = false;

    private void dismissControlView() {
        controlView.setVisibility(View.GONE);

        isControlShowing = false;
    }

    private void showControlView() {
        controlView.setVisibility(View.VISIBLE);
        isControlShowing = true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            int type = -1;
            if (buttonView == rbOriginSize) {
                type = 0;
            } else if (buttonView == rbAllSize) {
                type = 1;
            } else if (buttonView == rbFixAllSize) {
                type = 2;
            }
            changedVideoViewLayout(type);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                return true;
            }

            if (!isControlShowing) {

                showControlView();
            }
        }

        return super.onTouchEvent(event);
    }

    private void changedVideoViewLayout(int type) {
        videoView.setVideoLayout(type, 0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (!fromUser) {
            return;
        }
        if (seekBar == sbVolume) {
            SystemUtil.setVolume(progress, this);
        } else if (seekBar == sbLight) {
            SystemUtil.setScreeBrightness(progress, this);
        } else if (seekBar == this.seekBar) {
            videoView.seekTo(progress * 1000);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("TAG", "结束");
        isShowCurrentTime = false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        progressBar.setVisibility(View.GONE);
        Log.e("TAG", "开始准备");
        int duration = (int) (videoView.getDuration() / 1000);
        seekBar.setMax(duration);
        tvDuration.setText(SystemUtil.generateTime(videoView.getDuration()));
    }
}
