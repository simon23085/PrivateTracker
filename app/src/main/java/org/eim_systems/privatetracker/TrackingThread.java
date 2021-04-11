package org.eim_systems.privatetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.LinkedList;

public class TrackingThread extends Thread {
    private final String TAG = TrackingThread.class.getSimpleName();
    private LinkedList<Location> locationList;

    //default interval is 5 (seconds)
    private long interval = 5;
    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private LocationListener listener;
    private String p;

    //private LocationService service;
    private Context ctx;


    public TrackingThread(long l, Context ctx) {
        interval = l;
        this.ctx = ctx;
        //this.service = locationService;
    }

    public TrackingThread (Context ctx) {
        this.ctx = ctx;
        //this.service = locationService;
    }

    @SuppressLint("MissingPermission")
    public void run() {
        Log.i(TAG, "Tracking Thread run");
        locationList = new LinkedList<>();
        locationManager = ctx.getSystemService(LocationManager.class);
        if (!locationManager.isLocationEnabled()) {
            Log.e(TAG, "location is not enabled!!");
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        p = locationManager.getBestProvider(criteria, true);
        locationProvider = locationManager.getProvider(p);

        locationList.add(locationManager.getLastKnownLocation(p));
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(LocationService.isActive()) {
                    double d = location.distanceTo(locationList.getLast());
                    LocationService.addDistance(d);
                    locationList.addLast(location);
                    Log.i(TAG, "location: \n " + location.toString() + " \n");
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //nothing?
            }

            @Override
            public void onProviderEnabled(String s) {
                //start
            }

            @Override
            public void onProviderDisabled(String s) {
                //ähm problem
            }
        };
        locationManager.requestLocationUpdates(p, interval * 900, 0, listener);

       /* while (LocationService.isOn()) {
            //keep running until service is stopped
            while (LocationService.isActive()) {
                //tracking active //interval * 900 formally
                try {
                    wait(interval); //interval * 1000
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }*/


    }
}