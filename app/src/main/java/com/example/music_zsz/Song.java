package com.example.music_zsz;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String name;
    private String singer;
    private String cover_url;
    private String music_url;
    private String lyric_url;
    private int style;

    private boolean isLike;
    public Song(String name, String singer, String cover_url, String music_url,String lyric_url,int style) {
        this.name = name;
        this.singer = singer;
        this.cover_url = cover_url;
        this.music_url = music_url;
        this.lyric_url = lyric_url;
        this.style = style;
        this.isLike = false;
    }

    // 用于从 Parcel 中读取数据
    protected Song(Parcel in) {
        name = in.readString();
        singer = in.readString();
        cover_url = in.readString();
        music_url = in.readString();
        lyric_url = in.readString();
        style = in.readInt();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getStyle() {
        return style;
    }

    public String getName() {
        return name;
    }

    public String getSinger() {
        return singer;
    }

    public String getCoverUrl() {
        return cover_url;
    }

    public String getMusicUrl() {
        return music_url;
    }
    public String getLyricUrl() {
        return lyric_url;
    }

    public boolean getLike() {
        return isLike;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // 将对象写入 Parcel 中
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(singer);
        dest.writeString(cover_url);
        dest.writeString(music_url);
        dest.writeString(lyric_url);
        dest.writeInt(style);
    }
}
