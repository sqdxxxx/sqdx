package com.example.music_zsz;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LyricAdapter extends RecyclerView.Adapter<LyricAdapter.ViewHolder> {

    private List<LyricLine> lyricLines;
    private int selectedIndex = 0; // 当前选中歌词行

    public LyricAdapter(List<LyricLine> lyricLines) {
        this.lyricLines = lyricLines;
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lyric, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LyricLine line = lyricLines.get(position);
        holder.lyricTextView.setText(line.text);
        // 如果是当前行，则高亮显示
        if (position == selectedIndex) {
            holder.lyricTextView.setTextColor(Color.WHITE);
            holder.lyricTextView.setTextSize(20);
        } else {
            holder.lyricTextView.setTextColor(Color.GRAY);
            holder.lyricTextView.setTextSize(16);
        }
    }

    @Override
    public int getItemCount() {
        return lyricLines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lyricTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lyricTextView = itemView.findViewById(R.id.itemLyricTextView);
        }
    }

    // 用于存储每行歌词数据的 POJO 类
    public static class LyricLine {
        public int time; // 毫秒
        public String text;
        public LyricLine(int time, String text) {
            this.time = time;
            this.text = text;
        }
    }
}
