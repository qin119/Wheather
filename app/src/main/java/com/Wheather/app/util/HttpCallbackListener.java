package com.Wheather.app.util;

/**
 * Created by Administrator on 2017/5/31.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
