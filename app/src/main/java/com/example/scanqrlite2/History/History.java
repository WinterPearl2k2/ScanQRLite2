package com.example.scanqrlite2.History;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanqrlite2.R;
import com.example.scanqrlite2.Adapter.HistoryAdapter;
import com.example.scanqrlite2.History.History_Menu.History_Create;
import com.example.scanqrlite2.History.History_Menu.History_Scan;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class History extends Fragment {
    TabItem tabScan, tabCreate;
    TabLayout tabContainer;
    RelativeLayout title_history;
    ViewPager2 viewPager2;
    HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ORM(view);
        switchLayout();
        return view;
    }


    private void switchLayout() {
        adapter = new HistoryAdapter(getActivity());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabContainer, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: tab.setText(R.string.scan);
                        break;
                    case 1: tab.setText(R.string.create);
                        break;
                }
            }
        }).attach();
    }

    private void ORM(View view) {
        tabScan = view.findViewById(R.id.tab_scan);
        tabCreate = view.findViewById(R.id.tab_create);
        tabContainer = view.findViewById(R.id.tab_container);
        title_history = view.findViewById(R.id.title_history);
        viewPager2 = view.findViewById(R.id.history_layout);
    }
}