package com.example.scanqrlite2;

import android.app.Activity;
import android.os.Build;
import android.view.View;

public class FullScreen {
    Activity activity;

    public FullScreen(Activity activity) {
        this.activity = activity;
    }

    public void changeFullScreen(int type) {

        if (type == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                & ~ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if(type == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
