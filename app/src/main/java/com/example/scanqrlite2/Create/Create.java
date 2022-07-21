package com.example.scanqrlite2.Create;

import android.content.Intent;
import android.os.Bundle;
import com.example.scanqrlite2.Language;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.scanqrlite2.Create.item_create.Text;
import com.example.scanqrlite2.Create.item_create.URL;
import com.example.scanqrlite2.Create.item_create.Wifi;
import com.example.scanqrlite2.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Create extends Fragment {
    LinearLayout btnWifi, btnUrl, btnText;
    Language language;
    AdRequest request;
    AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        language = new Language(getContext());
        language.Language();
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        ORM(view);
        createWifi();
        createURL();
        createText();
        showAds();
        return view;
    }

    private void showAds() {
        request = new AdRequest.Builder().build();
        adView.loadAd(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adView != null)
            adView.resume();
    }

    @Override
    public void onStart() {
        if(adView != null)
            adView.pause();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if(adView != null)
            adView.destroy();
        super.onDestroy();
    }

    private void createText() {
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create(Text.class);
            }
        });
    }

    private void createURL() {
        btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create(URL.class);
            }
        });
    }

    private void createWifi() {
        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create(Wifi.class);
            }
        });
    }

    private void create(Class accessClass) {

        Intent intent = new Intent(getContext(), accessClass);
        startActivity(intent);
    }

    private void ORM(View view) {
        btnWifi = view.findViewById(R.id.btn_create_wifi);
        btnUrl = view.findViewById(R.id.btn_create_url);
        btnText = view.findViewById(R.id.btn_create_text);
        adView = view.findViewById(R.id.ads_create);
    }
}