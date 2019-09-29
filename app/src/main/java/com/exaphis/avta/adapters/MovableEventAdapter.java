package com.exaphis.avta.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.exaphis.avta.Event;
import com.exaphis.avta.MovableEvent;
import com.exaphis.avta.R;
import com.exaphis.avta.RecyclerViewClickListener;
import com.exaphis.avta.SetEvent;
import com.google.android.material.snackbar.Snackbar;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MovableEventAdapter extends RecyclerView.Adapter<MovableEventAdapter.EventViewHolder>{
    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Event event;
        TextView textViewHeading;
        TextView textViewDescription;
        TextView textViewTime;

        EventViewHolder(View itemView) {
            super(itemView);

            textViewHeading = itemView.findViewById(R.id.textViewHeading);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewTime = itemView.findViewById(R.id.textViewTime);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.recyclerViewListClicked(v, event);
        }
    }

    private ArrayList<Event> events;
    private Context context;
    private Event recentlyDeletedItem;
    private int recentlyDeletedItemPosition;
    private RecyclerViewClickListener clickListener;

    public MovableEventAdapter(ArrayList<Event> events, Context context, RecyclerViewClickListener clickListener) {
        this.events = events;
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public MovableEventAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        return new MovableEventAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovableEventAdapter.EventViewHolder holder, int position) {
        if (events.get(position) instanceof SetEvent) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        }

        MovableEvent listItem = (MovableEvent) events.get(position);
        holder.event = listItem;

        holder.itemView.setVisibility(View.VISIBLE);
        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        holder.textViewHeading.setText(listItem.getEventName());
        holder.textViewDescription.setText(listItem.getSubject());

        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("hh:mm a E, MMM dd yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        if (listItem.getStart().toLocalDate().equals(listItem.getEnd().toLocalDate())) {
            holder.textViewTime.setText(
                    context.getApplicationContext().getString(
                            R.string.event_list_time_same_day,
                            listItem.getStart().format(timeFormatter),
                            listItem.getEnd().format(timeFormatter),
                            listItem.getStart().format(dateFormatter))
                            + "\nDue " + listItem.getDueDate().format(dateFormatter)
            );
        }
        else {
            holder.textViewTime.setText(
                    context.getApplicationContext().getString(
                            R.string.event_list_time_different_day,
                            listItem.getStart().format(fullFormatter),
                            listItem.getEnd().format(fullFormatter))
                            .replace("\\n", "\n")
                            + "\nDue " + listItem.getDueDate().format(dateFormatter)
            );
        }
    }

    public void changeEvents(ArrayList<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        recentlyDeletedItem = events.get(position);
        recentlyDeletedItemPosition = position;
        events.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = ((Activity) context).findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, "1 task deleted",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        events.add(recentlyDeletedItemPosition,
                recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

