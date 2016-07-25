package com.example.grin.mobilizationmusic.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grin.mobilizationmusic.MainActivity;
import com.example.grin.mobilizationmusic.R;


public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        mainActivity.getSupportActionBar().setTitle(R.string.about);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }


}
