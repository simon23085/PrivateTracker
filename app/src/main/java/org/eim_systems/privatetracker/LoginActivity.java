package org.eim_systems.privatetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * min sdk version 23 for fingerprint, if lowering it, need to check dynamic if fingerprint is available by checking running sdk version
 */
public class LoginActivity extends Activity {
    Button login_button;
    EditText editText;
    TextView fingerprintTextView;
    ImageView fingerprintImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = new Intent(this, MainActivity.class);
        login_button = findViewById(R.id.login_button);
        editText = findViewById(R.id.password_field);
        //editText.setVisibility(View.INVISIBLE);
        fingerprintTextView = findViewById(R.id.fingerprintTextView);
        fingerprintImageView = findViewById(R.id.fingerprintImageView);
        //todo check whether the system has a fingerprint sensor
        /*
        if not has fingerprint sensor then set fingerprintImageView to invisible and also the text
         */
        //todo https://www.androidauthority.com/how-to-add-fingerprint-authentication-to-your-android-app-747304/
        //todo https://developer.android.com/training/articles/keystore


        login_button.setOnClickListener(v->{
            startActivity(intent);
        });


    }
}
