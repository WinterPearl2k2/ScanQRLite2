package com.example.scanqrlite2.History.History_Menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scanqrlite2.R;
import com.example.scanqrlite2.Adapter.HistoryScanAdapter;
import com.example.scanqrlite2.History.History_Menu.database.ScanDatabase;

import java.util.ArrayList;
import java.util.List;


public class History_Scan extends Fragment {
    RecyclerView recyclerView;
    HistoryScanAdapter adapter;
    List<HistoryScanItem> scanItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_scan, container, false);
        ORM(view);
        adapter = new HistoryScanAdapter(getActivity());

        scanItemList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        loadData();

        return view;
    }

    private void loadData() {
        scanItemList = ScanDatabase.getInstance(getContext()).scanItemDAO().getListItem();
        adapter.setData(scanItemList);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void ORM(View view) {
        recyclerView = view.findViewById(R.id.recycle_history_scan);
    }
}