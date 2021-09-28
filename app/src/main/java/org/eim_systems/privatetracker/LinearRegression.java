package org.eim_systems.privatetracker;

import android.location.Location;

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
    private final List<Location> processingQueue = new ArrayList<>();
    private final List<Location> locations = new ArrayList<>();
    /**
     * @param a linear regression parameter for latitude
     * @param b linear regression parameter for longitude
     */
    private double a;
    private double b;
    public synchronized void appendLocation(Location location){
        //todo add location to processingQueue
    }
    public synchronized List<Location> getLocations(){
        //todo change to copy
        return Collections.unmodifiableList(locations);
    }
    private void process (){
        /**
         * trim the list to length max and add the elements to the locations list
         */
        double n = processingQueue.size();
        if(n < min){
            return;
        }
        if(n > max){
            int m = (int) n - max;
            for(int i = 0; i < m; i++){
                Location location = processingQueue.remove(0);
                location.setLongitude(location.getLatitude() * a + b);
                location.setLatitude(location.getLatitude() * a);
                locations.add(location);
            }
        }
        /**
         * calculate the average of x(latitude) and y (longitude) used in linear regression
         * then calculate a and b used for correction
         */
        //todo: calculate the average of x and y, calculate a and  b
        double x, y;
        double sumLat = 0;
        double sumLon = 0;

        for(int i = 0; i < processingQueue.size(); i++){
            Location lo = processingQueue.get(i);
            sumLat += lo.getLatitude();
            sumLon += lo.getLongitude();
        }
        x =  (1/n) *  sumLat;
        y =  (1/n) * sumLon;

        double xe = 0;
        double xy = 0;
        for(int i = 0; i < processingQueue.size(); i++){
            Location lo = processingQueue.get(i);
            xy += lo.getLatitude() *lo.getLongitude();
            xe += Math.pow(lo.getLatitude(), 2);
        }
        double xs = n * Math.pow(x, 2);
        double xes = xe - xs;
        if(xs == xe){
            //todo raise exception
            throw new IllegalArgumentException();
        }
        a = (xy - n * x * y) / (xes);
        b = y - a * x;
    }
}
