package org.eim_systems.privatetracker;

import android.location.Location;

import org.jetbrains.annotations.TestOnly;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class LinearRegression {
    /**
     * @param max is the number where linear regression is applied to
     * @param min is the minimal number of locations required to process the linear regression
     */
    private static final int max = 10;
    private static final int min = 5;
    private final ArrayList<Location> processingQueue = new ArrayList<>();
    private final ArrayList<Location> locations = new ArrayList<>();

    //test only
    private final ArrayList<Location> debugList = new ArrayList<>();
    /**
     * @param a linear regression parameter for latitude
     * @param b linear regression parameter for longitude
     */
    private double a;
    private double b;

    public synchronized void appendLocation(Location location) {
        processingQueue.add(location);
        process();
    }

    public synchronized List<Location> getLocations() {
        //todo change to copy
        //return Collections.unmodifiableList(locations);
        return locations;
    }

    private void process() {
        /**
         * trim the list to length max and add the elements to the locations list
         */
        double n = processingQueue.size();
        System.out.println("processingQueue size: " + n);
        if (n < min) {
            return;
        }
        if (n >= max && n >= min) {
            apply((int) n - max);
        }
        /**
         * calculate the average of x(latitude) and y (longitude) used in linear regression
         * then calculate a and b used for correction
         */
        //todo: calculate the average of x and y, calculate a and  b
        double x, y;
        double sumLat = 0;
        double sumLon = 0;

        //override
        n = processingQueue.size();

        for (int i = 0; i < n; i++) {
            Location lo = processingQueue.get(i);
            sumLat += lo.getLatitude();
            sumLon += lo.getLongitude();
        }
        x = (1 / n) * sumLat;
        y = (1 / n) * sumLon;

        double xe = 0;
        double xy = 0;
        for (int i = 0; i < n; i++) {
            Location lo = processingQueue.get(i);
            xy += lo.getLatitude() * lo.getLongitude();
            xe += Math.pow(lo.getLatitude(), 2);
        }
        double xs = n * Math.pow(x, 2);
        double xes = xe - xs;
        if (xs == xe) {
            System.err.println("xs == xe : " + xs);
            return;
        }
        a = (xy - n * x * y) / (xes);
        b = y - a * x;
        System.out.println("\n a: " + a);
        System.out.println("b: " + b + "\n");
    }

    public double getDistance() {
        double distance = 0;
        for (int i = 1; i < locations.size(); i++) {
            distance += locations.get(i - 1).distanceTo(locations.get(i));
        }
        return distance;
    }
    private void apply(int m){
        for (int i = 0; i < m; i++) {
            Location location = processingQueue.remove(0);
            System.out.println("removed elem: \n :" + location.toString());
            System.out.println("longitude: " + location.getLongitude());
            System.out.println("latitude: " + location.getLatitude());
            Location location1 = new Location(location);

            location1.setLongitude(location.getLatitude() * a + b);
            location1.setLatitude(location.getLatitude());
            System.out.println("optimization: \n");
            System.out.println("longitude: " + location.getLongitude());
            System.out.println("latitude: " + location.getLatitude());
            System.out.println("distance between origin and optimized: " + location.distanceTo(location1));
            locations.add(location1);
            debugList.add(location);
        }
    }

    public double getTotalDistance() {
        /**
         first processes the complete processingQueue, applying linear regression and then returning the total distance
         */
        process();
        if (a == 0 && b == 0){
            return 0;
        }

        apply(processingQueue.size());
        double distanceOpt = getDistance();
        double distanceOri = getDebugDistance();
        System.out.println("\n \n distance between original Locations: \n" + distanceOri + " \n distance between optimized locations: \n" + distanceOpt + "\n total diff:" + (distanceOri - distanceOpt) );
        return distanceOpt;
    }

    private double getDebugDistance(){
        double distance = 0;
        for (int i = 1; i < debugList.size(); i++) {
            distance += debugList.get(i - 1).distanceTo(debugList.get(i));
        }
        return distance;
    }
}
