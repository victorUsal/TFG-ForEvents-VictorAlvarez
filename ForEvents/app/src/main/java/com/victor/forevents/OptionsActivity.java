package com.victor.forevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.victor.forevents.model.Tokens;
import com.victor.forevents.model.User;

public class OptionsActivity extends AppCompatActivity {

    TextView cuenta_btn, settings, preferencias,acerca;
    CheckBox checkBox;
    ImageButton back;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        cuenta_btn = findViewById(R.id.cuenta_btn);
        checkBox = findViewById(R.id.notificacion_ch);
        preferencias = findViewById(R.id.preferencias);
        acerca = findViewById(R.id.acerca);

        back = findViewById(R.id.ic_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cuenta_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this,UserOptionsActivity.class));
            }
        });

        preferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this,PreferenceActivity.class));
            }
        });

        acerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this,ForEventsActivity.class));
            }
        });

        isCheck();
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar();
            }
        });

    }

    private void validar() {
            if(checkBox.isChecked()){
                recuperarToken(firebaseUser.getUid());
            }else{
                vaciarToken();
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

    private void vaciarToken() {
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(firebaseUser.getUid()).child("token").setValue("");
    }

    private void isCheck(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tokens").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tokens tokens = dataSnapshot.getValue(Tokens.class);
                if(!tokens.getToken().isEmpty()){
                    checkBox.setChecked(true);
                }else{
                    checkBox.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
