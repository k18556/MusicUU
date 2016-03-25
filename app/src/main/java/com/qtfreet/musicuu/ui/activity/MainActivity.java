package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.APi;
import com.qtfreet.musicuu.utils.FileUtils;
import com.qtfreet.musicuu.utils.SPUtils;
import com.qtfreet.musicuu.wiget.ActionSheetDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
//        init();

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
//
//    private void init() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://musicuu.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        GitHubService gitHubService = retrofit.create(GitHubService.class);
//        Call<List<resultBean>> call = gitHubService.GetInfo("i", "wy");
//        call.enqueue(new Callback<List<resultBean>>() {
//            @Override
//            public void onResponse(Call<List<resultBean>> call, Response<List<resultBean>> response) {
//                String art = response.body().get(0).getArtist();
//                Log.e("TAG", art);
//            }
//
//            @Override
//            public void onFailure(Call<List<resultBean>> call, Throwable t) {
//
//            }
//        });
//
//
//    }
//
//    public interface GitHubService {
//        @GET("service/getIpInfo.php")
//        Call<List<resultBean>> GetInfo(@Query("key") String key, @Query("type") String type);
//    }

    private static final int REQUEST_SUCCESS = 1;
    private static final int REQUEST_ERROR = 0;

    String musictype = "";
    private UIButton btn_search;

    private void startSearchSong() {
        String text = mSearchEditText.getText().toString();
        if (text.equals("")) {
            Toast.makeText(MainActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mSearchProgressBar.setVisibility(View.VISIBLE);
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpUtils.get().url(APi.MUSICUU_API + "key=" + text + "&" + "type=" + musictype).build().execute(new StringCallback() {


            @Override
            public void onError(okhttp3.Call call, Exception e) {
                Message msg = Message.obtain();
                msg.what = REQUEST_ERROR;
                msg.obj = e.toString();
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(String response) {

                Message msg = Message.obtain();
                msg.what = REQUEST_SUCCESS;
                msg.obj = response;
                handler.sendMessage(msg);

            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case REQUEST_SUCCESS:
                    mSearchProgressBar.setVisibility(View.GONE);
                    if (msg.obj != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("data", msg.obj.toString());
                        Log.e("TAG", msg.obj.toString());
                        Intent i = new Intent(MainActivity.this, SearchActivity.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                    break;
                case REQUEST_ERROR:
                    break;
                case 2333:

                    break;
            }
            return true;
        }
    });

    @Bind(R.id.ib_search_btn)
    ImageButton mSearchButton;
    @Bind(R.id.et_search_content)
    EditText mSearchEditText;
    @Bind(R.id.pb_search_wait)
    ProgressBar mSearchProgressBar;


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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
