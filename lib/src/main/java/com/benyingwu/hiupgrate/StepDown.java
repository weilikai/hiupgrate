package com.benyingwu.hiupgrate;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * 2016/12/28.
 */

public class StepDown implements Callable<String> {
    String url;

    public StepDown(String url) {
        this.url = url;
    }

    @Override
    public String call() throws Exception {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            return new Scanner(connection.getInputStream()).useDelimiter("\\A").next();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
