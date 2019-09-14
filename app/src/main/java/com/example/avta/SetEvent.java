package com.example.avta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.time.Duration;

public class SetEvent extends Event {

    public SetEvent (String name, String subject, LocalDateTime start, LocalDateTime end) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.subject = subject;
        Duration tempLength = Duration.between(start, end);
        this.length = (int) tempLength.toMinutes();
    }


}
