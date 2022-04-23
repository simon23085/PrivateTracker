package org.eim_systems.privatetracker;

import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eim_systems.privatetracker.databinding.ActivityStoredBinding;

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

        for(cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToLast()){
            ConstraintLayout child = (ConstraintLayout) getLayoutInflater().inflate(R.layout.track, linearLayout);
            linearLayout.addView(child);
        }
    }
}