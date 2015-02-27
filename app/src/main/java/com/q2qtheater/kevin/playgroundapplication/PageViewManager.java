package com.q2qtheater.kevin.playgroundapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;


import com.q2qtheater.kevin.playgroundapplication.Fragments.FestivalStaff;
import com.q2qtheater.kevin.playgroundapplication.Fragments.Installation;
import com.q2qtheater.kevin.playgroundapplication.Fragments.LiveUpdate;
import com.q2qtheater.kevin.playgroundapplication.Fragments.ShowFragment;

/**
 * This class controls the user interaction with the activities using
 * a swipe interface.
 */

public class PageViewManager extends Activity implements
                        ShowFragment.OnFragmentInteractionListener{

    public final static String SHOW_DAY = "day";
    public final static String SHOW_INDEX = "index";


    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private static int NUM_PAGES = 4;

    private static final LiveUpdate screen1 = new LiveUpdate();
    private static final  ShowFragment screen2 = new ShowFragment();
    private static final Installation screen3 = new Installation();
    private static final FestivalStaff screen4 = new FestivalStaff();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view_manager);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
        public void onPageSelected(int position){
                //NotUsed
            }
        });

        ContentManager.initializeContent(getApplicationContext(), this);
    }

    /**
     * A method which allows other classes to display a toast message to the user
     *
     * @param message the message to display to the user
     */
    public void sendToast(String message){
        screen1.sendToast(message, getApplicationContext());
    }

    /**
     * Called by content manager to notify that new data has been set
     */
    public void updateScreen(){
        //pull the appropriate text fields, and update by ID
        screen1.updateScreen();
    }

    /**
     * When a show is selected from the show list, send an intent to display
     * the show information
     *
     * @param Day the day of the week that the show is scheduled for
     * @param ShowIndex the performance number of the show
     */
    @Override
    public void onFragmentInteraction(String Day, int ShowIndex) {
        //send an intent to the piece information fragment
        Log.d("DAY SELECTED", Day);
        Log.d("Index", Integer.toString(ShowIndex));
        Intent intent = new Intent(this, PieceInformation.class);
        intent.putExtra(SHOW_DAY, Day);
        intent.putExtra(SHOW_INDEX, Integer.toString(ShowIndex));
        startActivity(intent);
    }

    /**
     * A simple pager adapter that represents 5 objects, in
     * sequence.
     */
    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment toReturn;
            switch(position){
                case 0:
                    toReturn =  screen1;
                    screen1.updateScreen();
                    break;
                case 1:
                    toReturn = screen2;
                    break;
                case 2:
                    toReturn = screen3;
                    break;
                case 3:
                    toReturn = screen4;
                    break;
                default:
                    toReturn = null;
                    break;
            }

            return toReturn;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}