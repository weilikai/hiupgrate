package com.benyingwu.upgrate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.benyingwu.hiupgrate.HiUpgrate;
import com.benyingwu.hiupgrate.Version;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HiUpgrate.getInstance()
                .setParse(new HiUpgrate.Parse() {
                    @Override
                    public Version parse(String data) throws Exception {
                        Version version = new Version();
                        version.code = 2;
                        version.addr = "http://apk1.lenovomm.com/961e2e8a7afcf0fbdb613f71f332408e/5863992d/dlserver/fileman/s3/apk/app/app-apk-lestore/4110-2016-06-23103852-1466692732582.apk?v=5&clientid=15808-1a2-2-9999-1-3-1_240_iamp5UID8c43b4e62aafb31d4a2c652fat19700102059182702_c303d1p30&pn=com.nihuawocai";
                        return version;
                    }
                })
                .check(this, "http://baidu.com");
    }
}
