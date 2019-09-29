package com.exaphis.avta.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exaphis.avta.activities.EditMovableEventActivity;
import com.exaphis.avta.Event;
import com.exaphis.avta.activities.MainActivity;
import com.exaphis.avta.MovableEvent;
import com.exaphis.avta.adapters.MovableEventAdapter;
import com.exaphis.avta.MovableEventSwipeToDeleteCallback;
import com.exaphis.avta.R;
import com.exaphis.avta.RecyclerViewClickListener;
import com.exaphis.avta.ScheduleAlgorithm;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovableEventListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MovableEventListFragment extends Fragment implements RecyclerViewClickListener {
    private static final int EDIT_MOVABLE_EVENT_ACTIVITY_REQUEST_CODE = 3;

    private OnFragmentInteractionListener mListener;
    private MovableEventAdapter adapter;

    public MovableEventListFragment() {
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
        return inflater.inflate(R.layout.fragment_movable_event_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity == null) {
            throw new RuntimeException("Activity is null");
        }

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.movableEvents);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MovableEventAdapter(((MainActivity) getActivity()).getEvents(), getActivity(), this);

        ItemTouchHelper ith = new ItemTouchHelper(new MovableEventSwipeToDeleteCallback(getActivity(), adapter));
        ith.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
    }

    public void notifyAdapter(ArrayList<Event> events) {
        adapter.changeEvents(events);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onMovableEventListFragmentInitialize(this);
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
        void onMovableEventListFragmentInitialize(MovableEventListFragment fragment);
    }

    @Override
    public void recyclerViewListClicked(View v, Event eventClicked) {
        Intent intent = new Intent(v.getContext(), EditMovableEventActivity.class);
        intent.putExtra("event", eventClicked);
        startActivityForResult(intent, EDIT_MOVABLE_EVENT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_MOVABLE_EVENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            System.out.println("in edit request code");

            ArrayList<Event> events = ((MainActivity) getActivity()).getEvents();

            MovableEvent e = data.getParcelableExtra("event");
            int prevEventHashCode = data.getIntExtra("prevEventHashCode", -1);

            ArrayList<Event> temp = new ArrayList<>();
            for (Event tempEvent : events) {
                if (tempEvent.hashCode() != prevEventHashCode) {
                    temp.add(tempEvent);
                } else
                    System.out.println("hit hash");
            }
            temp.add(e);

            events = ScheduleAlgorithm.algorithm(temp);
            notifyAdapter(events);

            ((MainActivity) getActivity()).setEvents(events);
        }
    }
}
