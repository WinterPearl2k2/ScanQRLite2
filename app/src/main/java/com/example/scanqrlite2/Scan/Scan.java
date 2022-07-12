package com.example.scanqrlite2.Scan;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.scanqrlite2.R;

public class Scan extends Fragment {
    TextView btnGallery;
    CheckBox btnFlash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        ORM(view);
        scanByGallery();
        openFlash();
        return view;
    }

    private void openFlash() {
        if(btnFlash.isChecked())
            btnFlash.setText("Flash on");
        else
            btnFlash.setText("Flash off");
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnFlash.isChecked())
                    btnFlash.setText("Flash on");
                else
                    btnFlash.setText("Flash off");
            }
        });
    }

    private void scanByGallery() {
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                btnGallery.setEnabled(false);
                startActivityForResult(intent, 102);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102) {
            btnGallery.setEnabled(true);
        }
    }

    private void ORM(View view) {
        btnGallery = view.findViewById(R.id.btn_gallery);
        btnFlash = view.findViewById(R.id.btn_fash);
    }
}