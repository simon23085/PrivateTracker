package org.eim_systems.privatetracker;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Result implements Parcelable {
    ArrayList<Location> locations;

    public Result(ArrayList<Location> locations) {
        this.locations = locations;
    }


    public double getDistance() {
        double distance = 0;
        for (int i = 1; i < locations.size(); i++) {
            distance += locations.get(i - 1).distanceTo(locations.get(i));
        }
        return distance;
    }

    public double getUp() {
        double up = 0;
        for (int i = 1; i < locations.size(); i++) {
            Location location1, location2;
            location1 = locations.get(i - 1);
            location2 = locations.get(i);
            if (location2.getAltitude() > location1.getAltitude()) {
                up += (location2.getAltitude() - location1.getAltitude());
            }
        }
        return up;
    }

    public double getDown() {
        double down = 0;
        for (int i = 1; i < locations.size(); i++) {
            Location location1, location2;
            location1 = locations.get(i - 1);
            location2 = locations.get(i);
            if (location2.getAltitude() < location1.getAltitude()) {
                down += (location1.getAltitude() - location2.getAltitude());
            }
        }
        return down;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(locations);
    }

    public static final Parcelable.Creator<Result> CREATOR
            = new Parcelable.Creator<Result>() {
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public ArrayList<Location> getLocations() {
        return locations;
    }

    private Result(Parcel in) {
        locations = in.readArrayList(ArrayList.class.getClassLoader());
    }

}
