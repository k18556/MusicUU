package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.utils.FileUtils;
import com.qtfreet.musicuu.utils.SPUtils;
import com.qtfreet.musicuu.wiget.ActionSheetDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.uiview.UIButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initview();
    }

    @Override
    protected void onResume() {
        initDir();
        super.onResume();
    }

    private void initDir() {
        String path = (String) SPUtils.get("com.qtfreet.musicuu_preferences", this, "SavePath", "musicuu");
        Log.e("TAG", path + "                                     11111");

        if (FileUtils.getInstance().isSdCardAvailable()) {
            if (!FileUtils.getInstance().isFileExist(path)) {
                FileUtils.getInstance().creatSDDir(path);
            }
        }
    }

    String musictype = "";
    private UIButton btn_search;

    private void startSearchSong() {
        String text = mSearchEditText.getText().toString();
        if (text.equals("")) {
            Toast.makeText(MainActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putString("key", text);
        bundle.putString("type", musictype);
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }


    @Bind(R.id.ib_search_btn)
    ImageButton mSearchButton;
    @Bind(R.id.et_search_content)
    EditText mSearchEditText;

    private void initview() {
        ButterKnife.bind(this);
        btn_search = (UIButton) findViewById(R.id.btn_search);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        musictype = "wy";
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarTitle.setText(R.string.main_title);
            }
        }
        floatingActionButton.setOnClickListener(this);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchSong();

            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchSong();
            }
        });
    }

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title_name)
    TextView toolbarTitle;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                new ActionSheetDialog(MainActivity.this).builder().setTitle("选择音源").addSheetItem("网易云音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "wy";
                    }
                }).addSheetItem("电信爱音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "dx";
                    }
                }).addSheetItem("5Sing", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "fs";
                    }
                }).addSheetItem("百度音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "bd";
                    }
                }).addSheetItem("天天动听", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "tt";
                    }
                }).addSheetItem("虾米音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "xm";
                    }
                }).addSheetItem("酷我音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "kw";
                    }
                }).addSheetItem("酷狗音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "kg";
                    }
                }).addSheetItem("多米音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "dm";
                    }
                }).addSheetItem("萌否音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "mf";
                    }
                }).addSheetItem("Echo回声", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "echo";
                    }
                }).addSheetItem("QQ音乐", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        musictype = "qq";
                    }
                }).show();
                break;
        }
    }
}
