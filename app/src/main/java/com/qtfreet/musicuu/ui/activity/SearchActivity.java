package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.MusicBean;
import com.qtfreet.musicuu.model.resultBean;
import com.qtfreet.musicuu.ui.adapter.ContentAdapter;
import com.qtfreet.musicuu.wiget.ActionSheetDialog;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qtfreet on 2016/3/20.
 */
public class SearchActivity extends AppCompatActivity {

    private ContentAdapter mAdapter;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

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
            toolbar.setTitle("");
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                toolbarTitle.setText("搜索");
            }
        }
        String data = getIntent().getExtras().getString("data");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Type listType = new TypeToken<List<resultBean>>() {
        }.getType();
        Gson gson = new Gson();
        result = gson.fromJson(data, listType);
        for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
            resultBean resource = (resultBean) iterator.next();
            System.out.println("musicName-->" + resource.getSongName());
        }
        mAdapter = new ContentAdapter(this, result);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

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
        mAdapter.setDownloadMusicListener(new ContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                new ActionSheetDialog(SearchActivity.this).builder().setTitle("选择音质").addSheetItem("标准", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        downUrl = result.get(position).getLqUrl();
                        Log.e("TAG", downUrl);

                    }
                }).addSheetItem("较高", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        downUrl = result.get(position).getHqUrl();
                        Log.e("TAG", downUrl);
                    }
                }).addSheetItem("极高", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        downUrl = result.get(position).getSqUrl();
                        Log.e("TAG", downUrl);
                    }
                }).addSheetItem("无损", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        downUrl = result.get(position).getFlacUrl();
                        Log.e("TAG", downUrl);
                    }
                }).show();
            }
        });
    }

    String downUrl = "";


}
