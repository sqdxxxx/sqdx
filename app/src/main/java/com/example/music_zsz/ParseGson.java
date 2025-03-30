package com.example.music_zsz;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseGson {
    private String url;

    public ParseGson(String url) {
        this.url = url;
    }

    public ArrayList<Song> getSonglist(String str) {
        ArrayList<Song> songs = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray records = data.getJSONArray("records");

            if (records.length() == 0) {
                Log.d("ParseGson", "records 数组为空");
                return songs;
            }
            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);

                int style = record.getInt("style");

                if (record.has("musicInfoList")) {
                    JSONArray musicInfoList = record.getJSONArray("musicInfoList");
                    for (int j = 0; j < musicInfoList.length(); j++) {
                        JSONObject songObj = musicInfoList.getJSONObject(j);
                        String name = songObj.getString("musicName");
                        String singer = songObj.getString("author");
                        String coverUrl = songObj.getString("coverUrl");
                        String musicUrl = songObj.getString("musicUrl");
                        String lyricUrl = songObj.getString("lyricUrl");
                        Log.d("ParseGson", "music: " + name + ", style: " + style);
                        Song song = new Song(name, singer, coverUrl, musicUrl, lyricUrl, style);
                        songs.add(song);
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return songs;
    }
}
