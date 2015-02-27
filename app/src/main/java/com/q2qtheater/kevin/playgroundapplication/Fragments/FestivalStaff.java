package com.q2qtheater.kevin.playgroundapplication.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.q2qtheater.kevin.playgroundapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FestivalStaff#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FestivalStaff extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FestivalStaff.
     */
    public static FestivalStaff newInstance(String param1, String param2) {
        FestivalStaff fragment = new FestivalStaff();
        return fragment;
    }
    public FestivalStaff() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_festival_staff, container, false);
    }

}
