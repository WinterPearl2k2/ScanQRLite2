package com.example.scanqrlite2.Setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import com.example.scanqrlite2.BuildConfig;
import com.example.scanqrlite2.R;

import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.util.Locale;


public class Setting extends Fragment {
    private final int REQUEST_CODE =12;
    private ImageButton btnClickX;
    private SwitchCompat swBeep, swDarkmode,swVibrate, swClipboard;
    RelativeLayout btnChangeLanguage, btnChangeSearch;
    private LinearLayout btnBeep, btnDarkmode, btnClipboard, btnFeedback, btnRate, btnVibrate, btnVersion;
    private RadioButton itemGoogle, itemBing, itemYandex, itemCoccoc, itemYahoo, itemDuckDuckGo;
    private RadioButton itemEnglish, itemChinese, itemJapanese, itemKorean, itemVietnamese, itemThai;
    private TextView btnRatingClose, txtLanguage, txtVersion, btnRatingFeedback, txtSearch;
    private TextView txtContentFeedback, btnFeedbackYes, btnFeedbackNo, btnRatingVote,
            btnRatingVoteAgain, btnRatingOpenChplay, txtTitleRating;
    private ScaleRatingBar rtRating;
    private ImageView imgRating;
//    View view;
    Locale locale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ORM(view);
        Beep();
        Vibrate();
        CopyToClipboard();
        ChangeSearch();
        ChangeLanguage();
        DarkMode();
        RateUs();
        FeedBack();
        Version();
        return view;
    }

    public void Beep() {
        SharedPreferences beep = getActivity().getSharedPreferences("beep", Context.MODE_PRIVATE);
        boolean check = beep.getBoolean("beep", false);
        if(check) {
            swBeep.setChecked(true);
        } else {
            swBeep.setChecked(false);
        }
        btnBeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSwitch(swBeep, beep, "beep");
            }
        });
        swBeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSwitch(swBeep, beep, "beep");
            }
        });
    }

    public void Vibrate() {
        SharedPreferences vibrate = getActivity().getSharedPreferences("vibrate", Context.MODE_PRIVATE);
        boolean check = vibrate.getBoolean("vibrate", false);
        if(check) {
            swVibrate.setChecked(true);
        } else {
            swVibrate.setChecked(false);
        }
        btnVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSwitch(swVibrate, vibrate, "vibrate");
            }
        });
        swVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSwitch(swVibrate, vibrate, "vibrate");
            }
        });
    }

    private  void CopyToClipboard(){
        SharedPreferences clipboard = getActivity().getSharedPreferences("clipboard", Context.MODE_PRIVATE);
        boolean check = clipboard.getBoolean("clipboard", false);
        if(check) {
            swClipboard.setChecked(true);
        } else {
            swClipboard.setChecked(false);
        }
        btnClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSwitch(swClipboard, clipboard, "clipboard");
            }
        });
        swClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSwitch(swClipboard, clipboard, "clipboard");
            }
        });
    }

    private void CheckSwitch(SwitchCompat mSwitch, SharedPreferences preferences, String name) {
        SharedPreferences.Editor editor = preferences.edit();
        boolean check = preferences.getBoolean(name, false);
        if (check) {
            mSwitch.setChecked(false);
            editor.putBoolean(name, false);
        } else {
            mSwitch.setChecked(true);
            editor.putBoolean(name, true);
            if(name.equals("vibrate")) {
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(300);
            } else if (name.equals("beep")) {
                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 500);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP);
            }
        }
        editor.commit();
    }

    private  void ChangeSearch(){
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity());
        SharedPreferences preferences = getContext().getSharedPreferences("search", Context.MODE_PRIVATE);
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_search, null);
        String Search = preferences.getString("search", "Google");

        itemGoogle = view1.findViewById(R.id.item_google);
        itemBing = view1.findViewById(R.id.item_bing);
        itemYahoo = view1.findViewById(R.id.item_yahoo);
        itemCoccoc = view1.findViewById(R.id.item_coccoc);
        itemDuckDuckGo = view1.findViewById(R.id.item_duckduckgo);
        itemYandex = view1.findViewById(R.id.item_yandex);

        switch (Search) {
            case "Google":
                txtSearch.setText("Google");
                itemGoogle.setChecked(true);
                break;
            case "Bing":
                txtSearch.setText("Bing");
                itemBing.setChecked(true);
                break;
            case "Yahoo":
                txtSearch.setText("Yahoo");
                itemYahoo.setChecked(true);
                break;
            case "CocCoc":
                txtSearch.setText("CocCoc");
                itemCoccoc.setChecked(true);
                break;
            case "DuckDuckGo":
                txtSearch.setText("DuckDuckGo");
                itemDuckDuckGo.setChecked(true);
                break;
            case "Yandex":
                txtSearch.setText("Yandex");
                itemYandex.setChecked(true);
                break;
        }


        btnChangeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSearch(preferences, sheetDialog, view1);
            }
        });
    }
    private void CheckSearch(SharedPreferences preferences, BottomSheetDialog sheetDialog, View view){
        SharedPreferences.Editor editor = preferences.edit();
        sheetDialog.setContentView(view);
        RadioGroup listItemSearch = sheetDialog.findViewById(R.id.list_item_search);
        sheetDialog.show();
        listItemSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.item_google:
                        editor.putString("search","Google");
                        txtSearch.setText("Google");
                        break;
                    case R.id.item_bing:
                        editor.putString("search","Bing");
                        txtSearch.setText("Bing");
                        break;
                    case R.id.item_yahoo:
                        editor.putString("search","Yahoo");
                        txtSearch.setText("Yahoo");
                        break;
                    case R.id.item_coccoc:
                        editor.putString("search","CocCoc");
                        txtSearch.setText("CocCoc");
                        break;
                    case R.id.item_duckduckgo:
                        editor.putString("search","DuckDuckGo");
                        txtSearch.setText("DuckDuckGo");
                        break;
                    case R.id.item_yandex:
                        editor.putString("search","Yandex");
                        txtSearch.setText("Yandex");
                        break;
                }
                editor.commit();
                sheetDialog.dismiss();
            }
        });

    }
    private void ChangeLanguage() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity());
        SharedPreferences preferences = getContext().getSharedPreferences("language", Context.MODE_PRIVATE);
        View view2= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_language, null);
        String Language = preferences.getString("language", "English");
        Log.e("TAG1:",Language);


        itemEnglish = view2.findViewById(R.id.item_english);
        itemJapanese = view2.findViewById(R.id.item_japanese);
        itemChinese = view2.findViewById(R.id.item_chinese);
        itemVietnamese = view2.findViewById(R.id.item_vietnamese);
        itemKorean = view2.findViewById(R.id.item_korean);
        itemThai = view2.findViewById(R.id.item_thai);

        switch (Language) {
            case "English":
                txtLanguage.setText(R.string.english);
                itemEnglish.setChecked(true);
                break;
            case "Japanese":
                txtLanguage.setText(R.string.japanese);
                itemJapanese.setChecked(true);
                break;
            case "Chinese":
                txtLanguage.setText(R.string.chinese);
                itemChinese.setChecked(true);
                break;
            case "Korean":
                txtLanguage.setText(R.string.korean);
                itemKorean.setChecked(true);
                break;
            case "Vietnamese":
                txtLanguage.setText(R.string.vietnamese);
                itemVietnamese.setChecked(true);
                break;
            case "Thai":
                txtLanguage.setText(R.string.thai);
                itemThai.setChecked(true);
                break;
        }


        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckLanguage(preferences, sheetDialog, view2);
            }
        });
    }

    private void CheckLanguage(SharedPreferences preferences, BottomSheetDialog sheetDialog, View view){
        SharedPreferences.Editor editor = preferences.edit();
        sheetDialog.setContentView(view);
        RadioGroup listItemLanguage = sheetDialog.findViewById(R.id.list_item_language);
        sheetDialog.show();
        listItemLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.item_english:
                        editor.putString("language","English");
                        locale = new Locale("en");
                        txtLanguage.setText(R.string.english);
                        break;
                    case R.id.item_japanese:
                        editor.putString("language","Japanese");
                        locale = new Locale("ja");
                        txtLanguage.setText(R.string.japanese);
                        break;
                    case R.id.item_chinese:
                        editor.putString("language","Chinese");
                        locale = new Locale("zh");
                        txtLanguage.setText(R.string.chinese);
                        break;
                    case R.id.item_korean:
                        editor.putString("language","Korean");
                        locale = new Locale("ko");
                        txtLanguage.setText(R.string.korean);
                        break;
                    case R.id.item_vietnamese:
                        editor.putString("language","Vietnamese");
                        locale = new Locale("vi");
                        txtLanguage.setText(R.string.vietnamese);
                        break;
                    case R.id.item_thai:
                        editor.putString("language","Thai");
                        locale = new Locale("th");
                        txtLanguage.setText(R.string.thai);
                }
                changeLanguage(locale);
                editor.commit();
                sheetDialog.dismiss();
            }
        });

    }
    private void changeLanguage(Locale locale) {
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        getActivity().recreate();
    }

    private void DarkMode(){

    }
    private void RateUs() {
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_rate);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.cus_dialog);
                ORMDialogRating(dialog);
                rtRating.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
                        HandleRating(dialog);
                    }
                });
                btnClickX.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                HandleRating(dialog);
                dialog.show();
            }
        });
    }

    private void HandleRating(Dialog dialog) {
        btnRatingVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rtRating.getRating() >= 5f) {
                    imgRating.setImageResource(R.drawable.img_love);
                    txtTitleRating.setText(R.string.thank_you);
                    btnRatingClose.setVisibility(View.VISIBLE);
                    btnRatingOpenChplay.setVisibility(View.VISIBLE);
                    btnRatingFeedback.setVisibility(View.GONE);
                    btnRatingVote.setVisibility(View.GONE);
                    btnRatingOpenChplay.setText(R.string.open_chplay);
                    btnRatingOpenChplay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Writing something
                        }
                    });
                    rtRating.setScrollable(false);
                    rtRating.setClickable(false);
                } else if(rtRating.getRating() > 0f && rtRating.getRating() <= 4f) {
                    imgRating.setImageResource(R.drawable.icon_cry);
                    txtTitleRating.setText(R.string.report_bugs_and_let_us);
                    btnRatingVoteAgain.setVisibility(View.VISIBLE);
                    btnRatingClose.setVisibility(View.GONE);
                    btnRatingVoteAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rtRating.setRating(rtRating.getRating());
                            imgRating.setImageResource(R.drawable.icon_smile);
                            txtTitleRating.setText(R.string.rating_boost_your_app);
                            btnRatingVoteAgain.setVisibility(View.GONE);
                            btnRatingClose.setVisibility(View.INVISIBLE);
                            btnRatingVote.setVisibility(View.VISIBLE);
                            btnRatingFeedback.setVisibility(View.GONE);
                            rtRating.setScrollable(true);
                            rtRating.setClickable(true);
                        }
                    });
                    btnRatingFeedback.setVisibility(View.VISIBLE);
                    btnRatingOpenChplay.setVisibility(View.GONE);
                    btnRatingVote.setVisibility(View.GONE);
                    btnRatingFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            submitFeedback();
                        }
                    });
                    rtRating.setScrollable(false);
                    rtRating.setClickable(false);
                }
            }
        });
    }

    private void ORMDialogRating(Dialog dialog) {
        btnRatingClose = dialog.findViewById(R.id.btn_rating_close);
        btnRatingVote = dialog.findViewById(R.id.btn_rating_vote);
        btnRatingFeedback = dialog.findViewById(R.id.btn_rating_feedback);
        btnFeedback = dialog.findViewById(R.id.btn_feedback);
        btnRatingVoteAgain = dialog.findViewById(R.id.btn_rating_vote_again);
        btnRatingOpenChplay = dialog.findViewById(R.id.btn_rating_open_CHplay);
        rtRating = dialog.findViewById(R.id.rt_rating);
        imgRating = dialog.findViewById(R.id.img_rating);
        txtTitleRating = dialog.findViewById(R.id.txt_title_rating);
        btnClickX = dialog.findViewById(R.id.clickX_btn);
    }
    private void FeedBack() {
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_feedback);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.cus_dialog);
                ORMDialogFeedback(dialog);
                HandleFeedback(dialog);
                dialog.show();
            }
        });
    }
    private void ORMDialogFeedback(Dialog dialog) {
        txtContentFeedback = dialog.findViewById(R.id.txt_feedback);
        btnFeedbackYes = dialog.findViewById(R.id.btn_feedback_yes);
        btnFeedbackNo = dialog.findViewById(R.id.btn_feedback_no);
    }

    private void HandleFeedback(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtContentFeedback.setText(R.string.dialog_feedback);
        }
        btnFeedbackNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnFeedbackYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFeedbackYes.setClickable(false);
                submitFeedback();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE) {
            btnFeedbackYes.setClickable(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void submitFeedback(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//        emailIntent.setType("text/email");
        String EmailList = "nnhuquynh2603@gmail.com";
        emailIntent.setData(Uri.parse("mailto:" + EmailList + "?subject=" + "Scan QR Lite Feedback "));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, EmailList);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Scan QR Lite Feedback");

        try{
            startActivityForResult(Intent.createChooser(emailIntent,"Send Email"), REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getActivity(), "Email not installed" , Toast.LENGTH_SHORT).show();
        }


    }
    private void Version() {

        txtVersion.setText( getText(R.string.version) + BuildConfig.VERSION_NAME);
    }
    private void ORM(View view){
        btnBeep = view.findViewById(R.id.btn_sw_beep);
        swBeep = view.findViewById(R.id.sw_beep);
        btnVibrate = view.findViewById(R.id.btn_sw_vibrate);
        swVibrate = view.findViewById(R.id.sw_vibrate);
        btnClipboard = view.findViewById(R.id.btn_sw_auto_copy);
        swClipboard = view.findViewById(R.id.sw_copy);
        btnDarkmode = view.findViewById(R.id.btn_sw_dark_mode);
        swDarkmode = view.findViewById(R.id.sw_dark_mode);
        btnChangeSearch = view.findViewById(R.id.btn_search);
        txtSearch = view.findViewById(R.id.txt_search);
        btnChangeLanguage = view.findViewById(R.id.btn_language);
        txtLanguage = view.findViewById(R.id.txt_language);
        btnRate = view.findViewById(R.id.btn_rate);
        btnFeedback = view.findViewById(R.id.btn_feedback);
        btnVersion = view.findViewById(R.id.verion);
        txtVersion = view.findViewById(R.id.txt_version);
    }
}