package com.example.uberapp;

public class Event {

    private int eventid;
    private String eventname;
    private String eventdate;
    private int noofpeople;

    public Event(int eventid, String eventname, String eventdate, int noofpeople){
        this.eventid = eventid;
        this.eventname = eventname;
        this.eventdate = eventdate;
        this.noofpeople = noofpeople;
    }

    public int getEventid() {
        return eventid;
    }

    public String getEventname() {
        return eventname;
    }

    public String getEventdate() {
        return eventdate;
    }

    public int getNoofpeople() {
        return noofpeople;
    }
}
