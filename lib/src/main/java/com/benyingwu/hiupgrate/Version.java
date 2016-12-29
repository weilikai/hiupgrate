package com.benyingwu.hiupgrate;

/**
 * 2016/12/28.
 */

public class Version {
    public int code;
    public String addr;
    public String apk;
    public String tit;
    public String msg;

    @Override
    public String toString() {
        return "Version{" +
                "code=" + code +
                "apk=" + apk +
                ", addr='" + addr + '\'' +
                '}';
    }

    public String getTitle() {
        if (null == tit) {
            return "发现新版本";
        }
        return tit;
    }

    public String getMessage() {
        if (null == msg) {
            return "建议升级";
        }
        return msg;
    }
}
