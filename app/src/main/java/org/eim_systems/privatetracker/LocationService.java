package org.eim_systems.privatetracker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
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

import androidx.annotation.NonNull;

import java.util.LinkedList;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    static TrackingThread trackingThread;
    public static final int RECORD_ON = 1;
    public static final int RECORD_PAUSE = 2;
    public static final int RECORD_OFF = 3;
    public static final int RECORD_STATUS_IN = 4;
    public static final int RECORD_STATUS_OUT = 5;
    private static double distance = 0;
    //service is running
    private static volatile boolean on = false;
    //status status about tracking or pause
    private static volatile boolean active = false;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());


    @Override
    public IBinder onBind(Intent intent) {
        trackingThread = new TrackingThread(getApplicationContext());
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private static class IncomingHandler extends Handler {
    private final String LOCAL_TAG = IncomingHandler.class.getSimpleName();
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case RECORD_ON:
                    Log.i(LOCAL_TAG, "RECORD_ON");
                    on = true;
                    active = true;
                    trackingThread.start();
                    break;

                case RECORD_PAUSE:
                    Log.i(LOCAL_TAG, "RECORD_PAUSE");
                    active = false;
                    break;
                case RECORD_OFF:
                    Log.i(LOCAL_TAG, "RECORD_OFF");
                    saveRecord();
                    active = false;
                    on = false;
                case RECORD_STATUS_IN:
                    Log.i(LOCAL_TAG, "RECORD_STATUS_IN");

                    Messenger m = msg.replyTo;
                    Message msg2 = Message.obtain(null,RECORD_STATUS_OUT, distance );
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

        private void saveRecord() {
            //todo implement saveRecord (persistent)

        }
    }
    public synchronized void setOn(){
        on = true;
        active = true;
    }
    public synchronized  void setOff(){
        on = false;
        active = false;
        //todo save LocationList + metadata
        stopSelf();
    }
    public static synchronized void setPause(){
        active = false;
    }
    public static synchronized void setResume(){
        active = true;
    }
    public static synchronized void addDistance(double d){
        distance = distance + d;
    }

    public static synchronized boolean isOn() {
        return on;
    }

    public static synchronized boolean isActive() {
        return active;
    }
}