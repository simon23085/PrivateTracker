package org.eim_systems.privatetracker;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Result implements Serializable {
    List<Location> locations;

    /*public Result(ArrayList<Location> locations){
        this.locations = locations;
    }*/

    public Result(List<Location> locations){
        this.locations = locations;
    }


    public double getDistance(){
        double distance = 0;
        for(int i=1; i < locations.size(); i++){
            distance += locations.get(i - 1).distanceTo(locations.get(i));
        }
        return distance;
    }
    public double getUp(){
        double up = 0;
        for(int i=1; i < locations.size(); i++){
            Location location1, location2;
            location1 = locations.get(i-1);
            location2 = locations.get(i);
            if(location2.getAltitude()> location1.getAltitude()){
                up += location2.getAltitude()-location1.getAltitude();
            }
        }
        return up;
    }
    public double getDown(){
        double down = 0;
        for(int i=1; i < locations.size(); i++){
            Location location1, location2;
            location1 = locations.get(i-1);
            location2 = locations.get(i);
            if(location2.getAltitude()< location1.getAltitude()){
                down += location1.getAltitude()-location2.getAltitude();
            }
        }
        return down;
    }
}
