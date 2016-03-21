package com.qtfreet.musicuu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.model.APi;
import com.qtfreet.musicuu.model.resultBean;
import com.qtfreet.musicuu.ui.adapter.ContentAdapter;
import com.qtfreet.musicuu.utils.util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initview();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchSong();
            }
        });
    }


    private static final int REQUEST_SUCCESS = 1;
    private static final int REQUEST_ERROR = 0;

    String musictype = "";

    private void startSearchSong() {
        String text = search_text.getText().toString();
        if (text.equals("")) {
            Toast.makeText(MainActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OkHttpUtils.get().url(APi.MUSICUU_API + "key=" + text + "&" + "type=" + musictype).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e) {

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


    private void initview() {
        ButterKnife.bind(this);
//        musictype = util.music_type(MainActivity.this, 0);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarTitle.setText(R.string.main_title);
            }
        }
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                musictype = util.music_type(MainActivity.this, position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Bind(R.id.search_text)
    EditText search_text;
    @Bind(R.id.type)
    Spinner type;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
