package com.q2qtheater.kevin.playgroundapplication.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.q2qtheater.kevin.playgroundapplication.ContentManager;
import com.q2qtheater.kevin.playgroundapplication.InformationType;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.Credit;
import com.q2qtheater.kevin.playgroundapplication.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FestivalStaff#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FestivalStaff extends Fragment {
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter<Credit> mAdapter;

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

        ArrayList<Credit>staff = ContentManager.getProgramInformation(InformationType.FESTIVAL_STAFF);
        ArrayList<Credit>thanks = ContentManager.getProgramInformation(InformationType.SPECIAL_THANKS);

        ArrayList<Credit>fullArray = new ArrayList<Credit>();
        fullArray.addAll(staff);
        fullArray.addAll(thanks);

        mAdapter = new ArrayAdapter<Credit>(getActivity(),
                android.R.layout.simple_list_item_1,  fullArray);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_festival_staff, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        return view;
    }

}
