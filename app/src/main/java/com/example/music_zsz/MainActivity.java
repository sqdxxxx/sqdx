package com.example.music_zsz;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.music_zsz.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Handler bannerHandler = new Handler();
    private Runnable bannerRunnable;


    private BannerAdapter bannerAdapter;             // moduleConfigId == 1
    private SongAdapter specialAdapter;              // moduleConfigId == 2（专属好歌）
    private SongAdapter dailyAdapter;                // moduleConfigId == 3（一行一列）
    private SongAdapter popularAdapter;              // moduleConfigId == 4（一行两列）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        setupBanner();
        setupSpecialSongs();
        setupDailySongs();
        setupPopularSongs();
        setupSwipeRefresh();


        loadServerData();
    }


    private void loadServerData() {

        String url = "https://hotfix-service-prod.g.mi.com/music/homePage?current=1&size=5";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "加载失败：" + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "请求出错：" + response, Toast.LENGTH_SHORT).show()
                    );
                    return;
                }
                String json = response.body().string();

                ParseGson parser = new ParseGson(url);
                ArrayList<Song> allSongs = parser.getSonglist(json);


                ArrayList<Song> bannerSongs = new ArrayList<>();
                ArrayList<Song> specialSongs = new ArrayList<>();
                ArrayList<Song> dailySongs = new ArrayList<>();
                ArrayList<Song> popularSongs = new ArrayList<>();

                for (Song song : allSongs) {
                    int id = song.getModuleConfigId();
                    if (id == 1) {
                        bannerSongs.add(song);
                    } else if (id == 2) {
                        specialSongs.add(song);
                    } else if (id == 3) {
                        dailySongs.add(song);
                    } else if (id == 4) {
                        popularSongs.add(song);
                    }
                }
                Log.d("MainActivity", "banner:" + bannerSongs.size() +
                        ", special:" + specialSongs.size() +
                        ", daily:" + dailySongs.size() +
                        ", popular:" + popularSongs.size());
                runOnUiThread(() -> {

                    bannerAdapter.updateData(bannerSongs);
                    specialAdapter.updateData(specialSongs);
                    dailyAdapter.updateData(dailySongs);
                    popularAdapter.updateData(popularSongs);
                });
            }
        });
    }

    private void setupBanner() {

        bannerAdapter = new BannerAdapter(new ArrayList<>());
        binding.viewPager.setAdapter(bannerAdapter);

//        if (bannerAdapter.getItemCount() <= 1) {
//            binding.viewPager.setUserInputEnabled(false);
//        } else {
            binding.viewPager.setUserInputEnabled(true);
            bannerRunnable = new Runnable() {
                @Override
                public void run() {
                    int nextItem = (binding.viewPager.getCurrentItem() + 1) % bannerAdapter.getItemCount();
                    binding.viewPager.setCurrentItem(nextItem, true);
                    bannerHandler.postDelayed(this, 3000);
                }
            };
            bannerHandler.postDelayed(bannerRunnable, 3000);

            binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {

                }
            });
//        }
    }

    private void setupSpecialSongs() {
        binding.horizontalRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        specialAdapter = new SongAdapter(new ArrayList<>(), R.layout.item_special_song);
        binding.horizontalRecyclerView.setAdapter(specialAdapter);

    }

    private void setupDailySongs() {

        binding.dailyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dailyAdapter = new SongAdapter(new ArrayList<>(), R.layout.item_daily_song);
        binding.dailyRecyclerView.setAdapter(dailyAdapter);
    }

    private void setupPopularSongs() {

        binding.popularRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        popularAdapter = new SongAdapter(new ArrayList<>(), R.layout.item_popular_song);
        binding.popularRecyclerView.setAdapter(popularAdapter);

    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // 模拟刷新，实际应发起数据刷新请求
            binding.swipeRefreshLayout.postDelayed(() -> {
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
            }, 2000);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }
}
