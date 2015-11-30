package com.q2qtheater.kevin.playgroundapplication.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.q2qtheater.kevin.playgroundapplication.ContentManager;
import com.q2qtheater.kevin.playgroundapplication.InformationType;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.InformationContainer;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.InstallationInformation;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.ShowInformation;
import com.q2qtheater.kevin.playgroundapplication.R;


import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 */
public class ShowFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ShowFragment newInstance(String param1, String param2) {
        ShowFragment fragment = new ShowFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShowFragment() {
    }

    //Initialize the array which populates the shows in the festival
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<ShowInformation> thursday = ContentManager.getProgramInformation(InformationType.THURSDAY_SHOWS);
        ArrayList<ShowInformation> friday = ContentManager.getProgramInformation(InformationType.FRIDAY_SHOWS);
        ArrayList<ShowInformation> saturday = ContentManager.getProgramInformation(InformationType.SATURDAY_SHOWS);
        ArrayList<InstallationInformation> install = ContentManager.getProgramInformation(InformationType.INSTALLATIONS);

        ArrayList<InformationContainer> fullArray = new ArrayList<InformationContainer>();
        fullArray.addAll(thursday);
        fullArray.addAll(friday);
        fullArray.addAll(saturday);
        fullArray.addAll(install);

        mAdapter = new ArrayAdapter<InformationContainer>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, fullArray);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            ArrayList<ShowInformation> thursday = ContentManager.getProgramInformation(InformationType.THURSDAY_SHOWS);
            ArrayList<ShowInformation> friday = ContentManager.getProgramInformation(InformationType.FRIDAY_SHOWS);
            ArrayList<ShowInformation> saturday = ContentManager.getProgramInformation(InformationType.SATURDAY_SHOWS);
            ArrayList<InstallationInformation> install = ContentManager.getProgramInformation(InformationType.INSTALLATIONS);

            //Calculate totals seen at a certain point in festival
            String dayID;
            int index;
            int thursdayTotal = thursday.size();
            int fridayTotal = thursdayTotal + friday.size();
            int saturdayTotal = fridayTotal + saturday.size();

            //Determine which day the event falls on using the totals
            if(position < thursdayTotal){
                dayID = "thursday";
                index = position;
            }else if(position < fridayTotal){
                dayID = "friday";
                index = position - thursdayTotal;
            }else if(position < saturdayTotal){
                dayID = "saturday";
                index = position - fridayTotal;
            }else{
                dayID = "installation";
                index = position - saturdayTotal;
            }

            mListener.onFragmentInteraction(dayID, index);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String day, int ShowIndex);
    }

}
