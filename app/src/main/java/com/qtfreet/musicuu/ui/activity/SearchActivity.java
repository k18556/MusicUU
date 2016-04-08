package com.qtfreet.musicuu.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.APi;
import com.qtfreet.musicuu.model.ApiService;
import com.qtfreet.musicuu.model.DownListener;
import com.qtfreet.musicuu.model.MusicBean;
import com.qtfreet.musicuu.model.resultBean;
import com.qtfreet.musicuu.ui.adapter.SearchResultAdapter;
import com.qtfreet.musicuu.utils.DownloadUtil;
import com.qtfreet.musicuu.utils.SPUtils;
import com.qtfreet.musicuu.wiget.ActionSheetDialog;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.uiview.UIButton;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by qtfreet on 2016/3/20.
 */
public class SearchActivity extends AppCompatActivity implements DownListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SearchResultAdapter searchResultAdapter;

    @Bind(R.id.lv_search_result)
    ListView search_list;
    private static final int REQUECT_CODE_SDCARD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        MPermissions.requestPermissions(SearchActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        initview();
        initData();
        firstuse();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @PermissionGrant(REQUECT_CODE_SDCARD)
    public void requestSdcardSuccess() {

    }

    @PermissionDenied(REQUECT_CODE_SDCARD)
    public void requestSdcardFailed() {
        Toast.makeText(this, "未获取到SD卡权限!", Toast.LENGTH_SHORT).show();

    }

    private void initData() {
        showRefreshing(true);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APi.MUSIC_HOST).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<resultBean>> call = apiService.GetInfo(getIntent().getExtras().getString("type"), getIntent().getExtras().getString("key"));
        call.enqueue(new Callback<List<resultBean>>() {
            @Override
            public void onResponse(Call<List<resultBean>> call, Response<List<resultBean>> response) {
                showRefreshing(false);
                if (response.body().size() == 0) {
                    handler.sendEmptyMessage(REQUEST_ERROR);
                    return;
                }
                result = response.body();
                handler.sendEmptyMessage(REQUEST_SUCCESS);

            }

            @Override
            public void onFailure(Call<List<resultBean>> call, Throwable t) {
                showRefreshing(false);

                handler.sendEmptyMessage(REQUEST_ERROR);
            }
        });
    }

    private SwipeRefreshLayout refresh;
    private List<resultBean> result = null;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title_name)
    TextView toolbarTitle;

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_SUCCESS:
                    if (result == null) {
                        handler.sendEmptyMessage(REQUEST_ERROR);
                        return true;
                    }
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
                            MusicBean.hqUrl = result.get(position).getHqUrl();
                            MusicBean.sqUrl = result.get(position).getSqUrl();
                            MusicBean.lqUrl = result.get(position).getLqUrl();
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
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

    private void firstuse() {
        boolean isfirst = (boolean) SPUtils.get(this, "isdownload", true);
        if (isfirst) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提示");

            builder.setMessage("当前版本的下载功能还不完善，所以麻烦大家在下载期间请勿随意切换页面。");
            builder.setCancelable(false);
            builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SPUtils.put(SearchActivity.this, "isdownload", false);
                }
            });
            builder.show();
        }
    }

    @Override
    public void Download(View v, int position, final String songId, final String songName, final String artist, final String hqUrl, final String lqUrl, final String sqUrl, final String videoUrl, final String flacUrl, final UIButton btn_down) {
        int level = (int) SPUtils.get(this, "music_level", 3);
        if (level == 3) {
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
                        download(songName + "-" + artist + "-L" + type, lqUrl, songId, btn_down);
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
                        download(songName + "-" + artist + "-H" + type, hqUrl, songId, btn_down);


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
                        download(songName + "-" + artist + "-S" + type, sqUrl, songId, btn_down);


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
        } else {
            if (level == 0 && !lqUrl.equals("")) {
                String type = "";
                if (lqUrl.contains(".mp3")) {
                    type = ".mp3";
                }
                download(songName + "-" + artist + "-L" + type, lqUrl, songId, btn_down);
            } else if (level == 1 && !hqUrl.equals("")) {
                String type = "";
                if (hqUrl.contains(".mp3")) {
                    type = ".mp3";
                }
                download(songName + "-" + artist + "-H" + type, hqUrl, songId, btn_down);
            } else if (level == 2 && !sqUrl.equals("")) {
                String type = "";
                if (sqUrl.contains(".mp3")) {
                    type = ".mp3";
                }
                download(songName + "-" + artist + "-S" + type, sqUrl, songId, btn_down);
            }

        }
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

        DownloadUtil.StartDownload(this, name, url, tag, btn);

    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("默认音质");
                String[] itmes = {"标准", "较高", "极高", "清除设置"};
                builder.setItems(itmes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPUtils.put(SearchActivity.this, "music_level", which);
                    }
                });
                builder.create().show();
                break;
        }
    }
}
