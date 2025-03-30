package com.example.music_zsz;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final Handler bannerHandler;


    private final List<ModuleItem> moduleItemList = new ArrayList<>();


    private Runnable bannerRunnable;

    public MainRecyclerAdapter(Context context, Handler bannerHandler) {
        this.inflater = LayoutInflater.from(context);
        this.bannerHandler = bannerHandler;
    }




    public void clearAll() {
        moduleItemList.clear();
        notifyDataSetChanged();
    }


    public void addModuleItem(ModuleItem item) {
        moduleItemList.add(item);
        notifyItemInserted(moduleItemList.size() - 1);
    }


    public void setModuleItems(List<ModuleItem> newList) {
        moduleItemList.clear();
        if (newList != null) {
            moduleItemList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return moduleItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return moduleItemList.get(position).getModuleType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ModuleItem.TYPE_SEARCH_BAR: {
                View view = inflater.inflate(R.layout.item_search_bar, parent, false);
                return new SearchBarViewHolder(view);
            }
            case ModuleItem.TYPE_BANNER: {
                View view = inflater.inflate(R.layout.item_banner_container, parent, false);
                return new BannerViewHolder(view);
            }
            case ModuleItem.TYPE_SPECIAL: {
                View view = inflater.inflate(R.layout.item_special_container, parent, false);
                return new SpecialViewHolder(view);
            }
            case ModuleItem.TYPE_DAILY: {
                View view = inflater.inflate(R.layout.item_daily_container, parent, false);
                return new DailyViewHolder(view);
            }
            case ModuleItem.TYPE_POPULAR: {
                View view = inflater.inflate(R.layout.item_popular_container, parent, false);
                return new PopularViewHolder(view);
            }

        }
        // 当 viewType 不匹配上面所有定义的类型时，默认加载 item_daily_container 并返回一个 DailyViewHolder，以确保 onCreateViewHolder 方法总是返回一个非空的 ViewHolder
        View view = inflater.inflate(R.layout.item_daily_container, parent, false);
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModuleItem item = moduleItemList.get(position);
        switch (item.getModuleType()) {
            case ModuleItem.TYPE_SEARCH_BAR:
                break;
            case ModuleItem.TYPE_BANNER:
                ((BannerViewHolder) holder).bindData(item.getSongs());
                break;
            case ModuleItem.TYPE_SPECIAL:
                ((SpecialViewHolder) holder).bindData(item.getSongs());
                break;
            case ModuleItem.TYPE_DAILY:
                ((DailyViewHolder) holder).bindData(item.getSongs());
                break;
            case ModuleItem.TYPE_POPULAR:
                ((PopularViewHolder) holder).bindData(item.getSongs());
                break;
        }
    }


    static class SearchBarViewHolder extends RecyclerView.ViewHolder {
        public SearchBarViewHolder(View itemView) {
            super(itemView);
        }
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager;
        BannerAdapter bannerAdapter;

        public BannerViewHolder(View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.viewPager);
            bannerAdapter = new BannerAdapter(new ArrayList<>());
            viewPager.setAdapter(bannerAdapter);

            bannerRunnable = new Runnable() {
                @Override
                public void run() {
                    if (bannerAdapter.getItemCount() > 0) {
                        int current = viewPager.getCurrentItem();
                        int next = (current + 1) % bannerAdapter.getItemCount();
                        viewPager.setCurrentItem(next, true);
                    }
                    bannerHandler.postDelayed(this, 3000);
                }
            };
            bannerHandler.postDelayed(bannerRunnable, 3000);
        }

        public void bindData(List<Song> data) {
            bannerAdapter.updateData(data);
        }
    }

    static class SpecialViewHolder extends RecyclerView.ViewHolder {
        RecyclerView specialRecyclerView;
        SongAdapter specialAdapter;

        public SpecialViewHolder(View itemView) {
            super(itemView);
            specialRecyclerView = itemView.findViewById(R.id.specialRecyclerView);
            specialRecyclerView.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            specialRecyclerView.setNestedScrollingEnabled(false);

            specialAdapter = new SongAdapter(new ArrayList<>(), R.layout.item_special_song);
            specialRecyclerView.setAdapter(specialAdapter);
        }

        public void bindData(List<Song> data) {
            specialAdapter.updateData(data);
        }
    }

    static class DailyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView dailyRecyclerView;
        SongAdapter dailyAdapter;

        public DailyViewHolder(View itemView) {
            super(itemView);
            dailyRecyclerView = itemView.findViewById(R.id.dailyRecyclerView);
            dailyRecyclerView.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            dailyRecyclerView.setNestedScrollingEnabled(false);

            dailyAdapter = new SongAdapter(new ArrayList<>(), R.layout.item_daily_song);
            dailyRecyclerView.setAdapter(dailyAdapter);
        }

        public void bindData(List<Song> data) {
            dailyAdapter.updateData(data);
        }
    }

    static class PopularViewHolder extends RecyclerView.ViewHolder {
        RecyclerView popularRecyclerView;
        SongAdapter popularAdapter;

        public PopularViewHolder(View itemView) {
            super(itemView);
            popularRecyclerView = itemView.findViewById(R.id.popularRecyclerView);
            popularRecyclerView.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            popularRecyclerView.setNestedScrollingEnabled(false);

            popularAdapter = new SongAdapter(new ArrayList<>(), R.layout.item_popular_song);
            popularRecyclerView.setAdapter(popularAdapter);
        }

        public void bindData(List<Song> data) {
            popularAdapter.updateData(data);
        }
    }
}

