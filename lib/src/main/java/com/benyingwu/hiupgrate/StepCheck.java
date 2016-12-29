package com.benyingwu.hiupgrate;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.concurrent.Callable;

/**
 * 2016/12/28.
 */
public class StepCheck implements Callable<Boolean> {
    private Context context;
    private Version ver;

    public StepCheck(Context context, Version ver) {
        this.context = context;
        this.ver = ver;
    }

    @Override
    public Boolean call() throws Exception {
        int curCode = getVersionCode(context);
        int dstCode = ver.code;
        return dstCode > curCode;
    }

    public static int getVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }
}
