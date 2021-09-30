package org.eim_systems.privatetracker;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    public static final int RECORD_ON = 1;
    public static final int RECORD_PAUSE = 2;
    public static final int RECORD_OFF = 3;
    public static final int RECORD_STATUS_IN = 4;
    public static final int RECORD_STATUS_OUT = 5;
    public static final int RECORD_DATA_OUT = 6;
    public static final int ERROR = 0;

    private static volatile boolean on = false;
    private static volatile boolean active = false;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private static LinearRegression linearRegression;


    private static double distance = 0;


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        linearRegression = new LinearRegression();
        LocationListener locationListener = new LocationListener() {
            //todo use implementation of linear regression here
            @Override
            public void onLocationChanged(Location location) {
                if (on && active) {
                    linearRegression.appendLocation(location);
                    distance = linearRegression.getDistance();
                }
                Log.i(TAG, location.toString());
                //locations.add(location);
                Log.i(TAG, "accuracy:" + location.getAccuracy());
                Log.i(TAG, "current distance: " + distance);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //ACCURACY_COARSE or HIGH
        //criteria.setPowerRequirement(Criteria.POWER_MEDIUM); //or low?
        criteria.setAltitudeRequired(false);
        //todo enable altitude?
        //criteria.setCostAllowed(true);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        //todo https://stackoverflow.com/questions/3289039/google-maps-apps-with-mapview-have-different-current-positions
        String p = LocationManager.GPS_PROVIDER;
        p = locationManager.getBestProvider(criteria, false);
        LocationProvider locationProvider = locationManager.getProvider(p);
        if (!locationManager.isLocationEnabled() || !locationManager.isProviderEnabled(p)) {
            Log.d(TAG, "provider " + p + " not enabled or location is disabled");
        }
        Log.i(TAG, "provider:" + p);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //todo check minTime and minDistance values
        locationManager.requestLocationUpdates(p, 500, 0, locationListener);
    }


    private static class IncomingHandler extends Handler {
        private final String LOCAL_TAG = IncomingHandler.class.getSimpleName();

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECORD_ON:
                    Log.i(LOCAL_TAG, "RECORD_ON");
                    on = true;
                    active = true;
                    break;

                case RECORD_PAUSE:
                    if (!active) {
                        Messenger m = msg.replyTo;
                        Message msg2 = Message.obtain(null, ERROR);
                        try {
                            m.send(msg2);
                        } catch (RemoteException e) {
                            Log.e(LOCAL_TAG, e.getMessage());
                        }
                    }
                    Log.i(LOCAL_TAG, "RECORD_PAUSE");
                    active = false;
                    distance = linearRegression.getTotalDistance();
                    //todo
                    break;
                case RECORD_OFF:
                    Log.i(LOCAL_TAG, "RECORD_OFF");
                    if (!on) {
                        Messenger m = msg.replyTo;
                        Message msg2 = Message.obtain(null, ERROR);
                        try {
                            m.send(msg2);
                        } catch (RemoteException e) {
                            Log.e(LOCAL_TAG, e.getMessage());
                        }
                    }
                    active = false;
                    on = false;
                    distance = linearRegression.getTotalDistance();
                    try {
                        Messenger m = msg.replyTo;
                        Log.i(TAG, "RECORD_DATA_OUT");
                        Message msg2 = Message.obtain(null,  RECORD_DATA_OUT);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("obj", new Result(linearRegression.getLocations()));
                        msg2.setData(bundle);
                        m.send(msg2);
                    } catch (RemoteException e) {
                        Log.e(LOCAL_TAG, e.getMessage());
                    }
                case RECORD_STATUS_IN:
                    if (!active || !on) {
                        Messenger m = msg.replyTo;
                        Message msg2 = Message.obtain(null, ERROR);
                        try {
                            m.send(msg2);
                        } catch (RemoteException e) {
                            Log.e(LOCAL_TAG, e.getMessage());
                        }
                    }
                    Log.i(LOCAL_TAG, "RECORD_STATUS_IN");

                    Messenger m = msg.replyTo;
                    Message msg2 = Message.obtain(null, RECORD_STATUS_OUT, distance);
                    try {
                        m.send(msg2);
                    } catch (RemoteException e) {
                        Log.e(LOCAL_TAG, "send()", e);
                    }
                default:
                    Log.i(LOCAL_TAG, "default");
                    super.handleMessage(msg);
                    break;
            }
        }

    }
}