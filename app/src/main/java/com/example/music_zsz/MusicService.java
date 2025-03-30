package com.example.music_zsz;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    public enum PlayMode { LOOP, SINGLE, SHUFFLE }

    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private int currentIndex = 0;
    private PlayMode playMode = PlayMode.LOOP; // 默认顺序播放
    private final IBinder binder = new LocalBinder();
    private static final String CHANNEL_ID = "MusicPlayerChannel";
    private static final int NOTIFICATION_ID = 1;

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        createNotificationChannel();
        // 启动前台服务，通知栏可根据需要自定义
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("音乐播放")
                .setContentText("正在播放音乐")
                .setSmallIcon(R.drawable.ic_02) // 请确保项目中存在该图标
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // 设置播放列表，调用前请确保 songList 非空
    public void setSongList(List<Song> list) {
        this.songList = list;
        Log.d("setsonglist","success in musicService");
    }
    public void addSong(Song song) {
        if (this.songList == null) {
            this.songList = new ArrayList<>();
        }
        this.songList.add(0,song);
        currentIndex = 0;
    }

    // 播放指定索引的歌曲
    public void playSongAt(int index) {
        if (songList == null || songList.isEmpty()) return;
        if (index < 0 || index >= songList.size()) return;
        currentIndex = index;
        Song song = songList.get(currentIndex);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getMusicUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            updateNotification(song);
            // 更新当前播放的歌曲到 Repository
            SongRepository.getInstance().setCurrentSong(song);
        } catch (IOException e) {
            Log.e("MusicService", "播放失败：" + e.getMessage());
        }
    }


    // 切换到下一首
    public void playNext() {
        if (songList == null || songList.isEmpty()) return;
        switch (playMode) {
            case LOOP:
                currentIndex = (currentIndex + 1) % songList.size();
                break;
            case SINGLE:
                // 单曲循环时，重新播放当前歌曲
                break;
            case SHUFFLE:
                currentIndex = new Random().nextInt(songList.size());
                break;
        }
        playSongAt(currentIndex);
    }

    // 切换到上一首
    public void playPrevious() {
        if (songList == null || songList.isEmpty()) return;
        switch (playMode) {
            case LOOP:
                currentIndex = (currentIndex - 1 + songList.size()) % songList.size();
                break;
            case SINGLE:
                // 单曲循环时，重新播放当前歌曲
                break;
            case SHUFFLE:
                currentIndex = new Random().nextInt(songList.size());
                break;
        }
        playSongAt(currentIndex);
    }

    // 播放或暂停切换
    public void togglePlayPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }


    public int getDuration() {
        return mediaPlayer.getDuration();
    }


    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public void setPlayMode(PlayMode mode) {
        this.playMode = mode;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if (playMode == PlayMode.SINGLE) {
            playSongAt(currentIndex);
        } else {
            playNext();
        }
    }

    private void updateNotification(Song song) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("正在播放：" + song.getName())
                .setContentText("歌手：" + song.getSinger())
                .setSmallIcon(R.drawable.ic_02)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "音乐播放";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("音乐播放服务通知");
            channel.enableLights(false);
            channel.enableVibration(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}

