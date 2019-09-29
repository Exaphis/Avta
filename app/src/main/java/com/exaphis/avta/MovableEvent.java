package com.exaphis.avta;

import android.os.Parcel;
import android.os.Parcelable;
import java.time.LocalDateTime;

public class MovableEvent extends Event implements Parcelable {
    private LocalDateTime dueDate;
    private int enjoyLevel;

    public MovableEvent (String name, long length, String subject, LocalDateTime dueDate, int enjoyLevel) {
        super(name, length, subject, LocalDateTime.MAX, LocalDateTime.MAX);
        this.dueDate = dueDate;
        this.enjoyLevel = enjoyLevel;
    }

    public void setStartEnd (LocalDateTime start, LocalDateTime end) {
        super.setStart(start);
        super.setEnd(end);
    }

    public int getEnjoyLevel() {
        return enjoyLevel;
    }

    public LocalDateTime getDueDate () {
        return dueDate;
    }

    public int compareTo (MovableEvent other) {
        if (this.dueDate.isBefore(other.getDueDate()))
            return -1;
        else if (this.dueDate.isBefore(other.getDueDate()))
            return 1;
        else {
           if (this.enjoyLevel > other.enjoyLevel)
               return -1;
           else if (other.enjoyLevel > this.enjoyLevel)
               return 1;
           else
               return 0;
        }

    }

    protected MovableEvent(Parcel in) {
        super(in.readString(),
                in.readLong(),
                in.readString(),
                LocalDateTime.parse(in.readString()),
                LocalDateTime.parse(in.readString()));

        dueDate = LocalDateTime.parse(in.readString());
        enjoyLevel = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(dueDate.toString());
        dest.writeInt(enjoyLevel);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovableEvent> CREATOR = new Parcelable.Creator<MovableEvent>() {
        @Override
        public MovableEvent createFromParcel(Parcel in) {
            return new MovableEvent(in);
        }

        @Override
        public MovableEvent[] newArray(int size) {
            return new MovableEvent[size];
        }
    };
    }