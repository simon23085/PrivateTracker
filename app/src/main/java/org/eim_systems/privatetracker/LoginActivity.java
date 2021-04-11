package org.eim_systems.privatetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = new Intent(this, MainActivity.class);
        final Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(v->{
            startActivity(intent);
        });

    }
}
