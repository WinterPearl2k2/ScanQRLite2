package com.example.scanqrlite2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Result extends AppCompatActivity {
    FullScreen screen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        screen = new FullScreen(Result.this);
        screen.changeFullScreen(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result_generator);
    }
}