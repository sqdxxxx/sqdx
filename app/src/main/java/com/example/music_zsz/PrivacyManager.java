package com.example.music_zsz;

import com.tencent.mmkv.MMKV;


public class PrivacyManager {
    private static final String MMKV_ID = "privacy_config";
    private static final String KEY_AGREED = "user_agreed";

    private static volatile PrivacyManager instance;
    private MMKV mmkv;

    private PrivacyManager() {

        mmkv = MMKV.mmkvWithID(MMKV_ID, MMKV.MULTI_PROCESS_MODE);
    }



    public static PrivacyManager getInstance() {
        if (instance == null) {
            synchronized (PrivacyManager.class) {
                if (instance == null) {
                    instance = new PrivacyManager();
                }
            }
        }
        return instance;
    }

    public boolean hasAgreed() {
        return mmkv.decodeBool(KEY_AGREED, false);
    }

    public void setAgreed() {
        mmkv.encode(KEY_AGREED, true);
    }

    public void clear() {
        mmkv.clearAll();
    }
}
