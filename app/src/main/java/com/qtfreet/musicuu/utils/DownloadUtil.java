package com.qtfreet.musicuu.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.qtfreet.musicuu.wiget.AlertDialog;

import java.io.File;
import java.text.DecimalFormat;

import me.drakeet.uiview.UIButton;

/**
 * Created by qtfreet on 2016/3/26.
 */
public class DownloadUtil {
    public static void StartDownload(Context context, final String name, final String url, final String tag, final UIButton btn) {
        final String path = Environment.getExternalStorageDirectory() + "/" + SPUtils.get("com.qtfreet.musicuu_preferences", context, "SavePath", "musicuu");
        Log.e("TAG",path+"");
        final File file = new File(path+"/"+name);
        if (file.exists()){
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);
            dialog.setTitle("提示");
            dialog.setMessage("文件已存在，是否需要重新下载？");
            dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    download(name, url, new File(path), tag, btn);
                }
            });
            dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            dialog.show();
        }
        else {
            download(name, url, new File(path), tag, btn);
        }


    }

    private static void download(String name, String url, File file, String tag, final UIButton btn) {
        DownloadRequest request = new DownloadRequest.Builder().setTitle(name).setUri(url).setFolder(file).build();


        DownloadManager.getInstance().download(request, tag, new CallBack() {
            @Override
            public void onStarted() {

//                downloadStatus.setStatus(DownloadStatus.STATUS_STARTED);

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
                btn.setText(progress + "%");
            }

            @Override
            public void onCompleted() {
                btn.setText("完成");
            }

            @Override
            public void onDownloadPaused() {
                btn.setText("暂停");
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
