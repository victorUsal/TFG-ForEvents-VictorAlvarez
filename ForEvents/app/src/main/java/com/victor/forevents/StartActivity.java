package com.victor.forevents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button start_button;
    TextView login_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAuth = FirebaseAuth.getInstance();

        start_button = (Button)findViewById(R.id.start_button);
        login_tv = (TextView) findViewById(R.id.iniciar_sesion_tv);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(StartActivity.this,SignupActivity.class));
            }
        });


        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(StartActivity.this,LoginActivity.class));
            }
        });
    }

    //Si existe un usuario con sesión iniciada partir desde su HomeActivity
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    private void updateUI(FirebaseUser user) {
        if(user != null && !user.getUid().equals("JB0Ws2FpxtX4W1nhKaO1CXtxjLz1")){
           // Toast.makeText(StartActivity.this,user.getEmail(),Toast.LENGTH_LONG).show();
            startActivity(new Intent(StartActivity.this, HomeActivity.class));
            finish();
        }
    }


}