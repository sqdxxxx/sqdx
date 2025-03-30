package com.example.music_zsz;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<Song>> specialSongs;
    private MutableLiveData<List<Song>> dailySongs;
    private MutableLiveData<List<Song>> popularSongs;

    private final MutableLiveData<List<Song>> playlistLiveData = new MutableLiveData<>(new ArrayList<>());

    public MainViewModel() {
        specialSongs = new MutableLiveData<>();
        dailySongs = new MutableLiveData<>();
        popularSongs = new MutableLiveData<>();
        loadSongs();
    }

    private void loadSongs() {

        List<Song> special = new ArrayList<>();

        specialSongs.setValue(special);

        List<Song> daily = new ArrayList<>();

        dailySongs.setValue(daily);

        List<Song> popular = new ArrayList<>();

        popularSongs.setValue(popular);
    }

    public LiveData<List<Song>> getPlaylist() {
        return playlistLiveData;
    }


    public void addSong(Song song) {
        List<Song> list = playlistLiveData.getValue();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(song);
        playlistLiveData.setValue(list);
    }

    public LiveData<List<Song>> getSpecialSongs() {
        return specialSongs;
    }

    public LiveData<List<Song>> getDailySongs() {
        return dailySongs;
    }

    public LiveData<List<Song>> getPopularSongs() {
        return popularSongs;
    }
}
