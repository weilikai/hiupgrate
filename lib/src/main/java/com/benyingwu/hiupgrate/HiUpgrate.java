package com.benyingwu.hiupgrate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TimingLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 2016/12/28.
 */
public final class HiUpgrate {
    private static HiUpgrate ourInstance = new HiUpgrate();

    public static HiUpgrate getInstance() {
        return ourInstance;
    }

    private HiUpgrate() {
    }

    ExecutorService service = Executors.newSingleThreadExecutor();


    public void check(final Context context, final String url) {
        new Thread() {
            @Override
            public void run() {
                _check(context, url);
            }
        }.start();
    }

    public static interface Parse {
        Version parse(String data) throws Exception;
    }

    class ParseImpl implements Parse {
        public Version parse(String data) throws Exception {
            JSONObject obj = new JSONObject(data);
            Version version = new Version();
            version.code = obj.getInt("code");
            version.addr = obj.getString("addr");
            return version;
        }
    }

    Parse mParse = new ParseImpl();

    public HiUpgrate setParse(Parse parse) {
        mParse = parse;
        return this;
    }

    private void _check(final Context context, String url) {
        Log.d("hiupgrate", "end");
        final TimingLogger timing = new TimingLogger("hiupgrate", "check");
        try {
            // ### 检测更新
            timing.addSplit("begin\t down version");
            final String data = service.submit(new StepDown(url)).get(30, TimeUnit.SECONDS);
            timing.addSplit("end\t down version data, " + data);

            // ### 解析数据
            timing.addSplit("begin\t parse version data");
            final Version ver = service.submit(new com.benyingwu.hiupgrate.StepParse(data)).get(30, TimeUnit.SECONDS);
            timing.addSplit("end\t parse version data, " + ver);

            // ### 版本比较
            timing.addSplit("begin\t compare version");
            boolean cont = service.submit(new StepCheck(context, ver)).get();
            timing.addSplit("end\t compare version, need up=" + cont);

            if (cont) {
                cont = service.submit(new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        final boolean[] yes = new boolean[1];

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(context)
                                        .setCancelable(false)
                                        .setTitle(ver.getTitle())
                                        .setMessage(ver.getMessage())
                                        .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                yes[0] = false;
                                                synchronized (yes) {
                                                    yes.notifyAll();
                                                }
                                            }
                                        })
                                        .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                yes[0] = true;
                                                synchronized (yes) {
                                                    yes.notifyAll();
                                                }
                                            }
                                        })
                                        .create()
                                        .show();
                            }

                        });
                        synchronized (yes) {
                            yes.wait();
                        }
                        return yes[0];
                    }
                }).get();
            }
            if (cont) {
                // ### apk下载
                timing.addSplit("begin\t download apk");
                final String apkLocalPath = service.submit(new StepApkDown(context, ver.addr)).get();
                ver.apk = apkLocalPath;
                timing.addSplit("end\t download apk, " + apkLocalPath);

                // ### 执行安装
                timing.addSplit("begin\t inst apk " + ver);
                final boolean ok = service.submit(new StepInst(context, ver)).get();
                timing.addSplit("end\t inst apk, " + apkLocalPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            timing.addSplit("fail, " + e);
        } finally {
            timing.dumpToLog();
            Log.d("hiupgrate", "end");
        }
    }
}
