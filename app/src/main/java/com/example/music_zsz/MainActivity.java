package com.example.music_zsz;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_zsz.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainRecyclerAdapter mainAdapter;


    private int currentPage = 1;
    private boolean isLoading = false;

    private Handler bannerHandler = new Handler();
    private Runnable bannerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        mainAdapter = new MainRecyclerAdapter(this, bannerHandler);


        binding.mainRecyclerView.setAdapter(mainAdapter);
        binding.mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        binding.mainRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                int lastVisiblePosition = lm.findLastCompletelyVisibleItemPosition();
                int totalCount = mainAdapter.getItemCount();
                // 当滑到最后一个条目时，执行加载更多
                if (!isLoading && lastVisiblePosition == totalCount - 1) {
                    loadMoreData();
                }
            }
        });


        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.postDelayed(() -> {
                binding.swipeRefreshLayout.setRefreshing(false);
                currentPage = 1;
                mainAdapter.clearAll(); // 清空所有模块
                loadServerData(currentPage);
                Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
            }, 1000);
        });

        // 第一次加载第一页
        loadServerData(currentPage);
    }

    private void loadMoreData() {
        isLoading = true;
        currentPage++;
        loadServerData(currentPage);
    }

    private void loadServerData(int page) {
        //size代表目前有几个模块，刚开始是4个模块，所以page是1，size是4
        int size = page+3;
        String url = "https://hotfix-service-prod.g.mi.com/music/homePage?current=1" + "&size="+ size;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    isLoading = false;
                    Toast.makeText(MainActivity.this, "请求失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        isLoading = false;
                        Toast.makeText(MainActivity.this, "请求出错：" + response, Toast.LENGTH_SHORT).show();
                    });
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
                    int style = song.getStyle();
                    switch (style) {
                        case 1: bannerSongs.add(song); break;
                        case 2: specialSongs.add(song); break;
                        case 3: dailySongs.add(song); break;
                        case 4: popularSongs.add(song); break;
                    }
                }

                runOnUiThread(() -> {
                    if (page == 1) {
                        Log.d("init", "page=" + page
                                + ", bannerSongs=" + bannerSongs.size()
                                + ", specialSongs=" + specialSongs.size()
                                + ", dailySongs=" + dailySongs.size()
                                + ", popularSongs=" + popularSongs.size());

                        List<ModuleItem> initModules = new ArrayList<>();


                        initModules.add(new ModuleItem(ModuleItem.TYPE_SEARCH_BAR, null));

                        initModules.add(new ModuleItem(ModuleItem.TYPE_BANNER, bannerSongs));

                        initModules.add(new ModuleItem(ModuleItem.TYPE_SPECIAL, specialSongs));

                        initModules.add(new ModuleItem(ModuleItem.TYPE_DAILY, dailySongs));

                        initModules.add(new ModuleItem(ModuleItem.TYPE_POPULAR, popularSongs));

                        mainAdapter.setModuleItems(initModules);

                    } else {
                        //上拉加载可自定义添加上面模块，我这里写的是添加每日推荐和热门金曲

                        ArrayList<Song> Daily = new ArrayList<>();
                        Daily.addAll(dailySongs);
                        ArrayList<Song> Popular = new ArrayList<>();
                        Popular.addAll(popularSongs);

                        Log.d("add", "page=" + page
                                + ", dailySongs=" + dailySongs.size()
                                + ", popularSongs=" + popularSongs.size());



                        ModuleItem moreDaily = new ModuleItem(ModuleItem.TYPE_DAILY, Daily);
                        ModuleItem morePopular = new ModuleItem(ModuleItem.TYPE_POPULAR, Popular);

                        mainAdapter.addModuleItem(moreDaily);
                        mainAdapter.addModuleItem(morePopular);
                    }

                    isLoading = false;
                });
            }
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
