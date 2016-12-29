package com.benyingwu.hiupgrate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * 2016/12/28.
 */
public class StepInst implements Callable<Boolean> {
    private Context context;
    private Version ver;

    public StepInst(Context context, Version ver) {
        this.context = context;
        this.ver = ver;
    }

    @Override
    public Boolean call() throws Exception {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(ver.apk)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        return true;
    }
}
