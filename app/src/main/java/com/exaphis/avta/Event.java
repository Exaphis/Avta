package com.exaphis.avta;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Objects;

public class Event implements Parcelable, Comparable<Event>, WeekViewDisplayable<Event> {
    private String name;
    private long length;
    private String subject;
    private LocalDateTime start;
    private LocalDateTime end;

    public int compareTo (Event other) {
        if (this.end.isBefore(other.getStart()))
            return -1;
        else if (this.start.isAfter(other.getEnd()))
            return 1;
        else
            return 0;
    }

    @Override
    public WeekViewEvent<Event> toWeekViewEvent() {
        WeekViewEvent.Style style;
        if (this instanceof SetEvent) {
            style = new WeekViewEvent.Style.Builder()
                    .setBackgroundColor(Color.parseColor("#D81B60"))
                    .build();
        }
        else {
            style = new WeekViewEvent.Style.Builder()
                    .setBackgroundColor(Color.parseColor("#800f38"))
                    .build();
        }

        Calendar startCal = Calendar.getInstance();
        startCal.clear();
        startCal.set(start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth(),
                start.getHour(), start.getMinute(), start.getSecond());

        Calendar endCal = Calendar.getInstance();
        endCal.clear();
        endCal.set(end.getYear(), end.getMonthValue() - 1, end.getDayOfMonth(),
                end.getHour(), end.getMinute(), end.getSecond());

        return new WeekViewEvent.Builder<Event>()
                .setId(hashCode())
                .setTitle(name)
                .setStartTime(startCal)
                .setEndTime(endCal)
                .setLocation(subject)
                .setAllDay(false)
                .setStyle(style)
                .setData(this)
                .build();
    }

    public String getEventName() {
        return this.name;
    }

    public long getLength() {
        return this.length;
    }

    public String getSubject() {
        return this.subject;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    Event(String name, long length, String subject, LocalDateTime start, LocalDateTime end) {
        this.name = name;
        this.length = length;
        this.subject = subject;
        this.start = start;
        this.end = end;
    }

    protected Event(Parcel in) {
        name = in.readString();
        length = in.readLong();
        subject = in.readString();
        start = LocalDateTime.parse(in.readString());
        end = LocalDateTime.parse(in.readString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, length, subject, start, end);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(length);
        dest.writeString(subject);
        dest.writeString(start.toString());
        dest.writeString(end.toString());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
