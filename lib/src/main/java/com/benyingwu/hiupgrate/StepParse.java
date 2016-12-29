package com.benyingwu.hiupgrate;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * 2016/12/28.
 */

public class StepParse implements Callable<Version> {
    String content;

    public StepParse(String content) {
        this.content = content;
    }

    @Override
    public Version call() throws Exception {
        Version version = HiUpgrate.getInstance().mParse.parse(content);
        return version;
    }
}
