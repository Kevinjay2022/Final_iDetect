package com.example.idetect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class FragmentMechanicNotification extends Fragment {

    public FragmentMechanicNotification(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragMechNot = inflater.inflate(R.layout.fragment_mechanic_notification, container, false);


        return fragMechNot;
    }

}
