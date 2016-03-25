package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.DownListener;
import com.qtfreet.musicuu.model.MusicBean;
import com.qtfreet.musicuu.model.resultBean;
import com.qtfreet.musicuu.ui.adapter.SearchResultAdapter;
import com.qtfreet.musicuu.utils.SPUtils;
import com.qtfreet.musicuu.utils.StorageUtils;
import com.qtfreet.musicuu.wiget.ActionSheetDialog;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.uiview.UIButton;

/**
 * Created by qtfreet on 2016/3/20.
 */
public class SearchActivity extends AppCompatActivity implements DownListener {

    private SearchResultAdapter searchResultAdapter;

    @Bind(R.id.lv_search_result)
    ListView search_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initview();
    }

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title_name)
    TextView toolbarTitle;
    private List<resultBean> result;

    private void initview() {
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbarTitle.setText("搜索");
            }
        }
        String data = getIntent().getExtras().getString("data");

        Type listType = new TypeToken<List<resultBean>>() {
        }.getType();
        Gson gson = new Gson();
        result = gson.fromJson(data, listType);
        for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
            resultBean resource = (resultBean) iterator.next();
            System.out.println("musicName-->" + resource.getSongName());
        }
        searchResultAdapter = new SearchResultAdapter(this, result);
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


        searchResultAdapter.setDownloadListener(this);
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
}
