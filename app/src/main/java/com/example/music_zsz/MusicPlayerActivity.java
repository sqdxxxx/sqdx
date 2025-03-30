package com.example.music_zsz;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.palette.graphics.Palette;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;

import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {

    private MusicService musicService;
    private boolean serviceBound = false;
    private Handler handler = new Handler();
    private ObjectAnimator animator;
    private ViewPager2 viewPager;
    private MusicPagerAdapter pagerAdapter;



    private ImageView coverImageView, playModel, prevImageView, playPauseImageView, nextImageView, itemlist, closeButton, likeImageView;
    private TextView songNameTextView, singerTextView, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;

    // 播放进度更新任务
    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (serviceBound && musicService != null && musicService.isPlaying()) {
                int currentPos = musicService.getCurrentPosition();
                seekBar.setProgress(currentPos);
                tvCurrentTime.setText(formatTime(currentPos));
            }
            handler.postDelayed(this, 1000);
        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            serviceBound = true;
            List<Song> currentList = SongRepository.getInstance().getPlaylist().getValue();
            if (currentList != null) {
                musicService.setSongList(currentList);
                Log.d("setsonglist", "success in onServiceConnected");
            }
            initMusicPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new MusicPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        initCommonViews();
        setClickListeners();

        // 绑定服务
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        SongRepository.getInstance().getPlaylist().observe(this, songs -> {
            Log.d("Repository", "Playlist size: " + (songs != null ? songs.size() : "null"));
            if (musicService != null) {
                musicService.setSongList(songs);
                Log.d("setsonglist","success in observe");

            }
        });
        SongRepository.getInstance().getCurrentSong().observe(this, newSong -> {
            if (newSong != null) {
                updateUIForSong(newSong);

                Log.d("MusicPlayerActivity", "当前播放歌曲更新：" + newSong.getName());
            }
        });
    }

    // 初始化所有控件
    private void initCommonViews() {
        coverImageView = findViewById(R.id.coverImageView);
        songNameTextView = findViewById(R.id.songNameTextView);
        singerTextView = findViewById(R.id.singerTextView);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        seekBar = findViewById(R.id.seekBar);
        playModel = findViewById(R.id.playModel);
        prevImageView = findViewById(R.id.prevImageView);
        playPauseImageView = findViewById(R.id.playPauseImageView);
        nextImageView = findViewById(R.id.nextImageView);
        likeImageView  =findViewById(R.id.likeImageView);
        itemlist = findViewById(R.id.itemlist);
        closeButton = findViewById(R.id.close);
    }


    private void setClickListeners() {

        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, R.anim.anim_exit_bottom);
            });
        }


        playModel.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                MusicService.PlayMode currentMode = musicService.getPlayMode();
                switch (currentMode) {
                    case LOOP:
                        musicService.setPlayMode(MusicService.PlayMode.SINGLE);
                        playModel.setImageResource(R.drawable.ic_play03); // 更新图标
                        Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                        break;
                    case SINGLE:
                        musicService.setPlayMode(MusicService.PlayMode.SHUFFLE);
                        playModel.setImageResource(R.drawable.ic_play02);
                        Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                        break;
                    case SHUFFLE:
                        musicService.setPlayMode(MusicService.PlayMode.LOOP);
                        playModel.setImageResource(R.drawable.ic_play01);
                        Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        prevImageView.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                musicService.playPrevious();
            }
        });



        playPauseImageView.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                musicService.togglePlayPause();

                CoverFragment coverFragment = (CoverFragment) pagerAdapter.getFragmentAt(0);
                if (musicService.isPlaying()) {
                    playPauseImageView.setImageResource(R.drawable.ic_pause);
                    coverFragment.resumeAnimation();

                } else {
                    playPauseImageView.setImageResource(R.drawable.ic_play);
                    coverFragment.pauseAnimation();

                }
            }
        });


        nextImageView.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                musicService.playNext();
            }
        });

        likeImageView.setOnClickListener(v -> {
            SongRepository.getInstance().toggleLikeForCurrentSong();
        });


        itemlist.setOnClickListener(v -> {

            List<Song> songQueue = SongRepository.getInstance().getPlaylist().getValue();
            if (songQueue == null || songQueue.isEmpty()) {
                Toast.makeText(this, "播放队列为空", Toast.LENGTH_SHORT).show();
                return;
            }


            String[] songNames = new String[songQueue.size()];
            for (int i = 0; i < songQueue.size(); i++) {
                songNames[i] = songQueue.get(i).getName();
            }


            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("播放队列")
                    .setItems(songNames, (dialog, which) -> {

                        if (musicService != null) {
                            musicService.playSongAt(which);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });





        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            boolean userSeeking = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (serviceBound && musicService != null) {
                    musicService.seekTo(seekBar.getProgress());
                }
                userSeeking = false;
            }
        });
    }

    public void initViewPager(){
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new MusicPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }


    private void initMusicPlayer() {
        // 获取从mainactivity那里点击然后传入到这里来的歌曲
        Song firstSong = (Song) getIntent().getParcelableExtra("song");
        Log.d("getMusicUrl:",firstSong.getMusicUrl());

        if (firstSong != null) {
            updateUIForSong(firstSong);



            // 播放点击的第一个封面，并把它放到播放列表的第一项
            musicService.addSong(firstSong);
            musicService.playSongAt(0);



            handler.postDelayed(() -> {
                if (musicService != null) {
                    seekBar.setMax(musicService.getDuration());
                    tvTotalTime.setText(formatTime(musicService.getDuration()));
                }
            }, 1000);


            handler.post(updateProgressRunnable);
        }
    }
    private void updateUIForSong(Song song) {
        songNameTextView.setText(song.getName());
        singerTextView.setText(song.getSinger());
        if(song.getLike()){
            likeImageView.setImageResource(R.drawable.ic_liked);
        }else{
            likeImageView.setImageResource(R.drawable.ic_like);
        }



        ConstraintLayout Layout = findViewById(R.id.activity_music_player);
        Glide.with(this)
                .asBitmap()
                .load(song.getCoverUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(palette -> {
                            int dominantColor = palette.getDominantColor(Color.BLACK);
                            Layout.setBackgroundColor(dominantColor);
                        });
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });
    }

    public MusicService getMusicService() {
        return musicService;
    }





    private String formatTime(int millis) {
        int minutes = millis / 60000;
        int seconds = (millis % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        handler.removeCallbacks(updateProgressRunnable);
    }
}
