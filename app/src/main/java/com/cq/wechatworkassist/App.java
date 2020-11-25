package com.cq.wechatworkassist;

import android.app.Application;

import static com.cq.wechatworkassist.wework.WeworkUIKt.initWework;

public class App extends Application {
    public static App mApp;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initWework(this);
    }
}
