package com.victor.forevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private ProgressBar progressBar;

    //Variable de los datos que vamos a registrar
    private String username = "";
    private String name = "";
    private String email = "";
    private String password = "";

    FirebaseAuth firebaseAuth;
    DatabaseReference dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        firebaseAuth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();

        editTextUsername=(EditText)findViewById(R.id.username_signup_et);
        editTextName =(EditText)findViewById(R.id.name_signup_et);
        editTextEmail=(EditText)findViewById(R.id.email_signup_et);
        editTextPassword=(EditText)findViewById(R.id.password_signup_et);
        buttonSignup=(Button)findViewById(R.id.signup_button);
        progressBar =(ProgressBar)findViewById(R.id.progressBar);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editTextUsername.getText().toString();
                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if(!username.isEmpty() && !name.isEmpty() && !email.isEmpty() && !password.isEmpty()){

                    if(password.length() >= 6){
                        registerUser();
                    }else{
                        Toast.makeText(SignupActivity.this,"Cadena de 6 caracteres",Toast.LENGTH_SHORT).show();

                    }

                }else{
                    Toast.makeText(SignupActivity.this,"Debe completar los campos",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(){

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String id = firebaseAuth.getCurrentUser().getUid();

                    Map<String,Object> map = new HashMap<>();
                    map.put("id",id);
                    map.put("username",username.toLowerCase());
                    map.put("name",name);
                    map.put("email",email);
                    map.put("bio","");
                    map.put("imagen", "https://firebasestorage.googleapis.com/v0/b/forevents-2c0e8.appspot.com/o/user.png?alt=media&token=69b0c7ee-4a5d-43b3-9d0e-8775e633acc3" );


                    dataBase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                progressBar.setVisibility(View.VISIBLE);
                                buttonSignup.setVisibility(View.INVISIBLE);
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                recuperarToken(user.getUid());
                                updateUI(user);
                            }else{
                                Toast.makeText(SignupActivity.this,"No se añadio a la base de datos",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }else{
                    Toast.makeText(SignupActivity.this,"Debe completar los campos",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void updateUI(FirebaseUser user) {
        if(user != null && !user.getUid().equals("JB0Ws2FpxtX4W1nhKaO1CXtxjLz1")){
            startActivity(new Intent(SignupActivity.this, PreferenceActivity.class));
            finish();
        }
    }

    private void recuperarToken(final String userid) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        guardarToken(token,userid);
                    }
                });
    }

    private void guardarToken(String s,String userid) {

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(userid).child("token").setValue(s);

    }
    
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

}
