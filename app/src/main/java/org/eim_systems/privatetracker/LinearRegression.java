package org.eim_systems.privatetracker;

import android.location.Location;

import java.util.ArrayList;


/**
 * max is the number where linear regression is applied to
 * min is the minimal number of locations required to process the linear regression
 * a linear regression parameter for latitude
 * b linear regression parameter for longitude
 */
public class LinearRegression {

    private static final int max = 10;
    private static final int min = 5;
    private final ArrayList<Location> processingQueue = new ArrayList<>();
    private final ArrayList<Location> locations = new ArrayList<>();

    //test only
    private final ArrayList<Location> debugList = new ArrayList<>();
    private double a;
    private double b;

    public synchronized void appendLocation(Location location) {
        processingQueue.add(location);
        process();
    }

    public synchronized ArrayList<Location> getLocations() {
        //return Collections.unmodifiableList(locations);
        return locations;
    }

    /**
     * trim the list to length max and add the elements to the locations list,
     * then calculate the average of x(latitude) and y (longitude) used in linear regression,
     * after this, calculate a and b used for linear regression
     */
    private void process() {

        double n = processingQueue.size();
        System.out.println("processingQueue size: " + n);
        if (n < min) {
            return;
        }
        if (n >= max && n >= min) {
            apply((int) n - max);
        }
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

    public synchronized double getDistance() {
        double distance = 0;
        for (int i = 1; i < locations.size(); i++) {
            distance += locations.get(i - 1).distanceTo(locations.get(i));
        }
        return distance;
    }

    private void apply(int m) {
        for (int i = 0; i < m; i++) {
            Location location = processingQueue.remove(0);
            System.out.println("removed elem: \n :" + location.toString());
            System.out.println("longitude: " + location.getLongitude());
            System.out.println("latitude: " + location.getLatitude());
            Location location1 = new Location(location);

            location1.setLongitude(location.getLatitude() * a + b);
            location1.setLatitude(location.getLatitude());
            location1.setAltitude(location.getAltitude());
            System.out.println("optimization: \n");
            System.out.println("longitude: " + location.getLongitude());
            System.out.println("latitude: " + location.getLatitude());
            System.out.println("distance between origin and optimized: " + location.distanceTo(location1));
            locations.add(location1);
            debugList.add(location);
        }
    }

    /**
     * first processes the complete processingQueue,
     * applying linear regression and then returning the total distance
     */
    public synchronized double getTotalDistance() {

        process();
        if (a == 0 && b == 0) {
            return 0;
        }

        apply(processingQueue.size());
        double distanceOpt = getDistance();
        double distanceOri = getDebugDistance();
        System.out.println("\n \n distance between original Locations: \n" + distanceOri + " \n distance between optimized locations: \n" + distanceOpt + "\n total diff:" + (distanceOri - distanceOpt));
        return distanceOpt;
    }

    private double getDebugDistance() {
        double distance = 0;
        for (int i = 1; i < debugList.size(); i++) {
            distance += debugList.get(i - 1).distanceTo(debugList.get(i));
        }
        return distance;
    }
}
