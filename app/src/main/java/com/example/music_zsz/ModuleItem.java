package com.example.music_zsz;

import java.util.List;

public class ModuleItem {
    public static final int TYPE_SEARCH_BAR = 0;
    public static final int TYPE_BANNER = 1;
    public static final int TYPE_SPECIAL = 2;
    public static final int TYPE_DAILY = 3;
    public static final int TYPE_POPULAR = 4;


    private int moduleType;
    private List<Song> songs;


    public ModuleItem(int moduleType, List<Song> songs) {
        this.moduleType = moduleType;
        this.songs = songs;
    }

    public int getModuleType() {
        return moduleType;
    }

    public List<Song> getSongs() {
        return songs;
    }
}

