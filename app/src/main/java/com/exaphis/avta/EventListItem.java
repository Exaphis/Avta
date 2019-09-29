package com.exaphis.avta;

import java.time.LocalDateTime;

public class EventListItem {
    private String heading;
    private String description;
    private String time;

    public EventListItem(String heading, String description,
                         LocalDateTime start, LocalDateTime end) {
        this.heading = heading;
        this.description = description;

        time = "";
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }
}
