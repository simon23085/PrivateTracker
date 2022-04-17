package org.eim_systems.privatetracker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;


public class ResultActivity extends Activity {
    private static final String TAG = ResultActivity.class.getSimpleName();
    private static final int SELECT_FILE = 4711;
    Result result;
    TextView distance_tv;
    TextView up_tv;
    TextView down_tv;
    Button saveBtn;
    EditText editText;
    Button exportBtn;
    private String name;


    //todo implement store data persistent with GPX.class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        distance_tv = findViewById(R.id.result_distance);
        up_tv = findViewById(R.id.result_up);
        down_tv = findViewById(R.id.result_down);
        saveBtn = findViewById(R.id.saveBtn);
        editText = findViewById(R.id.editTextName);
        exportBtn = findViewById(R.id.exportBtn);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== SELECT_FILE && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            GPX.write(new File(uri.getPath()), name, result.getLocations());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        result = (Result) getIntent().getParcelableExtra("result");
        if (result == null) {
            System.out.println("result data is null");
            Log.e(TAG, "result is null");
            saveBtn.setEnabled(false);
        } else {
            System.out.println("data in ResultActivity retrieved");
            distance_tv.setText(round(result.getDistance()));
            up_tv.setText(round(result.getUp()));
            down_tv.setText(round(result.getDown()));

            saveBtn.setOnClickListener(v ->{
                PersistenceManager persistenceManager = PersistenceManager.init(getApplicationContext());
                String name = editText.getText().toString();
                if(name.isEmpty()){
                    Log.d(TAG, "name is null when saveBtn is pressed");
                    Toast.makeText(getApplicationContext(), getText(R.string.enter_name), Toast.LENGTH_SHORT).show();
                }else {
                    MetaData metaData = new MetaData(name, new Date(System.currentTimeMillis()), result.getDistance(), result.getUp(), result.getDown());
                    persistenceManager.insertRow(metaData);
                }

            });
            exportBtn.setOnClickListener(v -> {
                name = editText.getText().toString();
                if(name.isEmpty()){
                    Log.d(TAG, "name is null when exportGPX is pressed");
                    Toast.makeText(getApplicationContext(), getText(R.string.enter_name), Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/**");
                    intent.putExtra(Intent.EXTRA_TITLE, "trackname.gpx");
                    startActivityForResult(intent,SELECT_FILE );
                }

            });
        }


    }
    private String round(double d){
        if (d>=1000){
            return ( ((double) Math.round(d))/1000 + " km");
        }else{
            double l = Math.round(d*100);
            return (l/100) + " m";
        }
    }

}