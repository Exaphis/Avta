package com.exaphis.avta;


import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.time.Duration;

public class ScheduleAlgorithm {

    public static ArrayList<Event> algorithm (ArrayList<Event> events) {
        //Initialize internal variables
        ArrayList<MovableEvent> nonSetEvents = new ArrayList<>();
        ArrayList<Event> scheduleWorking = new ArrayList<>();
        ArrayList<LocalDateTime[]> freeTime = new ArrayList<>();

        //Separates Set Events and Movable Events into where they should be
        for (Event i : events) {
            if (i instanceof SetEvent)
                scheduleWorking.add(i);
            else if (i instanceof MovableEvent){
                nonSetEvents.add((MovableEvent) i);
                System.out.println("qwerty"); }
        }

        //Sorts them in their required ways
        Collections.sort(scheduleWorking); //by chronological order
        Collections.sort(nonSetEvents); //by due date

        //Removes any set events not in the next week
        if (scheduleWorking.size() != 0) {
            Iterator<Event> itr = scheduleWorking.iterator();
            while (itr.hasNext()) {
                if (itr.next().getStart().isAfter(LocalDateTime.now().plusWeeks(1)))
                    itr.remove();
            }
        }

        //Creates all available time periods
        if (scheduleWorking.size() == 0)
        {
            LocalDateTime[] temp = new LocalDateTime[2];
            temp[0] = LocalDateTime.now().plusMinutes(20);
            temp[1] = LocalDateTime.now().plusWeeks(1);
            freeTime.add(temp);
        }
        for (int i = 0; i < scheduleWorking.size(); i++) {
            LocalDateTime[] temp = new LocalDateTime[2];
            //Has to create the first set up until the first class

            if (i == 0) {
                if (scheduleWorking.get(i).getStart().isAfter(LocalDateTime.now().plusMinutes(20)) && !(Duration.between(LocalDateTime.now().plusMinutes(20), scheduleWorking.get(i).getStart().minusMinutes(5)).toMinutes() < 10)) {
                    temp[0] = LocalDateTime.now().plusMinutes(20);
                    temp[1] = scheduleWorking.get(i).getStart().minusMinutes(10);
                    freeTime.add(temp);
                    temp = new LocalDateTime[2];
                }
            }
            //creates the time between the other set events as free time (with a 10 minute gap, and making sure there it is at least 10 minutes of free time)
            if (scheduleWorking.size() != 1 && i != scheduleWorking.size()-1) {
                if (Duration.between(scheduleWorking.get(i).getEnd().plusMinutes(5), scheduleWorking.get(i + 1).getStart().minusMinutes(5)).toMinutes() < 10) {
                    System.out.println("Skipping time period of length " + Duration.between(scheduleWorking.get(i).getEnd().plusMinutes(5), scheduleWorking.get(i + 1).getStart().minusMinutes(5)).toMinutes() + " and you will find me using wasd");
                    continue;
                }
                if (i < scheduleWorking.size() - 1) {
                    temp[0] = scheduleWorking.get(i).getEnd().plusMinutes(5);
                    temp[1] = scheduleWorking.get(i + 1).getStart().minusMinutes(5);
                    freeTime.add(temp);
                }
                continue;
            }
            //Sets the gap between the last one and the end of the week, should it exist
            if (scheduleWorking.get(i).getEnd().isBefore(LocalDateTime.now().plusWeeks(1))) {
                temp[0] = scheduleWorking.get(i).getEnd().plusMinutes(5);
                temp[1] = LocalDateTime.MAX;
            }
            //escapes if the last two aren't true
            else {
                continue;
            }
            //Adds the period to the set of free times
            freeTime.add(temp);
        }

        //fits the earliest in queue it an into free time and takes away that free time
        while ((nonSetEvents.size()!= 0) &&( freeTime.size() != 0)) { //if out of free time or set events, it ends
            System.out.println("asdf");
            for (int i = 0; i < nonSetEvents.size(); i++){ //goes through the events to fit the earliest in queue
                if (freeTime.size() == 0) //Another check since it hasnt gone though the while loop yet, and the while loop ends after break
                    break;
                if (nonSetEvents.get(i).getLength() <= Duration.between(freeTime.get(0)[0], freeTime.get(0)[1]).toMinutes()) { //if it fits, it has a starting and ending time taken away
                    MovableEvent temp = nonSetEvents.get(i);
                    temp.setStartEnd(freeTime.get(0)[0], freeTime.get(0)[0].plusMinutes(temp.getLength()));
                    System.out.println("zxcv " + temp.getEventName());
                    scheduleWorking.add(temp);
                    nonSetEvents.remove(i);
                    LocalDateTime[] temp2 = freeTime.get(0);
                    if (Duration.between(temp2[0], temp2[1]).toMinutes() <= 5) //if the remaining free time is less than 5, delete it
                        freeTime.remove(0);
                    else { //other wise give a 5 minute gap and start looking through free time again
                        temp2[0] = temp.getEnd().plusMinutes(5);
                        freeTime.set(0, temp2);
                    }
                    break;
                }
                if (i == nonSetEvents.size()-1) { //if the free time isnt big enough for anything, get rid of it
                    freeTime.remove(0);
                }
            }
        }


        //Final formatting and return
        for (Event e : scheduleWorking)
            System.out.println(e.getEventName());
        return scheduleWorking;
    }
}
