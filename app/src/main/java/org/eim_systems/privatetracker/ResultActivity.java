package org.eim_systems.privatetracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class ResultActivity extends Activity {
    private static final String TAG = ResultActivity.class.getSimpleName();
    Result result;
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
        result = (Result)getIntent().getSerializableExtra("result");
        if(result == null){
            System.out.println("result data is null");
            Log.e(TAG, "result is null");
        }else {
            System.out.println("data in ResultActivity retrieved");
            distance_tv.setText(result.getDistance()+"");
            up_tv.setText(result.getUp()+"");
            down_tv.setText(result.getDown()+"");
        }



    }

}