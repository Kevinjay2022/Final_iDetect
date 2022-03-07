package com.example.idetect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentDriverNotification extends Fragment {

    public FragmentDriverNotification(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragDrvrNoty = inflater.inflate(R.layout.fragment_driver_notification, container, false);


        return fragDrvrNoty;
    }

}