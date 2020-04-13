package com.victor.forevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewQuestionSignup;
    private Button buttonLogin;
    private ProgressBar progressBar;

    private String email = "";
    private String password = "";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = (Button)findViewById(R.id.login_button);

        editTextEmail = (EditText)findViewById(R.id.email_login_et);
        editTextPassword=(EditText)findViewById(R.id.password_login_tv);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        textViewQuestionSignup = (TextView) findViewById(R.id.question_signup_tv);
        firebaseAuth = FirebaseAuth.getInstance();


        textViewQuestionSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();

                if(email.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Vacio Email...",Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Vacio password...",Toast.LENGTH_LONG).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    buttonLogin.setVisibility(View.INVISIBLE);
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    Toast.makeText(LoginActivity.this,"No fue posible ...",Toast.LENGTH_LONG).show();

                                }

                            }
                        });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user) {
        if(user != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();

        }
    }
}
