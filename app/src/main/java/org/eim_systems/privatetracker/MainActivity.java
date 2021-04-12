package org.eim_systems.privatetracker;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 123;
    private Messenger mService = null;

    private boolean started = false;
    private boolean pause = true;

    //todo connect service


    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.i(TAG, "OnServiceConnected()");
            mService = new Messenger(service);
            started = false;
            pause = true;
            Log.i(TAG, "is mService not null?" + String.valueOf( mService != null));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            started = false;
            pause = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        }
        final Button start_stop = findViewById(R.id.start_stop);
        final Button pause_resume = findViewById(R.id.pause_resume);
        TextView textView  =  findViewById(R.id.distance_textview);
        pause_resume.setEnabled(false);


        final Messenger mMessenger = new Messenger(new IncomingHandler(this, textView));


        start_stop.setOnClickListener(v-> {
            Log.i(TAG, "start_stop.setOnClickListener();\n");
            Log.i(TAG, "is mService not null?" + String.valueOf( mService != null));
            if(mService !=null){
                if(!started){
                    //start it
                    started = true;
                    pause = false;
                    try {
                        Message msg = Message.obtain(null, LocationService.RECORD_ON);
                        msg.replyTo = mMessenger;
                        mService.send(msg);
                        runOnUiThread(() -> {
                            Log.i(TAG, "UiThread, called from start_stop; started was false, now true \n" );
                            start_stop.setText(getString(R.string.stop));
                        });
                        pause_resume.setEnabled(true);
                    } catch (RemoteException e){
                        Log.e(TAG, e.getMessage());
                    }


                }else{ //started ==true
                    //stop it
                    started = false;
                    pause = true;
                    try {
                        Message msg = Message.obtain(null, LocationService.RECORD_OFF);
                        msg.replyTo = mMessenger;
                        mService.send(msg);
                        runOnUiThread(() -> {
                            Log.i(TAG, "UiThread, called from start_stop, started was true, now false");
                            start_stop.setText(getString(R.string.start));
                        });
                        pause_resume.setEnabled(false);
                        Intent intent =  new Intent(this, ResultActivity.class);
                       /* Log.d(TAG, "unbindService()");
                        if(mService!=null){
                            unbindService(mConnection);
                            mService = null;
                        }*/
                        startActivity(intent);
                    } catch (RemoteException e){
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
        pause_resume.setOnClickListener(v-> {
            Log.i(TAG, "pause_resume.setOnClickListener() \n");
            Log.i(TAG, "is mService not null?" + String.valueOf( mService != null));
            if(mService !=null){
                if(pause){
                    //start it
                    try {
                        Message msg = Message.obtain(null, LocationService.RECORD_ON);
                        msg.replyTo = mMessenger;
                        mService.send(msg);
                        pause = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pause_resume.setText(getString(R.string.pause));
                            }
                        });

                    } catch (RemoteException e){
                        Log.e(TAG, e.getMessage());
                    }


                }else{
                    //pause it
                    try {
                        Message msg = Message.obtain(null, LocationService.RECORD_PAUSE);
                        msg.replyTo = mMessenger;
                        mService.send(msg);
                        pause = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pause_resume.setText(getString(R.string.resume));
                            }
                        });

                    } catch (RemoteException e){
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
        //keeps the distance field up to date
        //todo rewrite updater as handler
        //final Handler h =  new Handler();
        new Thread(()->{
            while(true){
                try {
                    sleep(1000);
                    if(mService!=null) {
                        //Log.i(TAG, "is mService not null?" + String.valueOf(mService != null));
                        Message msg = Message.obtain(null, LocationService.RECORD_STATUS_IN);
                        msg.replyTo = mMessenger;
                        mService.send(msg);
                    }
                } catch (RemoteException | InterruptedException e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");

        if(mService!=null){
            unbindService(mConnection);
            mService = null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    private static class IncomingHandler extends Handler {
        private final Context ctx;
        private final TextView tv;
        private IncomingHandler(Context ctx, TextView tv){
            this.ctx = ctx;
            this.tv  =  tv;
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case LocationService.RECORD_STATUS_OUT:
                    Log.i(TAG, "RECORD_STATUS_OUT");
                    double d = (double) msg.obj;
                    Log.i(TAG, String.valueOf(d));
                    int i = (int) Math.round((d*100));
                    Log.i(TAG, String.valueOf(i));
                    double dist = ((double) i)/100.0;
                    Log.i(TAG, String.valueOf(dist));
                    String s = ctx.getString(R.string.curr_distance, String.valueOf(dist), "m");  //Resources.getSystem() replaced by ctx
                    Log.i(TAG, s);
                    tv.setText(s);
                    break;
                default:
                    Log.i(TAG, "default" + msg.what + " \n ");
                    super.handleMessage(msg);
            }
        }
    }
   /* class Updater implements Runnable {

        @Override
        public void run() {
            Log.i(Updater.class.getSimpleName(), "run");

        }
    }*/
}