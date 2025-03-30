package com.example.music_zsz;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MusicPagerAdapter extends FragmentStateAdapter {

    private SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();
    public MusicPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new CoverFragment();
        } else {
            fragment = new LyricFragment();
        }
        fragmentSparseArray.put(position, fragment);
        return fragment;
    }

    public Fragment getFragmentAt(int position) {
        return fragmentSparseArray.get(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
