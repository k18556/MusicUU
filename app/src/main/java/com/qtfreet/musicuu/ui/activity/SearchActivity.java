package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.APi;
import com.qtfreet.musicuu.model.ApiService;
import com.qtfreet.musicuu.model.DownListener;
import com.qtfreet.musicuu.model.MusicBean;
import com.qtfreet.musicuu.model.resultBean;
import com.qtfreet.musicuu.ui.adapter.SearchResultAdapter;
import com.qtfreet.musicuu.utils.SPUtils;
import com.qtfreet.musicuu.wiget.ActionSheetDialog;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.uiview.UIButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by qtfreet on 2016/3/20.
 */
public class SearchActivity extends AppCompatActivity implements DownListener, SwipeRefreshLayout.OnRefreshListener {

    private SearchResultAdapter searchResultAdapter;

    @Bind(R.id.lv_search_result)
    ListView search_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initview();
        initData();
        mSearchProgressBar.setVisibility(View.VISIBLE);
    }

    private void initData() {
        showRefreshing(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APi.MUSICUU_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<resultBean>> call = apiService.GetInfo(getIntent().getExtras().getString("key"), getIntent().getExtras().getString("type"));
        call.enqueue(new Callback<List<resultBean>>() {

            @Override
            public void onResponse(Call<List<resultBean>> call, Response<List<resultBean>> response) {
                showRefreshing(false);
                result = response.body();
                handler.sendEmptyMessage(REQUEST_SUCCESS);
                mSearchProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<resultBean>> call, Throwable t) {
                showRefreshing(false);
                mSearchProgressBar.setVisibility(View.GONE);
                handler.sendEmptyMessage(REQUEST_ERROR);
            }
        });
    }

    private SwipeRefreshLayout refresh;
    private List<resultBean> result = null;
    @Bind(R.id.pb_search_wait)
    ProgressBar mSearchProgressBar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title_name)
    TextView toolbarTitle;

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_SUCCESS:
                    searchResultAdapter = new SearchResultAdapter(SearchActivity.this, result);
                    search_list.setAdapter(searchResultAdapter);
                    search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            MusicBean.listenUrl = result.get(position).getListenUrl();
                            MusicBean.pic = result.get(position).getPicUrl();
                            MusicBean.time = result.get(position).getLength();
                            MusicBean.videoUrl = result.get(position).getVideoUrl();
                            MusicBean.songName = result.get(position).getSongName();
                            MusicBean.artist = result.get(position).getArtist();
                            Intent i = new Intent(SearchActivity.this, MusicPlayer.class);
                            startActivity(i);
                        }
                    });
                    searchResultAdapter.setDownloadListener(SearchActivity.this);
                    break;
                case REQUEST_ERROR:
                    Toast.makeText(SearchActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void showRefreshing(boolean isShow) {
        if (isShow) {
            refresh.setProgressViewOffset(false, 0, (int) (getResources().getDisplayMetrics().density * 24 +
                    0.5f));
            refresh.setRefreshing(true);
        } else {
            refresh.setRefreshing(false);
        }
    }

    private void initview() {
        ButterKnife.bind(this);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        refresh.setOnRefreshListener(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbarTitle.setText("搜索");
            }
        }
    }


    @Override
    public void Download(View v, int position, final String songId, final String songName, final String artist, final String hqUrl, final String lqUrl, final String sqUrl, final String videoUrl, final String flacUrl, final UIButton btn_down) {

        ActionSheetDialog actionSheetDialog = new ActionSheetDialog(SearchActivity.this).builder();
        actionSheetDialog.setTitle("选择音质");
        if (!lqUrl.equals("")) {
            actionSheetDialog.addSheetItem("标准", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    String type = "";
                    if (lqUrl.contains(".mp3")) {
                        type = ".mp3";
                    }
                    download(songName + "-" + artist + type, lqUrl, songId, btn_down);
                }
            });
        }
        if (!hqUrl.equals("")) {
            actionSheetDialog.addSheetItem("较高", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    String type = "";
                    if (hqUrl.contains(".mp3")) {
                        type = ".mp3";
                    }
                    download(songName + "-" + artist + type, hqUrl, songId, btn_down);


                }
            });
        }
        if (!sqUrl.equals("")) {
            actionSheetDialog.addSheetItem("极高", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    String type = "";
                    if (sqUrl.contains(".mp3")) {
                        type = ".mp3";
                    }
                    download(songName + "-" + artist + type, sqUrl, songId, btn_down);


                }
            });
        }
        if (!flacUrl.equals("")) {
            actionSheetDialog.addSheetItem("无损", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    String type = "";
                    if (flacUrl.contains(".flac")) {
                        type = ".flac";
                    } else if (flacUrl.contains(".ape")) {
                        type = ".ape";

                    } else {
                        type = ".mp3";
                    }
                    download(songName + "-" + artist + type, flacUrl, songId, btn_down);


                }
            });
        }
        if (!videoUrl.equals("")) {
            actionSheetDialog.addSheetItem("MV", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    String type = "";
                    if (videoUrl.contains(".mp4")) {
                        type = ".mp4";
                    }
                    download(songName + "-" + artist + type, videoUrl, songId, btn_down);


                }
            });
        }
        actionSheetDialog.show();
    }


    private static final int REQUEST_SUCCESS = 1;
    private static final int REQUEST_ERROR = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void download(String name, String url, String tag, final UIButton btn) {

        DownloadRequest request = new DownloadRequest.Builder().setTitle(name).setUri(url).setFolder(new File(Environment.getExternalStorageDirectory() + "/" + (String) SPUtils.get("com.qtfreet.musicuu_preferences", this, "SavePath", "musicuu"))).build();
        DownloadManager.getInstance().download(request, tag, new CallBack() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onConnecting() {
                btn.setText("...");
            }

            @Override
            public void onConnected(long total, boolean isRangeSupport) {

            }

            @Override
            public void onProgress(long finished, long total, int progress) {

                DecimalFormat df = new DecimalFormat("######0.00");
//                        Log.e("TAG", df.format((double) finished * 100 / (double) total) + "");
                btn.setText(df.format((double) finished * 100 / (double) total) + "%");
            }

            @Override
            public void onCompleted() {
                btn.setText("完成");
            }

            @Override
            public void onDownloadPaused() {

            }

            @Override
            public void onDownloadCanceled() {

            }

            @Override
            public void onFailed(DownloadException e) {
                btn.setText("失败");
            }
        });
    }

    @Override
    public void onRefresh() {
        initData();
    }
}
