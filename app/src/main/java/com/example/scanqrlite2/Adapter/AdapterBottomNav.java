package com.example.scanqrlite2.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.scanqrlite2.Create.Create;
import com.example.scanqrlite2.History.History;
import com.example.scanqrlite2.Scan.Scan;
import com.example.scanqrlite2.Setting.Setting;

public class AdapterBottomNav extends FragmentStateAdapter {

    public AdapterBottomNav(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Scan();
            case 1: return new Create();
            case 2: return new History();
            case 3: return new Setting();
            default: return new Scan();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
