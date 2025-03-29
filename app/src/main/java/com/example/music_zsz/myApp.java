package com.example.music_zsz;

import android.app.Application;

import com.tencent.mmkv.MMKV;

public class myApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String rootDir = MMKV.initialize(this);
        MMKV mmkv = MMKV.defaultMMKV();
        System.out.println("MMKV 初始化路径: " + rootDir);
    }
}
