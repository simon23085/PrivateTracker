package org.eim_systems.privatetracker;

import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.resources.TextAppearanceFontCallback;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eim_systems.privatetracker.databinding.ActivityStoredBinding;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class StoredActivity extends AppCompatActivity {

    private ActivityStoredBinding binding;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStoredBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());
        linearLayout = findViewById(R.id.verticalLayout);
        addRows();
    }

    private void addRows(){
        PersistenceManager.init(getApplicationContext());
        Cursor cursor = PersistenceManager.getInstance().getCursor();
        for(cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToNext()){
            ConstraintLayout child = (ConstraintLayout) getLayoutInflater().inflate(R.layout.track, null);
            linearLayout.addView(child);

            TextView name = findViewById(R.id.nameField);
            int nameIndex = cursor.getColumnIndexOrThrow("name");
            name.setText(cursor.getString(nameIndex));
            TextView distance = findViewById(R.id.distanceTv);
            int distanceIndex = cursor.getColumnIndexOrThrow("distance");
            distance.setText(cursor.getString(distanceIndex));
            TextView upTv = findViewById(R.id.upTv);
            int upIndex = cursor.getColumnIndexOrThrow("up");
            upTv.setText(cursor.getString(upIndex));
            TextView downTv = findViewById(R.id.downTv);
            int downIndex = cursor.getColumnIndexOrThrow("down");
            downTv.setText(cursor.getString(downIndex));
            TextView dateTv = findViewById(R.id.dateTv);
            int dateIndex = cursor.getColumnIndexOrThrow("date");
            long datel = cursor.getLong(dateIndex);
            Date date = new Date(datel);
            DateFormat dateFormat = DateFormat.getDateInstance();
            dateTv.setText(dateFormat.format(datel));
        }
    }
}