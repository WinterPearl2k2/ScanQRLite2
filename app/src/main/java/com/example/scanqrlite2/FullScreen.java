package com.example.scanqrlite2;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;

public class FullScreen {
    Activity activity;
    int type;

    public FullScreen(Activity activity) {
        this.activity = activity;
    }

    public void changeFullScreen(int type) {
        if (type == 0) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            & ~ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if(type == 1) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.primaryShrinePink));
        }
    }
}
