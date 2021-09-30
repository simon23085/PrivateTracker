package org.eim_systems.privatetracker;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.MediumTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Factory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class MyServiceTest {
    private  Messenger mService;
    static Messenger  mMessenger;
    private static int status;
    private static boolean error = false;
    static {
        Looper.prepare();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = new Messenger(service);
            Looper.loop();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    {
        ApplicationProvider.getApplicationContext().bindService(new Intent(ApplicationProvider.getApplicationContext(), LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
        mMessenger = new Messenger(new IncomingHandler(ApplicationProvider.getApplicationContext()));
    }


    @Test
    public void testWithStartedService() {//bound not startedService
        //ApplicationProvider.getApplicationContext().startService(new Intent(ApplicationProvider.getApplicationContext(),LocationService.class));
        // Add your test code here.
    }

    /*@BeforeClass
    public void testWithBoundService() throws TimeoutException {
        ApplicationProvider.getApplicationContext().bindService(new Intent(ApplicationProvider.getApplicationContext(), LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
        mMessenger = new Messenger(new IncomingHandler(ApplicationProvider.getApplicationContext()));
    }*/

    @Test
    public synchronized void testOnOffService() {
        try {
            error = false;
            status = -1;
            recordOn();
            recordOff();
            //success
        }catch (RemoteException e){
            Assert.fail();
        }
    }

    @Test
    public synchronized void testOnPauseOnOff() {
        try {
            error = false;
            status = -1;
            recordOn();
            recordPause();
            recordOn();
            recordOff();
            //success
        } catch (RemoteException e){
            Assert.fail();
        }
    }

   /* @Test idiotic tests
    public synchronized void testOffOn(){g
        try{
            error = false;
            status = -1;
            recordOff();
            assertTrue(error);
            error = false;
        } catch(RemoteException e){
            //success
        }
    }
   @Test
    public synchronized void testIn(){g
        try{
            error = false;
            status = -1;
            recordIn();
            assertTrue(error);
            error = false;
        } catch(RemoteException e){
            //success
        }
    }
    @Test
    public synchronized void testOnInOff(){g
        try{
            error = false;
            status = -1;
            recordOn();
            assertFalse(error);
            error = false;
            recordIn();
            Thread.sleep(1000);
            assertEquals(LocationService.RECORD_STATUS_OUT, status);
            status = -1;
        } catch(RemoteException | InterruptedException e){
            Assert.fail();
        }
    }*/




    public void recordOn() throws RemoteException {
        Message msg = Message.obtain(null, LocationService.RECORD_ON);
        msg.replyTo = mMessenger;
        mService.send(msg);
    }


    public void recordOff() throws RemoteException {
        Message msg = Message.obtain(null, LocationService.RECORD_OFF);
        msg.replyTo = mMessenger;
        mService.send(msg);
    }

    public void recordPause() throws RemoteException {
        Message msg = Message.obtain(null, LocationService.RECORD_PAUSE);
        msg.replyTo = mMessenger;
        mService.send(msg);
    }


    public void recordIn() throws RemoteException {
        Message msg = Message.obtain(null, LocationService.RECORD_STATUS_IN);
        msg.replyTo = mMessenger;
        mService.send(msg);
    }

    private static class IncomingHandler extends Handler {
        private final Context ctx;

        private IncomingHandler(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocationService.RECORD_STATUS_OUT:
                    double d = (double) msg.obj;
                    //int i = (int) Math.round((d * 100));
                    //double dist = ((double) i) / 100.0;

                    //String s = ctx.getString(R.string.curr_distance, String.valueOf(dist), "m");  //Resources.getSystem() replaced by ctx
                        status = LocationService.RECORD_STATUS_OUT;
                    break;
                    case LocationService.ERROR:
                        error = true;
                        status = LocationService.ERROR;
                        break;
                default:
                    //not wanted!!
            }
        }
    }
}
