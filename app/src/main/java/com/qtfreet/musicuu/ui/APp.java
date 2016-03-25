package com.qtfreet.musicuu.ui;

import android.app.Application;

import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadManager;

/**
 * Created by qtfreet on 2016/3/21.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initDownloader();
    }

    private void initDownloader() {
        DownloadConfiguration configuration = new DownloadConfiguration();
        configuration.setMaxThreadNum(10);
        configuration.setThreadNum(3);
        DownloadManager.getInstance().init(getApplicationContext(), configuration);
    }

}
