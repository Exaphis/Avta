package com.exaphis.avta;

import android.os.Parcel;
import android.os.Parcelable;
import java.time.LocalDateTime;
import java.time.Duration;

public class SetEvent extends Event implements Parcelable {
    public SetEvent (String name, String subject, LocalDateTime start, LocalDateTime end) {
        super(name, Duration.between(start, end).toMinutes(), subject, start, end);
    }

    protected SetEvent(Parcel in) {
        super(in.readString(),
                in.readLong(),
                in.readString(),
                LocalDateTime.parse(in.readString()),
                LocalDateTime.parse(in.readString()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SetEvent> CREATOR = new Parcelable.Creator<SetEvent>() {
        @Override
        public SetEvent createFromParcel(Parcel in) {
            return new SetEvent(in);
        }

        @Override
        public SetEvent[] newArray(int size) {
            return new SetEvent[size];
        }
    };
}
