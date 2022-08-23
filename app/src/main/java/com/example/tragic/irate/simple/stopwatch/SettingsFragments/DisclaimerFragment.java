package com.example.tragic.irate.simple.stopwatch.SettingsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tragic.irate.simple.stopwatch.R;

public class DisclaimerFragment extends Fragment {



    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.disclaimer_fragment_layout, container, false);
        root.setBackgroundColor(getResources().getColor(R.color.darker_grey));

        return root;

    }
}
