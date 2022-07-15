package com.example.scanqrlite2.Setting;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.scanqrlite2.R;

import java.util.Locale;

public class Setting extends Fragment {
    private final int REQUEST_CODE =12;
    private SwitchCompat swBeep, swDarkmode, swClipboard;
    private RelativeLayout btnChangeLanguage, btnChangeSearch;
    private LinearLayout btnBeep, btnDarkmode, btnClipboard, btnFeedback, btnRate, btnVersion;
    private RadioButton itemGoogle, itemBing, itemYandex, itemCoccoc, itemYahoo, itemDuckDuckGo;
    private RadioButton itemEnglish, itemChina, itemJapanese, itemKorea, itemVietnam;
    private TextView txtLanguage, txtVersion, btnRatingFeedback, txtSearch;
    private TextView txtContentFeedback, btnFeedbackYes, btnFeedbackNo;
    View view;
    Locale locale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ORM();
        return view;
    }

    private void ORM(){
        btnBeep = view.findViewById(R.id.btn_sw_beep);
        swBeep = view.findViewById(R.id.sw_beep);
        btnClipboard = view.findViewById(R.id.btn_sw_auto_copy);
        swClipboard = view.findViewById(R.id.sw_copy);
        btnDarkmode = view.findViewById(R.id.btn_sw_dark_mode);
        btnChangeSearch = view.findViewById(R.id.btn_search);
        txtS
    }
}