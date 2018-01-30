package com.oneway.tools.redirector.web.controller;

import java.util.List;

/**
 * Created by yaoren on 2018/1/9 0009.
 */
public class Result {
    private boolean success;
    private String msg;
    private List<String> urls;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
