package com.benyingwu.hiupgrate;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * 2016/12/28.
 */

public class StepApkDown implements Callable<String> {
    private Context context;
    String url;

    public StepApkDown(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public String call() throws Exception {
        try {
            File dst = new File(Environment.getExternalStorageDirectory(), "" + url.hashCode() + ".apk");
            String serviceString = Context.DOWNLOAD_SERVICE;
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(serviceString);

            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationUri(Uri.fromFile(dst));

//            request.setTitle("");
// request.setDescription("");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//request.setMimeType("application/download.file");

            long reference = downloadManager.enqueue(request);

            for (; ; ) {
                int[] st = getBytesAndStatus(downloadManager, reference);
                Log.d("hiupgrate", Arrays.toString(st));

                Thread.sleep(500);

                if (st[2] == DownloadManager.STATUS_FAILED) {
                    throw new Exception("down fail");
                }

                if (st[2] == DownloadManager.STATUS_SUCCESSFUL) {
                    break;
                }
            }
            return dst.getAbsolutePath();
        } catch (Exception e) {
            throw e;
        }
    }

    public int[] getBytesAndStatus(DownloadManager downloadManager, long downloadId) {
        int[] bytesAndStatus = new int[]{-1, -1, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }
}
