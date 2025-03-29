package com.example.music_zsz;

public class Song {
    private String name;
    private String singer;
    private String cover_url;
    private int moduleConfigId;

    public Song(String name, String singer,String cover_url,int moduleConfigId) {
        this.name = name;
        this.singer = singer;
        this.cover_url = cover_url;
        this.moduleConfigId = moduleConfigId;
    }

    public int getModuleConfigId() {return moduleConfigId;}
    public String getName() {return name;}
    public String getSinger() {
        return singer;
    }
    public String getCoverUrl() {
        return cover_url;
    }






}
