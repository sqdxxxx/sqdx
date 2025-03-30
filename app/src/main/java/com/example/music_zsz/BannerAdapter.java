package com.example.music_zsz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Song> bannerSongs;

    public BannerAdapter(List<Song> bannerSongs) {
        this.bannerSongs = bannerSongs;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Song song = bannerSongs.get(position);
        Glide.with(holder.itemView)
                .load(song.getCoverUrl())
                .error(R.drawable.ic_02)
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
            SongRepository.getInstance().addSong(song);
            Toast.makeText(
                    v.getContext(),
                    "将 " + song.getName() + " 添加到音乐列表",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    @Override
    public int getItemCount() {
        Log.d("bannerSongs",""+bannerSongs.size());
        return bannerSongs.size();
    }


    public void updateData(List<Song> newSongs) {
        this.bannerSongs = newSongs;
        notifyDataSetChanged();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        BannerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImageView);
        }
    }
}
