package com.example.tragic.irate.simple.stopwatch;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopUp {

    public void showPopUp(final View view) {
        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popUpView = inflater.inflate(R.layout.pom_reset_popup, null);

        PopupWindow popupWindow  = new PopupWindow(popUpView, WindowManager.LayoutParams.WRAP_CONTENT, 75, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 900);
    }
}
