package com.victor.forevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText email_reset;
    Button btn_reset;

    private String email = "";
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        email_reset = findViewById(R.id.email_reset);
        btn_reset = findViewById(R.id.reset_button);
        progressBar = findViewById(R.id.progressBar);


        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = email_reset.getText().toString();

                if(!email.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword();
                }else{
                    Toast.makeText(ResetPasswordActivity.this,"Debes introducir un email", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void resetPassword() {

        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this,"Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(ResetPasswordActivity.this,"No se puedo enviar el correo de restablecer contraseña", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }
}
