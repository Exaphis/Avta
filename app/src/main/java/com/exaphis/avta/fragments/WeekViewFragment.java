package com.exaphis.avta.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.EventClickListener;
import com.alamkanak.weekview.MonthChangeListener;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.exaphis.avta.activities.EditMovableEventActivity;
import com.exaphis.avta.activities.EditSetEventActivity;
import com.exaphis.avta.Event;
import com.exaphis.avta.activities.MainActivity;
import com.exaphis.avta.MovableEvent;
import com.exaphis.avta.R;
import com.exaphis.avta.ScheduleAlgorithm;
import com.exaphis.avta.SetEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeekViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class WeekViewFragment extends Fragment {
    private static final int EDIT_SET_EVENT_ACTIVITY_REQUEST_CODE = 2;
    private static final int EDIT_MOVABLE_EVENT_ACTIVITY_REQUEST_CODE = 3;

    private OnFragmentInteractionListener mListener;
    private ArrayList<Event> events;
    private WeekView<Event> weekView;

    public WeekViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_week_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        weekView = view.findViewById(R.id.weekView);
        weekView.setMonthChangeListener(new MonthChangeListener<Event>() {
            @Override
            public List<WeekViewDisplayable<Event>> onMonthChange(Calendar startDate, Calendar endDate) {
                LocalDateTime monthStart = LocalDateTime.ofInstant(startDate.toInstant(),
                        startDate.getTimeZone().toZoneId());
                LocalDateTime monthEnd = LocalDateTime.ofInstant(endDate.toInstant(),
                        endDate.getTimeZone().toZoneId());

                List<WeekViewDisplayable<Event>> displayables = new ArrayList<>();

                for (Event e : events) {
                    if (e.getEnd().isAfter(monthStart) && e.getStart().isBefore(monthEnd)) {
                        displayables.add(e);
                    }
                }

                return displayables;
            }
        });

        weekView.setOnEventClickListener(new EventClickListener<Event>() {
            @Override
            public void onEventClick(Event event, RectF rectF) {
                if (event instanceof MovableEvent) {
                    Intent intent = new Intent(getActivity(), EditMovableEventActivity.class);
                    intent.putExtra("event", event);
                    startActivityForResult(intent, EDIT_MOVABLE_EVENT_ACTIVITY_REQUEST_CODE);
                }
                else {
                    Intent intent = new Intent(getActivity(), EditSetEventActivity.class);
                    intent.putParcelableArrayListExtra("events", events);
                    intent.putExtra("event", event);
                    startActivityForResult(intent, EDIT_SET_EVENT_ACTIVITY_REQUEST_CODE);
                }
            }
        });
        weekView.goToCurrentTime();
    }

    public void notifyWeekView(ArrayList<Event> events) {
        this.events = events;
        weekView.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onWeekViewFragmentInitialize(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateEvents(ArrayList<Event> events) {
        this.events = events;
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
        void onWeekViewFragmentInitialize(WeekViewFragment fragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("weekviewfragment qoiwejfoiqwejfoij");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_MOVABLE_EVENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            System.out.println("in edit request code");
            MovableEvent e = data.getParcelableExtra("event");
            int prevEventHashCode = data.getIntExtra("prevEventHashCode", -1);

            ArrayList<Event> temp = new ArrayList<>();
            for (Event tempEvent : events) {
                if (tempEvent.hashCode() != prevEventHashCode) {
                    temp.add(tempEvent);
                }
                else
                    System.out.println("hit hash");
            }
            temp.add(e);

            events = ScheduleAlgorithm.algorithm(temp);
            notifyWeekView(events);

            ((MainActivity) getActivity()).setEvents(events);
        }
        else if (requestCode == EDIT_SET_EVENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            SetEvent e = data.getParcelableExtra("event");
            int prevEventHashCode = data.getIntExtra("prevEventHashCode", -1);

            ArrayList<Event> temp = new ArrayList<>();
            for (Event tempEvent : events) {
                if (tempEvent.hashCode() != prevEventHashCode) {
                    temp.add(tempEvent);
                }
                else
                    System.out.println("hit hash");
            }
            temp.add(e);

            events = ScheduleAlgorithm.algorithm(temp);
            notifyWeekView(events);

            ((MainActivity) getActivity()).setEvents(events);
        }
    }
}
