package org.eim_systems.privatetracker;

import java.util.Date;

public class MetaData {
    private String name;
    private Date date;
    private double distance;
    private double up;
    private double down;
    private Date time;

    public MetaData(String name, Date date, double distance, double up, double down){
        this.name = name;
        this.date=date;
        this.distance=distance;
        this.up=up;
        this.down=down;
    }
    public MetaData(String name, Date date, double distance, double up, double down, Date time){
        this.name = name;
        this.date=date;
        this.distance=distance;
        this.up=up;
        this.down=down;
        this.time=time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getUp() {
        return up;
    }

    public void setUp(double up) {
        this.up = up;
    }

    public double getDown() {
        return down;
    }

    public void setDown(double down) {
        this.down = down;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
