package com.example.scanqrlite2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.scanqrlite2.Adapter.AdapterBottomNav;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigationView;
    static ViewPager2 viewPager;
    AdapterBottomNav adapterBottomNav;
    Language language;
    FullScreen fullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        language = new Language(this);
        language.Language();
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        getChangeFullScr();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ORM();
        SetUpLayout();
        SetUpView();
        darkMode();
    }

    private boolean darkMode() {
        SharedPreferences darkMode = getSharedPreferences("dark_mode", MODE_PRIVATE);
        boolean check = darkMode.getBoolean("dark_mode", false);
        if(check) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            return true;
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            return false;
        }
    }

    private void getChangeFullScr() {
        SharedPreferences preferences = getSharedPreferences("fullscr", MODE_PRIVATE);
        int type = preferences.getInt("fullscr", 0);
        fullScreen = new FullScreen(MainActivity.this);
        fullScreen.changeFullScreen(type);
    }

    private void SetUpLayout() {
        navigationView.setItemIconTintList(null);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_scan:
                        viewPager.setCurrentItem(0, false);
                        break;
                    case R.id.nav_create:
                        viewPager.setCurrentItem(1, false);
                        break;
                    case R.id.nav_history:
                        viewPager.setCurrentItem(2, false);
                        break;
                    case R.id.nav_setting:
                        viewPager.setCurrentItem(3, false);
                        break;
                }
                language.Language();
                return false;
            }
        });
    }

    private void SetUpView() {
        adapterBottomNav = new AdapterBottomNav(this);
        viewPager.setAdapter(adapterBottomNav);
        viewPager.setUserInputEnabled(false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int type;
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        navigationView.getMenu().findItem(R.id.nav_scan).setChecked(true);
                        type = 0;
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.nav_create).setChecked(true);
                        if(darkMode())
                            type = 0;
                        else
                            type = 1;
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.nav_history).setChecked(true);
                        if(darkMode())
                            type = 0;
                        else
                            type = 1;
                        break;
                    case 3:
                        navigationView.getMenu().findItem(R.id.nav_setting).setChecked(true);
                        if(darkMode())
                            type = 0;
                        else
                            type = 1;
                        break;
                    default:
                        navigationView.getMenu().findItem(R.id.nav_scan).setChecked(true);
                        type = 0;
                        break;
                }
                language.Language();
                SharedPreferences.Editor preferences = getSharedPreferences("fullscr", MODE_PRIVATE).edit();
                preferences.putInt("fullscr", type);
                fullScreen = new FullScreen(MainActivity.this);
                fullScreen.changeFullScreen(type);
            }
        });
    }

    private void ORM() {
        navigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.view_pager);
    }
}