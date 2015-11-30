package com.q2qtheater.kevin.playgroundapplication.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.q2qtheater.kevin.playgroundapplication.ContentManager;
import com.q2qtheater.kevin.playgroundapplication.InformationType;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.BreakInformation;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.ShowInformation;
import com.q2qtheater.kevin.playgroundapplication.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A Framgent which displays up to the minute information about upcoming
 * shows and breaks when loaded
 */

public class LiveUpdate extends Fragment {

    //References to screen outputs
    private TextView playingNow = null;
    private TextView playingNext = null;
    private TextView playingAfter = null;
    private TextView nextBreak = null;

    public LiveUpdate() {
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
        View view = inflater.inflate(R.layout.fragment_live_update, container, false);
        nextBreak = (TextView) view.findViewById(R.id.break_time);
        playingNow = (TextView) view.findViewById(R.id.show_playing);
        playingNext = (TextView) view.findViewById(R.id.show_next);
        playingAfter = (TextView) view.findViewById(R.id.show_after);
        updateScreen();
        return view;
    }

    /**
     * A class which allows other classes to display text to a user as a toast
     * context must be provided since this class is a fragment, not activity
     *
     * @param message the message to display
     * @param context the app context to display it in
     */
    public void sendToast(String message, Context context){
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    /**
     * This function grabs the show list and break list and the time,
     * and then compares the current time to upcoming performance times
     * to display what shows are coming up next
     */
    public void updateScreen(){
        if(nextBreak != null) {
            nextBreak.setText(" ");
            playingNow.setText(" ");
            playingNext.setText(" ");
            playingAfter.setText(" ");

            //Construct the current date for comparison
            TimeZone pitsburghTZ = TimeZone.getTimeZone("GMT-5:00");
            GregorianCalendar now = new GregorianCalendar(
                    pitsburghTZ);

            GregorianCalendar thursday = new GregorianCalendar(2015, 0, 29);
            GregorianCalendar friday = new GregorianCalendar(2015, 0, 30);
            GregorianCalendar saturday = new GregorianCalendar(2015, 0, 31);
            Calendar gregCal = new GregorianCalendar(pitsburghTZ);

            thursday.setTimeZone(pitsburghTZ);
            friday.setTimeZone(pitsburghTZ);
            saturday.setTimeZone(pitsburghTZ);

            ArrayList<ShowInformation> currentDay = new ArrayList<ShowInformation>();

            if (now.compareTo(saturday) >= 0) {
                currentDay = ContentManager.getProgramInformation(InformationType.SATURDAY_SHOWS);
            } else if (now.compareTo(friday) >= 0) {
                currentDay = ContentManager.getProgramInformation(InformationType.FRIDAY_SHOWS);
            } else if (now.compareTo(thursday) >= 0) {
                currentDay = ContentManager.getProgramInformation(InformationType.THURSDAY_SHOWS);
            }

            //loop through shows for day, identify show immediately after now
            int currentIndex;
            for (currentIndex = 0; currentIndex < currentDay.size(); currentIndex++) {
                ShowInformation showCompare = currentDay.get(currentIndex);
                gregCal.setTime(showCompare.getDate());
                if (now.get(Calendar.HOUR) < gregCal.get(Calendar.HOUR)) {
                    playingNow.setText(showCompare.getTitle() + " " + showCompare.getLocation());
                    break;
                }
            }

            //See if there are remaining shows
            if (currentIndex + 1 < currentDay.size()) {
                ShowInformation nextShow = currentDay.get(currentIndex + 1);
                playingNext.setText(nextShow.getTitle() + " " + nextShow.getLocation());
            }

            //See if there are remaining shows
            if (currentIndex + 2 < currentDay.size()) {
                ShowInformation showAfter = currentDay.get(currentIndex + 2);
                playingAfter.setText(showAfter.getTitle() + " " + showAfter.getLocation());
            }


            ArrayList<BreakInformation> breaks = ContentManager.getProgramInformation(InformationType.BREAKS);

            //If there are any breaks left, display the next break to the screen
            for (int i = 0; i < breaks.size(); i++) {
                BreakInformation compareBreak = breaks.get(i);
                gregCal.setTime(compareBreak.getDate());

                if(now.get(Calendar.DATE) == gregCal.get(Calendar.DATE)){
                    if (now.get(Calendar.HOUR) < gregCal.get(Calendar.HOUR)) {
                        DateFormat df = new SimpleDateFormat("HH:mm");
                        String formattedDate = df.format(compareBreak.getDate());
                        nextBreak.setText(formattedDate);
                        break;
                    }
                }
            }

        }

    }

}
