package org.eim_systems.privatetracker;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultActivity extends Activity {
    ArrayList<Location> locations;
    TextView distance_tv;
    TextView up_tv;
    TextView down_tv;


    //todo implement store data persistent with GPX.class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        distance_tv = findViewById(R.id.result_distance);
        up_tv = findViewById(R.id.result_up);
        down_tv = findViewById(R.id.result_down);

    }
    @Override
    protected void onStart() {
        super.onStart();
        locations = (ArrayList<Location> )getIntent().getSerializableExtra("locations");
        System.out.println("data in ResultActivity retrieved");
        distance_tv.setText(getDistance()+"");
        up_tv.setText(getUp()+"");
        down_tv.setText(getDown()+"");


    }
    private double getDistance(){
        double distance = 0;
        for(int i=1; i < locations.size(); i++){
            distance += locations.get(i - 1).distanceTo(locations.get(i));
        }
        return distance;
    }
    private double getUp(){
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
    private double getDown(){
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