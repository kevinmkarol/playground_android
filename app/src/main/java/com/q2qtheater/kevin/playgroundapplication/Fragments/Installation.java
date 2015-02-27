package com.q2qtheater.kevin.playgroundapplication.Fragments;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.q2qtheater.kevin.playgroundapplication.R;

/**
 * A simple {@link Fragment} subclass.
 *
 * A fragment which displays the installation map image.
 */
public class Installation extends Fragment {


    public Installation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_installation, container, false);
    }


}
