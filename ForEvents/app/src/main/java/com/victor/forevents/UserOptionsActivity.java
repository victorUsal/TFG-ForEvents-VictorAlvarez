package com.victor.forevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.victor.forevents.model.User;

import java.util.HashMap;

public class UserOptionsActivity extends AppCompatActivity {

    MaterialEditText email, username, bio;
    TextView logout,change_password,delete_account;
    Button save;
    ImageButton back;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_options);


        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        logout = findViewById(R.id.logout);
        save = findViewById(R.id.button_save_edit_profile);
        change_password = findViewById(R.id.change_password);
        delete_account = findViewById(R.id.delete_account);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.ic_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_password();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserOptionsActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(email.getText().toString(),username.getText().toString(),bio.getText().toString());
            }
        });


        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                email.setText(user.getEmail());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void change_password() {
        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(firebaseUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(UserOptionsActivity.this,"Se ha enviado un correo para cambiar la contraseña", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UserOptionsActivity.this,"No se puedo enviar el correo para cambiar la contraseña", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void updateProfile(String email, String username, String bio){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("email",email);
        hashMap.put("username",username.toLowerCase());
        hashMap.put("bio",bio);

        reference.updateChildren(hashMap);
    }

}
