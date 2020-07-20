package com.example.uberapp.History;

public class HistoryObject
{
    private String rideId;
    private String time;
    private String paid;

    public HistoryObject(String time, String paid,String rideId) {
        this.time = time;
        this.paid = paid;
        this.rideId = rideId;

    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
