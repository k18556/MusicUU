package com.qtfreet.musicuu.ui.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.qtfreet.musicuu.R;
import com.qtfreet.musicuu.utils.StorageUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;
/**
 * Created by qtfreet on 2016/3/20.
 */
public class DownloadService extends IntentService {

    int oldprogress = 0;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        String apkurl = intent.getStringExtra("url");
        final String name = apkurl.substring(apkurl.lastIndexOf("/") + 1, apkurl.length());
        final String path = StorageUtils.getCacheDirectory(this).getPath();
        //使用Okhttp下载文件，抛弃之前的httpUrlConnetction，提升效率
        OkHttpUtils//
                .get()//
                .url(apkurl)//
                .build()//
                .execute(new FileCallBack(path, name)//
                {
                    @Override
                    public void inProgress(float progress, long total) {
                        int p = (int) (100 * progress);
                        if (p == oldprogress) {
                            //当进度一样时不更新通知栏，避免过度操作卡顿
                        } else {
                            updateProgress(p);
                        }
                        oldprogress = p;
                        Log.e("TAG", p + "");
                    }


                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(File file) {
                        Log.e("TAG", "onResponse :" + file.getAbsolutePath());

                    }
                });
    }

    private void updateProgress(int progress) {

    }

}
